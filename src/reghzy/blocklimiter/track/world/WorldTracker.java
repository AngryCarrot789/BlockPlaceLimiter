package reghzy.blocklimiter.track.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import reghzy.blocklimiter.track.ServerTracker;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.utils.collections.Array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class WorldTracker {
    private final ServerTracker serverTracker;
    private final Array<HeightLayerTracker> layers;
    private final String worldName;

    public WorldTracker(ServerTracker serverTracker, String worldName) {
        this.serverTracker = serverTracker;
        this.worldName = worldName;
        this.layers = new Array<HeightLayerTracker>(256);
        for(int y = 0; y < 256; y++) {
            this.layers.set(y, new HeightLayerTracker(y));
        }
    }

    public User getOwner(int x, int y, int z) {
        return getBlock(x, y, z).getOwner();
    }

    public User getOwner(Vector3 vector3) {
        return getBlock(vector3).getOwner();
    }

    public TrackedBlock placeBlock(TrackedBlock block) {
        return placeBlock(block, block.getLocation());
    }

    public TrackedBlock placeBlock(TrackedBlock block, Vector3 vector3) {
        return placeBlock(block, vector3.x, vector3.y, vector3.z);
    }

    public TrackedBlock placeBlock(TrackedBlock block, int x, int y, int z) {
        return getLayer(y).placeBlock(block, x, z);
    }

    public TrackedBlock breakBlock(TrackedBlock block) {
        return breakBlock(block.getLocation());
    }

    public TrackedBlock breakBlock(Vector3 vector3) {
        return breakBlock(vector3.x, vector3.y, vector3.z);
    }

    public TrackedBlock breakBlock(int x, int y, int z) {
        return getLayer(y).breakBlock(x, z);
    }

    public TrackedBlock getBlock(Block block) {
        return getBlock(block.getX(), block.getY(), block.getZ());
    }

    public TrackedBlock getBlock(int x, int y, int z) {
        return getBlock(new Vector2(x, z), y);
    }

    public TrackedBlock getBlock(Vector3 vector3) {
        return getBlock(vector3.toVector2(), vector3.y);
    }

    public TrackedBlock getBlock(Vector2 vector2, int y) {
        return getLayer(y).getBlock(vector2);
    }

    public World getWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    public String getWorldName() {
        return worldName;
    }

    public Collection<TrackedBlock> getBlocks() {
        ArrayList<TrackedBlock> blocks = new ArrayList<TrackedBlock>(2048);
        for(int i = 0; i < 256; i++) {
            blocks.addAll(getLayer(i).getBlocks());
        }
        return blocks;
    }

    private HeightLayerTracker getLayer(int y) {
        return layers.get(y);
    }

    private static class HeightLayerTracker {
        private final int y;
        private final HashMap<Vector2, TrackedBlock> blocks;

        public HeightLayerTracker(int y) {
            this.y = y;
            this.blocks = new HashMap<Vector2, TrackedBlock>();
        }

        public int getY() {
            return this.y;
        }

        public TrackedBlock placeBlock(TrackedBlock block, Vector2 location) {
            block.getLocation().set(location.x, this.y, location.z);
            block.getOwner().getData().addBlock(block);
            this.blocks.put(location, block);
            return block;
        }
        public TrackedBlock placeBlock(TrackedBlock block, int x, int z) {
            return placeBlock(block, new Vector2(x, z));
        }

        public TrackedBlock placeBlock(TrackedBlock block) {
            return placeBlock(block, block.getLocation().toVector2());
        }

        public TrackedBlock breakBlock(int x, int z) {
            return breakBlock(new Vector2(x, z));
        }

        public TrackedBlock breakBlock(TrackedBlock trackedBlock) {
            return breakBlock(trackedBlock.getLocation().toVector2());
        }

        public TrackedBlock breakBlock(Vector2 location) {
            TrackedBlock block = this.blocks.remove(location);
            if (block != null) {
                block.getOwner().getData().removeBlock(block);
            }

            return block;
        }

        public TrackedBlock getBlock(Vector2 location) {
            return this.blocks.get(location);
        }

        public Collection<TrackedBlock> getBlocks() {
            return this.blocks.values();
        }
    }
}
