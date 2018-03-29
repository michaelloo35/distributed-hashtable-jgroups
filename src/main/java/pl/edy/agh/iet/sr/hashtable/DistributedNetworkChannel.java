package pl.edy.agh.iet.sr.hashtable;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;

import java.net.InetAddress;
import java.util.Map;

public class DistributedNetworkChannel {

    private static final String MULTICAST_GROUP = "230.0.0.1";
    private final JChannel jChannel;

    public DistributedNetworkChannel(String channel, Map<String, String> map) {
        jChannel = new JChannel(false);
        setupProtocolStack(jChannel);
        setReceiver(map);
        connectToCluster(channel);
    }

    /**
     * @param stringMessage should be formed with convention "operation key value"
     */
    public void sendMessage(String stringMessage) {

        Message msg = new Message(null, null, stringMessage);
        try {
            jChannel.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setReceiver(Map<String, String> map) {
        jChannel.receiver(new DistributedMapReceiver(map, jChannel));
    }

    public JChannel getjChannel() {
        return jChannel;
    }

    private void connectToCluster(String clusterName) {
        try {
            jChannel.connect(clusterName);
            jChannel.getState(null, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setupProtocolStack(JChannel jChannel) {

        ProtocolStack protocolStack = new ProtocolStack();
        jChannel.setProtocolStack(protocolStack);
        try {
            protocolStack.addProtocol(new UDP().setValue("mcast_group_addr", InetAddress.getByName(MULTICAST_GROUP)))
                    .addProtocol(new PING())
                    .addProtocol(new MERGE3())
                    .addProtocol(new FD_SOCK())
                    .addProtocol(new FD_ALL()
                            .setValue("timeout", 12000)
                            .setValue("interval", 3000))
                    .addProtocol(new VERIFY_SUSPECT())
                    .addProtocol(new BARRIER())
                    .addProtocol(new NAKACK2())
                    .addProtocol(new UNICAST3())
                    .addProtocol(new STABLE())
                    .addProtocol(new GMS())
                    .addProtocol(new UFC())
                    .addProtocol(new MFC())
                    .addProtocol(new FRAG2())
                    .addProtocol(new STATE())
                    .addProtocol(new SEQUENCER())
                    .init();
        } catch (Exception e) {
            System.out.println("Error while initializing protocol stack");
        }

    }

}
