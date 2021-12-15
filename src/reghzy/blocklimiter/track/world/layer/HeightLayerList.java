package reghzy.blocklimiter.track.world.layer;

import org.bukkit.craftbukkit.v1_6_R3.util.UnsafeList;
import reghzy.blocklimiter.track.world.TrackedBlock;
import reghzy.blocklimiter.track.world.Vector3;

import java.util.Collection;

/**
 * An implementation of IHeightLayer2D which uses a list to hold the blocks
 */
public class HeightLayerList implements IHeightLayer2D {
    private final int y;
    private final UnsafeList<TrackedBlock> blocks;

    public HeightLayerList(int y) {
        this.y = y;
        this.blocks = new UnsafeList<TrackedBlock>(32);
    }

    public int getY() {
        return this.y;
    }

    public TrackedBlock placeBlock(TrackedBlock block) {
        return placeBlock(block, block.getLocation().x, block.getLocation().z);
    }

    public TrackedBlock placeBlock(TrackedBlock block, int x, int z) {
        block.getLocation().set(x, this.y, z);
        block.getOwner().getData().addBlock(block);
        this.blocks.add(block);
        return block;
    }

    public TrackedBlock breakBlock(TrackedBlock trackedBlock) {
        return breakBlock(trackedBlock.getLocation().x, trackedBlock.getLocation().z);
    }

    @Override
    public TrackedBlock breakBlock(Vector3 vector) {
        return breakBlock(vector.x, vector.z);
    }

    /**
     * Removes the block from this height layer, and from the player's data who owned it, and returns the block
     * @param x The block's X coordinate
     * @param z The block's Z coordinate
     * @return The block that was previously there (or null if it didn't exist. Usually it should NOT be null)
     */
    public TrackedBlock breakBlock(int x, int z) {
        TrackedBlock block = getBlock(x, z);
        if (block != null) {
            block.getOwner().getData().removeBlock(block);
            this.blocks.remove(block);
        }

        return block;
    }

    public TrackedBlock getBlock(Vector3 location) {
        return getBlock(location.x, location.z);
    }

    public TrackedBlock getBlock(int x, int z) {
        UnsafeList<TrackedBlock> trackedBlocks = this.blocks;
        for (int i = 0, size = trackedBlocks.size(); i < size; i++) {
            TrackedBlock block = trackedBlocks.unsafeGet(i);
            Vector3 location = block.getLocation();
            if (location.x == x && location.z == z) {
                return block;
            }
        }

        return null;
    }

    public Collection<TrackedBlock> getBlocks() {
        return this.blocks;
    }
}
