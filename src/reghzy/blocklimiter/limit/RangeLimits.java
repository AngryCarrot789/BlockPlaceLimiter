package reghzy.blocklimiter.limit;

import reghzy.blocklimiter.track.utils.IntegerRange;
import reghzy.blocklimiter.track.utils.RangeLimit;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RangeLimits {
    private final Map<IntegerRange, RangeLimit> permissions;

    public RangeLimits(HashMap<IntegerRange, RangeLimit> permissions) {
        this.permissions = permissions;
    }

    public RangeLimits() {
        this.permissions = new HashMap<IntegerRange, RangeLimit>(4);
    }

    public RangeLimit getRangeLimit(IntegerRange range) {
        return this.permissions.get(range);
    }

    public Collection<Map.Entry<IntegerRange, RangeLimit>> getEntrySets() {
        return permissions.entrySet();
    }

    public void addPermission(IntegerRange range, RangeLimit permission) {
        this.permissions.put(range, permission);
    }

    @Nullable
    public RangeLimit getRangeLimit(int value) {
        for(Map.Entry<IntegerRange, RangeLimit> entry : this.permissions.entrySet()) {
            if (entry.getKey().isBetween(value)) {
                return entry.getValue();
            }
        }

        return null;
    }

    @Nullable
    public IntegerRange getCachedRange(int value) {
        for(IntegerRange range : this.permissions.keySet()) {
            if (range.isBetween(value)) {
                return range;
            }
        }

        return null;
    }

    // public HashMap<Integer, IntegerRange> generateCache() {
    //     HashMap<Integer, IntegerRange> cache = new HashMap<Integer, IntegerRange>();
    //     for(IntegerRange range : permissions.keySet()) {
    //         for (int i = range.min; i <= range.max; i++) {
    //             cache.put(i, range);
    //         }
    //     }
    //     return cache;
    // }
}
