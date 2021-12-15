package reghzy.blocklimiter.track.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import reghzy.blocklimiter.track.ServerTracker;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.track.world.layer.HeightLayerList;
import reghzy.blocklimiter.track.world.layer.IHeightLayer2D;

import java.util.ArrayList;
import java.util.Collection;

public class WorldTracker {
    private final ServerTracker serverTracker;
    private final IHeightLayer2D[] layers;
    private final String worldName;

    public WorldTracker(ServerTracker serverTracker, String worldName) {
        this.serverTracker = serverTracker;
        this.worldName = worldName;
        this.layers = new IHeightLayer2D[256];
        for(int y = 0; y < 256; y++) {
            this.layers[y] = new HeightLayerList(y);
        }
    }

    public User getOwner(int x, int y, int z) {
        return getBlock(x, y, z).getOwner();
    }

    public User getOwner(Vector3 vector3) {
        return getBlock(vector3).getOwner();
    }

    public TrackedBlock placeBlock(TrackedBlock block, Block bukkitBlock) {
        return placeBlock(block, bukkitBlock.getX(), bukkitBlock.getY(), bukkitBlock.getZ());
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

    public TrackedBlock breakBlock(Block block) {
        return breakBlock(block.getX(), block.getY(), block.getZ());
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

    public TrackedBlock getBlock(Vector3 vector3) {
        return getBlock(vector3.x, vector3.y, vector3.z);
    }

    public TrackedBlock getBlock(BlockLocation2D location, int y) {
        return getBlock(location.x, y, location.z);
    }

    public TrackedBlock getBlock(int x, int y, int z) {
        return getLayer(y).getBlock(x, z);
    }

    public World getWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    public String getWorldName() {
        return worldName;
    }

    public Collection<TrackedBlock> getAllBlocks() {
        ArrayList<TrackedBlock> blocks = new ArrayList<TrackedBlock>(256);
        for(int i = 0; i < 256; i++) {
            blocks.addAll(getLayer(i).getBlocks());
        }

        return blocks;
    }

    private IHeightLayer2D getLayer(int y) {
        return this.layers[y];
    }
}
