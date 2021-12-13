package dragonjetz.blocklimiter.track.world;

import org.bukkit.block.Block;

public class BlockLocation2D {
    public final int x;
    public final int z;

    public BlockLocation2D(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public BlockLocation2D(Block block) {
        this(block.getX(), block.getZ());
    }

    public BlockLocation2D(Vector3 vector3) {
        this(vector3.x, vector3.z);
    }

    public Vector3 toVector3(int y) {
        return new Vector3(this.x, y, this.z);
    }

    public String toString() {
        return "BlockLocation2D{x:" + x + ",z:" + z + "}";
    }

    public boolean equals(Object obj) {
        if (obj instanceof BlockLocation2D) {
            BlockLocation2D blockLocation2D = (BlockLocation2D) obj;
            return blockLocation2D.x == this.x && blockLocation2D.z == this.z;
        }
        return false;
    }

    public int hashCode() {
        return this.x + (this.z << 15);
    }
}
