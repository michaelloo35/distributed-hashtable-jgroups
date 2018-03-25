package pl.edy.agh.iet.sr.hashtable;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;

import java.net.InetAddress;

public class DistrubtedNetworkChannel {

    private static final String MULTICAST_GROUP = "230.0.0.1";
    private final JChannel jChannel;

    public DistrubtedNetworkChannel(String clusterName, DistributedMapReceiver receiver) {

        jChannel = new JChannel(false);
        setupProtocolStack(jChannel);

        jChannel.setReceiver(receiver);

        connectToCluster(clusterName);

    }

    private void connectToCluster(String clusterName) {
        try {
            jChannel.connect(clusterName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(KeyValuePair keyValuePair) {

        Message msg = new Message(null, null, keyValuePair);

        try {
            jChannel.send(msg);
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
                    .addProtocol(new FLUSH())
                    .init();
        } catch (Exception e) {
            System.out.println("Error while initializing protocol stack");
        }

    }

}
