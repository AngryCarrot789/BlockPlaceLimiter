package reghzy.blocklimiter.track.block;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import reghzy.blocklimiter.track.ServerBlockTracker;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.world.HeightLayerTracker;
import reghzy.blocklimiter.track.world.Vector3;
import reghzy.blocklimiter.track.world.WorldBlockTracker;

public class TrackedBlock {
    private final User owner;
    private final String worldName;
    private final BlockDataPair blockData;
    private final Vector3 location;

    public TrackedBlock(User owner, String worldName, BlockDataPair blockData, Vector3 location) {
        this.owner = owner;
        this.worldName = worldName;
        this.blockData = blockData;
        this.location = location;
    }

    public User getOwner() {
        return this.owner;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public BlockDataPair getBlockData() {
        return this.blockData;
    }

    public Vector3 getLocation() {
        return this.location;
    }

    public HeightLayerTracker getHeightLayer() {
        return ServerBlockTracker.getInstance().getWorldTracker(getWorldName()).getLayer(location);
    }

    public WorldBlockTracker getWorldTracker() {
        return ServerBlockTracker.getInstance().getWorldTracker(getWorldName());
    }

    public World getBukkitWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    public Block getBlock(World world) {
        return world.getBlockAt(location.x, location.y, location.z);
    }

    public Block getBlock() {
        return getBlock(getBukkitWorld());
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TrackedBlock) {
            TrackedBlock block = (TrackedBlock) obj;
            return block.owner.equals(this.owner) &&
                   block.worldName.equals(this.worldName) &&
                   block.blockData.equals(this.blockData) &&
                   block.location.equals(this.location);
        }
        return false;
    }
}
