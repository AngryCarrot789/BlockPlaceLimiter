package reghzy.blocklimiter.track;

import org.bukkit.World;
import org.bukkit.block.Block;
import reghzy.blocklimiter.track.world.TrackedBlock;
import reghzy.blocklimiter.track.world.WorldTracker;

import java.util.ArrayList;

/**
 * A class for syncing all of the blocks in the world with the tracked blocks
 */
public final class Synchroniser {
    public static ArrayList<TrackedBlock> scanForUnsynedBlocks() {
        ArrayList<TrackedBlock> unsynced = new ArrayList<TrackedBlock>();
        ServerTracker serverTracker = ServerTracker.getInstance();

        for(WorldTracker worldTracker : serverTracker.getWorldTrackers()) {
            World world = worldTracker.getWorld();
            if (world == null)
                continue;

            for(int i = 0; i < 256; i++) {
                for(TrackedBlock block : worldTracker.getBlocks()) {
                    Block bukkitBlock = block.getBlock(world);
                    if (bukkitBlock.isEmpty()) {
                        unsynced.add(block);
                    }
                }
            }
            for (TrackedBlock block : unsynced) {
                block.getWorldTracker().breakBlock(block);
            }
        }

        return unsynced;
    }
}
