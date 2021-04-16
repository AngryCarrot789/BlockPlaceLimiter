package reghzy.blocklimiter.track;

import net.minecraft.server.v1_6_R3.ItemNameTag;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import reghzy.blocklimiter.exceptions.BlockAlreadyBrokenException;
import reghzy.blocklimiter.track.block.TrackedBlock;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.world.HeightLayerTracker;
import reghzy.blocklimiter.track.world.WorldBlockTracker;

import java.util.ArrayList;

/**
 * A class for syncing all of the blocks in the world with the tracked blocks
 */
public final class Synchroniser {
    public static ArrayList<TrackedBlock> scanForUnsynedBlocks() {
        ArrayList<TrackedBlock> unsynced = new ArrayList<TrackedBlock>();
        //ArrayList<Block> unsyncedBukkit = new ArrayList<Block>();
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
                        unsynced.add(block);
                        //unsyncedBukkit.add(bukkitBlock);
                    }
                }
            }
            for (TrackedBlock block : unsynced) {
                //Block bukkitBlock = unsyncedBukkit.get(i);
                removeOwnerBlock(block);
            }
        }

        return unsynced;
    }

    public static void processUnsyncronisedBlock(TrackedBlock trackedBlock, Block bukkitBlock) {
        removeOwnerBlock(trackedBlock);
    }

    public static void removeOwnerBlock(TrackedBlock block) {
        try {
            block.getHeightLayer().breakBlock(block);
        }
        catch (BlockAlreadyBrokenException e) {

        }
        block.getOwner().getData().removeBlock(block);
    }

    public static void placeOwnerBlock(TrackedBlock block, Block bukkitBlock) {
        BlockDataPair blockData = block.getBlockData();
        bukkitBlock.setTypeIdAndData(blockData.id, blockData.data == -1 ? 0 : (byte) blockData.data, true);
    }
}
