package reghzy.blocklimiter.utils.collections.multimap;

import java.util.*;

/**
 * A HashSetMultiMap is a collection of keys, which key to a hashset, but with functions to make managing that easier
 *
 * @param <K> The Key type
 * @param <V> The Value type (this doesn't have to be a collection, but it will be a hashset in the background)
 */
public class HashSetMultiMap<K, V> implements MultiMap<K, V> {
    private final HashMap<K, HashSet<V>> map;

    public HashSetMultiMap() {
        this.map = new HashMap<K, HashSet<V>>(12);
    }

    public boolean put(K key, V value) {
        return getOrCreateValues(key).add(value);
    }

    public boolean putAll(K key, Collection<V> values) {
        return getOrCreateValues(key).addAll(values);
    }

    public Collection<V> remove(K key) {
        getOrCreateValues(key).clear();
        return Arrays.asList((V[]) map.remove(key).toArray());
    }

    public boolean remove(K key, V value) {
        return getOrCreateValues(key).remove(value);
    }

    public HashSet<V> getValues(K key) {
        return getOrCreateValues(key);
    }

    public Collection<K> getKeys() {
        return map.keySet();
    }

    public boolean containsKey(K key) {
        return this.map.containsKey(key);
    }

    public boolean contains(K key, V value) {
        HashSet<V> values = map.get(key);
        return values != null && values.contains(value);
    }

    public boolean containsValue(V value) {
        for (K key : this.getKeys()) {
            if (getValues(key).contains(value))
                return true;
        }
        return false;
    }

    public int keysSize() {
        return this.map.size();
    }

    public int valuesSize(K key) {
        HashSet<V> values = map.get(key);
        if (values == null)
            return 0;
        return values.size();
    }

    public ArrayList<Collection<V>> getAllValues() {
        ArrayList<Collection<V>> valuesTotal = new ArrayList<Collection<V>>(this.map.size() * 4);
        for (K key : getKeys()) {
            valuesTotal.add(getValues(key));
        }
        return valuesTotal;
    }

    public Collection<MultiMapEntrySet<K, V>> getEntrySet() {
        Collection<MultiMapEntrySet<K, V>> entrySets = new ArrayList<MultiMapEntrySet<K, V>>(this.map.size());
        for (K key : this.getKeys()) {
            entrySets.add(new MultiMapEntrySet<K, V>(key, getOrCreateValues(key)));
        }
        return entrySets;
    }

    private HashSet<V> getOrCreateValues(K key) {
        HashSet<V> hashMap = map.get(key);
        if (hashMap == null) {
            hashMap = new HashSet<V>();
            map.put(key, hashMap);
        }

        return hashMap;
    }
}
