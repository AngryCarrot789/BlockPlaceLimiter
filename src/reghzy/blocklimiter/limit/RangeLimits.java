package reghzy.blocklimiter.limit;

import reghzy.blocklimiter.track.utils.IntegerRange;
import reghzy.blocklimiter.track.utils.PermissionMessagePair;

import java.util.HashMap;

public class RangeLimits {
    private final HashMap<IntegerRange, PermissionMessagePair> permissions;

    public RangeLimits(HashMap<IntegerRange, PermissionMessagePair> permissions) {
        this.permissions = permissions;
    }

    public RangeLimits() {
        this.permissions = new HashMap<IntegerRange, PermissionMessagePair>(4);
    }

    public PermissionMessagePair getPermission(IntegerRange range) {
        return this.permissions.get(range);
    }

    public void addPermission(IntegerRange range, PermissionMessagePair permission) {
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
