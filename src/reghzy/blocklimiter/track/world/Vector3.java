package reghzy.blocklimiter.track.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import reghzy.api.utils.text.StringHelper;

public class Vector3 {
    public int x;
    public int y;
    public int z;

    public Vector3() {

    }

    public Vector3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Block block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
    }

    public Vector3(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public BlockLocation2D toVector2() {
        return new BlockLocation2D(this.x, this.z);
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

    public static Vector3 deserialise(String content) {
        String[] split = StringHelper.split(content, ',', 0);
        if (split.length != 3) {
            return null;
        }

        return new Vector3(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    public static String serialise(Vector3 vector3) {
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
        if (obj instanceof Vector3) {
            Vector3 v = (Vector3) obj;
            return v.x == this.x && v.y == this.y && v.z == this.z;
        }
        return false;
    }
}