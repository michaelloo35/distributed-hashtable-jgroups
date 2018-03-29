package pl.edy.agh.iet.sr.hashtable;

import org.jgroups.*;
import org.jgroups.util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class DistributedMapReceiver extends ReceiverAdapter {

    private static final int KEY_INDEX = 1;
    private static final int VALUE_INDEX = 2;
    private static final int OPERATION_INDEX = 0;

    private final Map<String, String> map;
    private final JChannel jChannel;

    public DistributedMapReceiver(Map<String, String> map, JChannel jChannel) {

        this.map = map;
        this.jChannel = jChannel;
    }

    @Override
    public void receive(Message msg) {
        handleReceive(msg);

        System.out.println("received msg from "
                + msg.getSrc() + ": "
                + msg.getObject());
    }

    private void handleReceive(Message msg) {
        String[] splittedString = new String(msg.getRawBuffer()).split("\\s");

        if (splittedString.length >= 2) {
            switch (splittedString[OPERATION_INDEX].substring(1)) {
                case "put":
                    if (splittedString.length != 3)
                        System.out.println("Received corrupted put message");
                    else
                        synchronized (map) {
                            map.put(splittedString[KEY_INDEX], splittedString[VALUE_INDEX]);
                        }
                    break;

                case "remove":
                    if (splittedString.length != 2)
                        System.out.println("Received corrupted remove message");
                    else
                        synchronized (map) {
                            map.remove(splittedString[KEY_INDEX]);
                        }
                    break;

                default:
                    System.out.println("Unrecognized received operation");
            }
        } else {
            System.out.println("Received corrupted message");
        }
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized (map) {
            Util.objectToStream(map, new DataOutputStream(output));
        }
    }

    @Override
    public void setState(InputStream input) throws Exception {
        // implementation synchronized on the state (so no incoming messages can modify it during the state transfer)
        synchronized (map) {
            map.clear();
            Map<String, String> coordinatorState = (Map<String, String>) Util.objectFromStream(new DataInputStream(input));
            map.putAll(coordinatorState);
            System.out.println("Synchronized state");
        }
    }

    @Override
    public void viewAccepted(View view) {
        handleView(view);
    }

    private void handleView(View view) {
        System.out.println(view.toString());
        if (view instanceof MergeView) {
            ViewHandler handler = new ViewHandler(jChannel, (MergeView) view);
            // requires separate thread as we don't want to block JGroups
            handler.start();
        }
    }

    private class ViewHandler extends Thread {
        JChannel ch;
        MergeView view;

        private ViewHandler(JChannel ch, MergeView view) {
            this.ch = ch;
            this.view = view;
        }

        // partitioning
        public void run() {
            List<View> subgroups = view.getSubgroups();
            View primaryPartitionView = subgroups.get(0); // picks the first
            Address localAddress = ch.getAddress();

            if (!primaryPartitionView.getMembers().contains(localAddress)) {
                System.out.println("Not member of the new primary partition ("
                        + primaryPartitionView + "), will re-acquire the state");
                try {
                    ch.getState(null, 30000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else {
                System.out.println("Member of the new primary partition ("
                        + primaryPartitionView + "), will do nothing");
            }
        }
    }
}
