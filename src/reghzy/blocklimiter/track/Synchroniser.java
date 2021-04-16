package reghzy.blocklimiter.track;

import org.bukkit.World;
import org.bukkit.block.Block;
import reghzy.blocklimiter.track.block.TrackedBlock;
import reghzy.blocklimiter.track.world.HeightLayerTracker;
import reghzy.blocklimiter.track.world.WorldBlockTracker;

/**
 * A class for syncing all of the blocks in the world with the tracked blocks
 */
public final class Synchroniser {
    public void scanServer() {
        ServerBlockTracker serverTracker = ServerBlockTracker.getInstance();

        for(WorldBlockTracker worldTracker : serverTracker.getWorldTrackers()) {
            World world = worldTracker.getWorld();
            if (world == null)
                continue;

            for(int i = 0; i < 256; i++) {
                HeightLayerTracker layer = worldTracker.getLayer(i);
                for(TrackedBlock block : layer.getBlocks()) {
                    Block bukkitBlock = block.getBlock(world);
                    if (bukkitBlock.isEmpty()) {
                        processUnsyncronisedBlock(block, bukkitBlock);
                    }
                }
            }
        }
    }

    public void processUnsyncronisedBlock(TrackedBlock trackedBlock, Block bukkitBlock) {
        // BlockDataPair blockData = block.getBlockData();
        // bukkitBlock.setTypeIdAndData(blockData.id, blockData.data == -1 ? 0 : (byte) blockData.data, true);
        // bukkitBlock.
    }
}
