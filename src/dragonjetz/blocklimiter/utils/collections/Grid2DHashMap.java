package dragonjetz.blocklimiter.utils.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * A simple class that uses 2 hash maps to store objects at specific coordinates in an integer-based grid
 * @param <V> The value to store at the given coordinates
 */
public class Grid2DHashMap<V> {
    private final HashMap<Integer, HashMap<Integer, V>> map;

    public Grid2DHashMap() {
        this.map = new HashMap<Integer, HashMap<Integer, V>>();
    }

    public V get(Integer x, Integer z) {
        HashMap<Integer, V> inner = map.get(x);
        if (inner == null) {
            return null;
        }

        return inner.get(z);
    }

    public V put(Integer x, Integer z, V value) {
        HashMap<Integer, V> inner = this.map.get(x);
        if (inner == null) {
            inner = new HashMap<Integer, V>();
            this.map.put(x, inner);
        }

        return inner.put(z, value);
    }

    public V remove(Integer x, Integer z) {
        HashMap<Integer, V> inner = this.map.get(x);
        if (inner == null) {
            return null;
        }

        return inner.remove(z);
    }

    public Collection<Collection<Integer>> getKeySet() {
        ArrayList<Collection<Integer>> keySet = new ArrayList<Collection<Integer>>();
        for(Integer x : this.map.keySet()) {
            keySet.add(new ArrayList<Integer>(this.map.get(x).keySet()));
        }
        return keySet;
    }

    public Collection<V> getAllValues() {
        ArrayList<V> values = new ArrayList<V>(256);
        for(HashMap<Integer, V> innerMap : this.map.values()) {
            values.addAll(innerMap.values());
        }
        return values;
    }
}
