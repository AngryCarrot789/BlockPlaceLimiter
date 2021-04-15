package reghzy.blocklimiter.utils.collections.multimap;

import java.util.Collection;

/**
 * A multimap is a collection of collections, but with functions to make managing that easier
 * <p>
 *     e.g. adding values, you only need to specify a key and value, the multimap
 *     will get the collection that the key points to for you, or create it if required
 * </p>
 * @param <K> The Key type
 * @param <V> The Value type (this doesn't have to be a collection, but it will be a collection in the background)
 */
public interface MultiMap<K,V> {
    boolean put(K key, V value);
    boolean putAll(K key, Collection<V> value);

    Collection<V> remove(K key);
    boolean remove(K key, V value);

    Collection<V> getValues(K key);
    Collection<K> getKeys();

    boolean contains(K key, V value);
    boolean containsKey(K key);
    boolean containsValue(V value);

    int keysSize();
    int valuesSize(K key);

    Collection<Collection<V>> getAllValues();
    Collection<MultiMapEntrySet<K, V>> getEntrySet();
}

// tbh... could've used the google guava multimap stuff, but i was missing
// some basic stuff, like getValues(key), and i got bored so why not :))
