package reghzy.blocklimiter.track.world;

import reghzy.blocklimiter.track.block.TrackedBlock;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.utils.logs.ChatLogger;

import java.util.HashMap;

public class HeightLayerTracker {
    private final int y;
    private final WorldBlockTracker worldBlockTracker;
    private final HashMap<Vector2, TrackedBlock> blocks;

    public HeightLayerTracker(WorldBlockTracker worldBlockTracker, int y) {
        this.y = y;
        this.worldBlockTracker = worldBlockTracker;
        this.blocks = new HashMap<Vector2, TrackedBlock>();
    }

    public TrackedBlock getBlock(Vector2 location) {
        return this.blocks.get(location);
    }

    public User getBlockOwner(Vector2 location) {
        return getBlock(location).getOwner();
    }

    public void placeBlock(TrackedBlock block, Vector2 location) {
        block.getLocation().set(location.x, this.y, location.z);
        TrackedBlock existingBlock = this.blocks.put(location, block);
        if (existingBlock == null)
            return;

        ChatLogger.logConsole("A block already exists at: " + location.toString());
    }

    public void placeBlock(TrackedBlock block, int x, int z) {
        block.getLocation().set(x, this.y, z);
        placeBlock(block, new Vector2(x, z));
    }

    public void placeBlock(TrackedBlock block) {
        placeBlock(block, block.getLocation().toVector2());
    }

    public TrackedBlock breakBlock(Vector2 location) {
        TrackedBlock existingBlock = this.blocks.remove(location);
        if (existingBlock == null) {
            ChatLogger.logConsole("A block did not exist at: " + location.toString());
        }
        return existingBlock;
    }

    public TrackedBlock breakBlock(int x, int z) {
        return breakBlock(new Vector2(x, z));
    }

    public TrackedBlock breakBlock(TrackedBlock trackedBlock) {
        return breakBlock(trackedBlock.getLocation().toVector2());
    }
}
