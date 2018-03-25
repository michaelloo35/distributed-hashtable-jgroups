package pl.edy.agh.iet.sr.hashtable;

public interface SimpleStringMap {

    boolean containsKey(String key);

    String get(String key);

    String put(String key, String value);

    String remove(String key);
}
