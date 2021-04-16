package reghzy.blocklimiter.track.world;

import org.bukkit.block.Block;

public class Vector2 {
    public final int x;
    public final int z;

    public Vector2(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public Vector2(Block block) {
        this(block.getX(), block.getZ());
    }

    public Vector2(Vector3 vector3) {
        this(vector3.x, vector3.z);
    }

    public Vector3 toVector3(int y) {
        return new Vector3(this.x, y, this.z);
    }

    public String toString() {
        return x + " " + z;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Vector2) {
            Vector2 vector2 = (Vector2) obj;
            return vector2.x == this.x && vector2.z == this.z;
        }
        return false;
    }

    public int hashCode() {
        return hash(this.x, this.z);
    }

    public static int hash(int x, int z) {
        return x + (z << 15);
    }
}
