package reghzy.blocklimiter.exceptions;

import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.track.world.Vector3;

public class BlockAlreadyBrokenException extends Exception {
    private final Vector3 location;
    private final User breaker;

    public BlockAlreadyBrokenException(Vector3 location, User breaker) {
        super("A limited block at " + location.toString() + " was already broken, but was still placed. Breaker: " + breaker.getName());
        this.location = location;
        this.breaker = breaker;
    }

    public Vector3 getLocation() {
        return location;
    }

    public User getBreaker() {
        return breaker;
    }
}
