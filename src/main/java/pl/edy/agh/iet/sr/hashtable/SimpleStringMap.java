package pl.edy.agh.iet.sr.hashtable;

public interface SimpleStringMap {

    boolean containsKey(String key);

    String get(String key);

    String getState();

    void put(String key, String value);

    void remove(String key);
}
