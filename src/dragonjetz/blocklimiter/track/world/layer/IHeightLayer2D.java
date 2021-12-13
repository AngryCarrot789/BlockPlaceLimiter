package dragonjetz.blocklimiter.track.world.layer;

import dragonjetz.blocklimiter.track.world.TrackedBlock;
import dragonjetz.blocklimiter.track.world.Vector3;

import java.util.Collection;

/**
 * This interface provides functions to do with blocks which are contained at a specific height layer (there is 256 layers in a single world)
 */
public interface IHeightLayer2D {
    int getY();

    /**
     * Gets a block at the given X and Z coordinates
     * @param x The block's X coordinate
     * @param z The block's Z coordinate
     * @return The block, or null if it didn't exist
     */
    TrackedBlock getBlock(int x, int z);

    /**
     * Gets a block at the given vector's X and Z coordinates
     * @param vector The coordinates of the block (only uses the X and Z part)
     * @return The block, or null if it didn't exist
     */
    TrackedBlock getBlock(Vector3 vector);

    /**
     * Removes the block from this height layer, and from the player's data who owned it, and returns the block
     * @param x The block's X coordinate
     * @param z The block's Z coordinate
     * @return The block that was previously there (or null if it didn't exist. Usually it should NOT be null)
     */
    TrackedBlock breakBlock(int x, int z);

    /**
     * Removes the block from this height layer, and from the player's data who owned it, and returns the block
     * @param vector The coordinates of the block (only uses the X and Z part)
     * @return The block that was previously there (or null if it didn't exist. Usually it should NOT be null)
     */
    TrackedBlock breakBlock(Vector3 vector);

    /**
     * Removes the block from this height layer, and from the player's data who owned it, and returns the block
     * @param block The block to use the location of
     * @return The block that was previously there (or null if it didn't exist. Usually it should NOT be null)
     */
    TrackedBlock breakBlock(TrackedBlock block);

    /**
     * Places the given block at the given X and Z coordinates
     * @param block The block to place
     * @return The block that you passed in the parameter
     */
    TrackedBlock placeBlock(TrackedBlock block, int x, int z);

    /**
     * Places the given block
     * @param block The block to place
     * @return The block that you passed in the parameter
     */
    TrackedBlock placeBlock(TrackedBlock block);

    Collection<TrackedBlock> getBlocks();
}
