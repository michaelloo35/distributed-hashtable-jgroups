package pl.edy.agh.iet.sr.hashtable;

import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import java.io.InputStream;
import java.io.OutputStream;

public class DistributedMapReceiver extends ReceiverAdapter {

    public DistributedMapReceiver() {

    }

    @Override
    public void receive(Message msg) {
        super.receive(msg);
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        super.getState(output);
    }

    @Override
    public void setState(InputStream input) throws Exception {
        super.setState(input);
    }

    @Override
    public void viewAccepted(View view) {
        super.viewAccepted(view);
    }
}
