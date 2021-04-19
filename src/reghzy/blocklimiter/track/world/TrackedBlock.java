package reghzy.blocklimiter.track.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import reghzy.blocklimiter.track.ServerTracker;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.track.utils.BlockDataPair;

public class TrackedBlock {
    private final User owner;
    private final String worldName;
    private final BlockDataPair blockData;
    private final Vector3 location;

    private TrackedBlock(User owner, String worldName, BlockDataPair blockData, Vector3 location) {
        this.owner = owner;
        this.worldName = worldName;
        this.blockData = blockData;
        this.location = location;
    }

    /**
     * Creates a tracked block, owned by the given owner, placed in the given world name, with the given block ID:Data at the given location
     * <p>
     *     This will NOT add the block to the user's data; it will technically be un-owned. there are very little reasons to use this function standalone
     * </p>
     */
    public static TrackedBlock createBlock(User owner, String worldName, BlockDataPair blockData, Vector3 location) {
        return new TrackedBlock(owner, worldName, blockData, location);
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

    public WorldTracker getWorldTracker() {
        return ServerTracker.getInstance().getWorldTracker(getWorldName());
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
