package dragonjetz.blocklimiter.track.utils;

public class RangeLimit {
    public final String permission;
    public final String limitHitMessage;

    public RangeLimit(String permission,
                      String limitHitMessage) {
        this.permission = permission;
        this.limitHitMessage = limitHitMessage;
    }

    @Override
    public String toString() {
        return this.permission + ": " + limitHitMessage;
    }
}
