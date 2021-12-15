package reghzy.blocklimiter.track.world.layer;

import reghzy.blocklimiter.track.world.TrackedBlock;
import reghzy.blocklimiter.track.world.Vector3;
import reghzy.blocklimiter.utils.collections.Grid2DHashMap;

import java.util.Collection;

/**
 * A height layer tracker is a 2 dimensional grid of blocks (it actually uses a
 * LongObjectHashMap to reduce memory usage), it also contains its Y location
 */
public class HeightLayerGrid2DHashMap implements IHeightLayer2D {
    private final int y;
    // private final LongObjectHashMap<TrackedBlock> blocks;
    // private final HashMap<Integer, TrackedBlock> blocks;
    private final Grid2DHashMap<TrackedBlock> blocks;

    public HeightLayerGrid2DHashMap(int y) {
        this.y = y;
        //this.blocks = new LongObjectHashMap<TrackedBlock>();
        //this.blocks = new HashMap<Integer, TrackedBlock>();
        this.blocks = new Grid2DHashMap<TrackedBlock>();
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
        this.blocks.put(x, z, block);
        return block;
    }

    public TrackedBlock breakBlock(TrackedBlock trackedBlock) {
        return breakBlock(trackedBlock.getLocation().x, trackedBlock.getLocation().z);
    }

    public TrackedBlock breakBlock(Vector3 vector) {
        return breakBlock(vector.x, vector.z);
    }

    public TrackedBlock breakBlock(int x, int z) {
        TrackedBlock block = this.blocks.remove(x, z);
        if (block != null) {
            block.getOwner().getData().removeBlock(block);
        }

        return block;
    }

    public TrackedBlock getBlock(Vector3 location) {
        return getBlock(location.x, location.z);
    }

    public TrackedBlock getBlock(int x, int z) {
        return this.blocks.get(x, z);
    }

    public Collection<TrackedBlock> getBlocks() {
        return this.blocks.getAllValues();
    }
}