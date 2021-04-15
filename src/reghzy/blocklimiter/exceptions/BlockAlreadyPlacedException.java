package reghzy.blocklimiter.exceptions;

import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.track.world.Vector3;

public class BlockAlreadyPlacedException extends RuntimeException {
    private final Vector3 location;
    private final User placer;

    public BlockAlreadyPlacedException(Vector3 location, User placer) {
        super("Block at " + location.toString() + " was already placed, but someone tried to place a block there. Placer: " + placer.getName());
        this.location = location;
        this.placer = placer;
    }

    public Vector3 getLocation() {
        return location;
    }

    public User getPlacer() {
        return placer;
    }
}
