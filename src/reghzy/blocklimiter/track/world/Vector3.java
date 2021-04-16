package reghzy.blocklimiter.track.world;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import reghzy.blocklimiter.utils.StringHelper;

public class Vector3 {
    public int x;
    public int y;
    public int z;

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

    public Vector2 toVector2() {
        return new Vector2(this.x, this.z);
    }

    public void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vector3 fromBlock(Block block) {
        return new Vector3(block.getX(), block.getY(), block.getZ());
    }

    public Block getBlock(World world) {
        return world.getBlockAt(this.x, this.y, this.z);
    }

    public static Vector3 deserialise(String content) {
        String[] split = StringHelper.split(content, ',', 0);
        if (split.length != 3) {
            return null;
        }

        return new Vector3(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    public static String serialise(Vector3 vector3) {
        return String.valueOf(vector3.x) + ',' + vector3.y + ',' + vector3.z;
    }

    public String formatColour() {
        return "&c" + x + "&6, &a" + y + "&6, &9" + z;
    }

    @Override
    public String toString() {
        return x + " " + y + " " + z;
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