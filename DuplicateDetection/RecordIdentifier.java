package DuplicateDetection;

import java.util.ArrayList;
import java.util.Objects;

public class RecordIdentifier implements Comparable {
    private String key;
    private String id;
    private ArrayList<String> keyTokens;

    public RecordIdentifier(String key, String id) {
        this.key = key;
        this.id = id;
    }

    public RecordIdentifier(String key, String id, ArrayList<String> keyTokens) {
        this.key = key;
        this.id = id;
        this.keyTokens = keyTokens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordIdentifier ri = (RecordIdentifier) o;
        return this.key.equals(ri.key) && this.id.equals(ri.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, id);
    }

    @Override
    public String toString() {
        return key + "-" + id;
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        RecordIdentifier ri = (RecordIdentifier) o;
        if (this.key.compareTo(ri.key) == 0) return 0;
        else return (this.key.compareTo(ri.key) > 0) ? 1 : -1;
    }

    /**
     * Return record id
     * @return record id
     */
    public String getId() {
        return id;
    }

    /**
     * Return key signature of record
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * Return key as token list. The key tokens are the attribut parts from which the key is build
     * @return list of key tokens
     */
    public ArrayList<String> getKeyTokens() {
        return this.keyTokens;
    }
}
