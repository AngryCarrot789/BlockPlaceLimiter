package reghzy.blocklimiter.utils.collections.multimap;

import java.util.Collection;

/**
 * Contains an entry for a multimap; a key and the values
 * @param <K> The key type
 * @param <V> The value type
 */
public class MultiMapEntry<K,V> {
    private final K key;
    private final Collection<V> values;

    public MultiMapEntry(K key, Collection<V> values) {
        this.key = key;
        this.values = values;
    }

    public K getKey() {
        return key;
    }

    public Collection<V> getValues() {
        return values;
    }
}
