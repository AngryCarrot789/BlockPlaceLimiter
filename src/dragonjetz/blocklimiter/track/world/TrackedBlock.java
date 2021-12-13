package dragonjetz.blocklimiter.track.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import dragonjetz.blocklimiter.track.ServerTracker;
import dragonjetz.blocklimiter.track.user.User;
import dragonjetz.blocklimiter.track.utils.BlockDataPair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A tracked blocks holds the owner of the block, the name of the world its in, the location, and the block ID:MetaData
 * <p>
 *     Also contains functions for getting the actual minecraft blocks, ect
 * </p>
 */
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

    @Nonnull
    public User getOwner() {
        return this.owner;
    }

    @Nonnull
    public String getWorldName() {
        return this.worldName;
    }

    @Nonnull
    public BlockDataPair getBlockData() {
        return this.blockData;
    }

    @Nonnull
    public Vector3 getLocation() {
        return this.location;
    }

    @Nonnull
    public WorldTracker getWorldTracker() {
        return ServerTracker.getInstance().getWorldTracker(getWorldName());
    }

    @Nullable
    public World getBukkitWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    @Nonnull
    public Block getBlock(World world) {
        return world.getBlockAt(location.x, location.y, location.z);
    }

    @Nullable
    public Block getBlock() {
        World world = getBukkitWorld();
        if (world == null) {
            return null;
        }

        return getBlock(world);
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
