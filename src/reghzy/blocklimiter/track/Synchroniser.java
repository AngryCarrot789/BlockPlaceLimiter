package reghzy.blocklimiter.track;

import net.minecraft.world.chunk.Chunk;
import org.bukkit.World;
import reghzy.api.utils.NMSAPI;
import reghzy.api.utils.types.ItemDataPair;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.world.BPLVec3i;
import reghzy.blocklimiter.track.world.TrackedBlock;
import reghzy.blocklimiter.track.world.WorldTracker;

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
                BPLVec3i location = block.getLocation();
                if (loadChunks || world.isChunkLoaded(location.x >> 4, location.z >> 4)) {
                    Chunk chunk = NMSAPI.getChunk(world, location.x >> 4, location.z >> 4);
                    int id = NMSAPI.getBlockId(chunk, location.x, location.y, location.z);
                    BlockDataPair data = block.getBlockData();
                    if (data.data == -1) {
                        if (data.id != id) {
                            unsynced.add(block);
                        }
                    }
                    else if (data.id != id && data.data != NMSAPI.getBlockData(chunk, location.x, location.y, location.z)) {
                        unsynced.add(block);
                    }
                }
            }
        }

        for (TrackedBlock block : unsynced) {
            block.getWorldTracker().breakBlock(block);
        }

        return unsynced;
    }

    public static List<TrackedBlock> scanForUnsynedBlocks(boolean loadChunks, ItemDataPair specificItem) {
        ArrayList<TrackedBlock> unsynced = new ArrayList<TrackedBlock>();
        for (WorldTracker worldTracker : ServerTracker.getInstance().getWorldTrackers()) {
            World world = worldTracker.getWorld();
            if (world == null)
                continue;

            for (TrackedBlock block : worldTracker.getAllBlocks()) {
                BlockDataPair data = block.getBlockData();
                if (!data.match(specificItem.getId(), specificItem.getData())) {
                    continue;
                }

                BPLVec3i location = block.getLocation();
                if (loadChunks || world.isChunkLoaded(location.x >> 4, location.z >> 4)) {
                    Chunk chunk = NMSAPI.getChunk(world, location.x >> 4, location.z >> 4);
                    int id = NMSAPI.getBlockId(chunk, location.x, location.y, location.z);
                    if (data.data == -1) {
                        if (data.id != id) {
                            unsynced.add(block);
                        }
                    }
                    else if (data.id != id && data.data != NMSAPI.getBlockData(chunk, location.x, location.y, location.z)) {
                        unsynced.add(block);
                    }
                }
            }
        }

        for (TrackedBlock block : unsynced) {
            block.getWorldTracker().breakBlock(block);
        }

        return unsynced;
    }
}
