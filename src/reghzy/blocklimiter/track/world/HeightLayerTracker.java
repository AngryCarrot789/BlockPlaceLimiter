package reghzy.blocklimiter.track.world;

import reghzy.blocklimiter.exceptions.BlockAlreadyBrokenException;
import reghzy.blocklimiter.exceptions.BlockAlreadyPlacedException;
import reghzy.blocklimiter.track.block.TrackedBlock;
import reghzy.blocklimiter.track.user.User;

import java.util.Collection;
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

    /**
     * Returns the block that was placed. Throws a BlockAlreadyPlacedException if... well it was already placed due to a bug
     * @param block
     * @param location
     * @return
     */
    public TrackedBlock placeBlock(TrackedBlock block, Vector2 location) throws BlockAlreadyPlacedException {
        block.getLocation().set(location.x, this.y, location.z);
        block.getOwner().getData().addBlock(block);
        TrackedBlock existingBlock = this.blocks.put(location, block);
        if (existingBlock == null)
            return block;

        throw new BlockAlreadyPlacedException(block.getLocation(), existingBlock.getOwner());
    }

    public TrackedBlock placeBlock(TrackedBlock block, int x, int z) throws BlockAlreadyPlacedException {
        block.getLocation().set(x, this.y, z);
        return placeBlock(block, new Vector2(x, z));
    }

    public TrackedBlock placeBlock(TrackedBlock block) throws BlockAlreadyPlacedException {
        return placeBlock(block, block.getLocation().toVector2());
    }

    public TrackedBlock breakBlock(Vector2 location) throws BlockAlreadyBrokenException {
        TrackedBlock existingBlock = this.blocks.remove(location);
        if (existingBlock == null) {
            throw new BlockAlreadyBrokenException(location.toVector3(this.y), User.unknownUser());
        }

        existingBlock.getOwner().getData().removeBlock(existingBlock);
        return existingBlock;
    }

    public TrackedBlock breakBlock(int x, int z) throws BlockAlreadyBrokenException {
        return breakBlock(new Vector2(x, z));
    }

    public TrackedBlock breakBlock(TrackedBlock trackedBlock) throws BlockAlreadyBrokenException {
        return breakBlock(trackedBlock.getLocation().toVector2());
    }

    public Collection<TrackedBlock> getBlocks() {
        return this.blocks.values();
    }
}
