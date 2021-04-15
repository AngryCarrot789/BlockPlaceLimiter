package reghzy.blocklimiter.utils.collections.multimap;

import java.util.Collection;

/**
 *
 * @param <K>
 * @param <V>
 */
public class MultiMapEntrySet<K,V> {
    private final K key;
    private final Collection<V> values;

    public MultiMapEntrySet(K key, Collection<V> values) {
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
