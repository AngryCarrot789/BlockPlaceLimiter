package dragonjetz.blocklimiter.track;

import dragonjetz.api.utils.types.ItemDataPair;
import dragonjetz.blocklimiter.track.world.TrackedBlock;
import dragonjetz.blocklimiter.track.world.Vector3;
import dragonjetz.blocklimiter.track.world.WorldTracker;
import net.minecraft.server.v1_6_R3.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R3.CraftChunk;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for syncing all of the blocks in the world with the tracked blocks
 */
public final class Synchroniser {
    public static List<TrackedBlock> scanForUnsynedBlocks(boolean loadChunks) {
        ArrayList<TrackedBlock> unsynced = new ArrayList<TrackedBlock>();
        ServerTracker serverTracker = ServerTracker.getInstance();

        for(WorldTracker worldTracker : serverTracker.getWorldTrackers()) {
            World world = worldTracker.getWorld();
            if (world == null)
                continue;

            for (TrackedBlock block : worldTracker.getAllBlocks()) {
                Vector3 location = block.getLocation();
                if (!loadChunks) {
                    if (!world.isChunkLoaded(location.x >> 4, location.z >> 4)) {
                        continue;
                    }
                }

                Chunk chunk = ((CraftChunk) world.getChunkAt(location.x >> 4, location.z >> 4)).getHandle();
                int id = chunk.getTypeId(location.x & 15, location.y & 255, location.z & 15);
                int meta = chunk.getData(location.x & 15, location.y & 255, location.z & 15);
                if (!block.getBlockData().match(id, meta)) {
                    unsynced.add(block);
                }
            }
        }

        for (TrackedBlock block : unsynced) {
            block.getWorldTracker().breakBlock(block);
        }

        return unsynced;
    }

    public static List<TrackedBlock> scanForUnsynedBlocks(boolean loadChunks, ItemDataPair data) {
        ArrayList<TrackedBlock> unsynced = new ArrayList<TrackedBlock>();
        for (WorldTracker worldTracker : ServerTracker.getInstance().getWorldTrackers()) {
            World world = worldTracker.getWorld();
            if (world == null)
                continue;

            for (TrackedBlock block : worldTracker.getAllBlocks()) {
                if (!block.getBlockData().match(data.getId(), data.getData())) {
                    continue;
                }

                Vector3 location = block.getLocation();
                if (!loadChunks) {
                    if (!world.isChunkLoaded(location.x >> 4, location.z >> 4)) {
                        continue;
                    }
                }

                Chunk chunk = ((CraftChunk) world.getChunkAt(location.x >> 4, location.z >> 4)).getHandle();
                int id = chunk.getTypeId(location.x & 15, location.y & 255, location.z & 15);
                int meta = chunk.getData(location.x & 15, location.y & 255, location.z & 15);
                if (!block.getBlockData().match(id, meta)) {
                    unsynced.add(block);
                }
            }
        }

        for (TrackedBlock block : unsynced) {
            block.getWorldTracker().breakBlock(block);
        }

        return unsynced;
    }
}
