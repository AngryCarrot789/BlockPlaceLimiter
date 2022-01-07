package reghzy.blocklimiter.track.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import reghzy.api.utils.text.StringHelper;

public class BPLVec3i {
    public int x;
    public int y;
    public int z;

    public BPLVec3i() {

    }

    public BPLVec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BPLVec3i(Block block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
    }

    public BPLVec3i(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public BPLVec2i toVector2() {
        return new BPLVec2i(this.x, this.z);
    }

    public void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Block getBlock(World world) {
        return world.getBlockAt(this.x, this.y, this.z);
    }

    public Location toBukkitLocation(World world) {
        return new Location(world, this.x, this.y, this.z);
    }

    public static BPLVec3i deserialise(String content) {
        String[] split = StringHelper.split(content, ',', 0);
        if (split.length != 3) {
            return null;
        }

        return new BPLVec3i(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    public static String serialise(BPLVec3i vector3) {
        return new StringBuilder().append(vector3.x).append(',').append(vector3.y).append(',').append(vector3.z).toString();
    }

    public String formatColour() {
        return new StringBuilder().append("&c").append(x).append("&6, &a").append(y).append("&6, &9").append(z).toString();
    }

    @Override
    public String toString() {
        return new StringBuilder().append(x).append(" ").append(y).append(" ").append(z).toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BPLVec3i) {
            BPLVec3i v = (BPLVec3i) obj;
            return v.x == this.x && v.y == this.y && v.z == this.z;
        }
        return false;
    }
}