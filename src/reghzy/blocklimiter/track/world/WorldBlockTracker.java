package reghzy.blocklimiter.track.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import reghzy.blocklimiter.track.ServerBlockTracker;
import reghzy.blocklimiter.track.block.TrackedBlock;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.utils.collections.Array;

public class WorldBlockTracker {
    private final ServerBlockTracker serverBlockTracker;
    private final Array<HeightLayerTracker> layers;
    private final String worldName;
    private World world;

    public WorldBlockTracker(ServerBlockTracker serverBlockTracker, String worldName) {
        this.serverBlockTracker = serverBlockTracker;
        this.worldName = worldName;
        this.layers = new Array<HeightLayerTracker>(256);
        for(int y = 0; y < 256; y++) {
            this.layers.set(y, new HeightLayerTracker(this, y));
        }
    }

    public User getBlockOwner(Vector3 location) {
        return getLayer(location).getBlockOwner(location.toVector2());
    }

    public void placeBlock(TrackedBlock block, int x, int y, int z) {
        getLayer(y).placeBlock(block, x, z);
    }

    public void placeBlock(TrackedBlock block, Vector3 vector3) {
        getLayer(vector3).placeBlock(block, vector3.toVector2());
    }

    public void placeBlock(TrackedBlock block) {
        getLayer(block.getLocation()).placeBlock(block, block.getLocation().toVector2());
    }

    public TrackedBlock breakBlock(TrackedBlock block) {
        return getLayer(block.getLocation().y).breakBlock(block.getLocation().toVector2());
    }

    public TrackedBlock breakBlock(int x, int y, int z) {
        return getLayer(y).breakBlock(x, z);
    }

    public TrackedBlock breakBlock(Vector3 vector3) {
        return getLayer(vector3).breakBlock(vector3.toVector2());
    }

    public TrackedBlock getBlock(Vector2 vector2, int y) {
        return getLayer(y).getBlock(vector2);
    }

    public TrackedBlock getBlock(Vector3 vector3) {
        return getBlock(vector3.toVector2(), vector3.y);
    }

    public TrackedBlock getBlock(int x, int y, int z) {
        return getBlock(new Vector2(x, z), y);
    }

    public HeightLayerTracker getLayer(int y) {
        return layers.get(y);
    }

    public HeightLayerTracker getLayer(Vector3 vector3) {
        return getLayer(vector3.y);
    }

    public World getWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    public String getWorldName() {
        return worldName;
    }
}
