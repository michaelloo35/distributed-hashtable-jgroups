package pl.edy.agh.iet.sr.hashtable;

import java.util.HashMap;
import java.util.Map;

public class DistributedMap implements SimpleStringMap {

    private final DistributedNetworkChannel channel;
    private final Map<String, String> map;

    public DistributedMap(String channel) {
        this.map = new HashMap<>();
        this.channel = new DistributedNetworkChannel(channel, map);
    }

    @Override
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public String getState() {
        return map.toString();
    }

    @Override
    public void put(String key, String value) {
        channel.sendMessage("put" + " " + key + " " + value);
    }

    @Override
    public void remove(String key) {
        channel.sendMessage("remove" + " " + key);
    }
}
