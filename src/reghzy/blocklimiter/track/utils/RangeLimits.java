package reghzy.blocklimiter.track.utils;

import java.util.HashMap;

public class RangeLimits {
    private final HashMap<IntegerRange, RangeLimit> permissions;

    public RangeLimits(HashMap<IntegerRange, RangeLimit> permissions) {
        this.permissions = permissions;
    }

    public RangeLimits() {
        this.permissions = new HashMap<IntegerRange, RangeLimit>(4);
    }

    public RangeLimit getPermission(IntegerRange range) {
        return this.permissions.get(range);
    }

    public void addPermission(IntegerRange range, RangeLimit permission) {
        this.permissions.put(range, permission);
    }

    public HashMap<Integer, IntegerRange> generateCache() {
        HashMap<Integer, IntegerRange> cache = new HashMap<Integer, IntegerRange>();
        for(IntegerRange range : permissions.keySet()) {
            for (int i = range.min; i <= range.max; i++) {
                cache.put(i, range);
            }
        }
        return cache;
    }
}
