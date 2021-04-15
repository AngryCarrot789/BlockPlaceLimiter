package reghzy.blocklimiter.track.utils;

public class PermissionMessagePair {
    public final String permission;
    public final String denyMessage;

    public PermissionMessagePair(String permission, String denyMessage) {
        this.permission = permission;
        this.denyMessage = denyMessage;
    }

    @Override
    public String toString() {
        return this.permission + ": " + denyMessage;
    }
}
