package reghzy.blocklimiter.track.world;

import org.bukkit.block.Block;

public class BPLVec2i {
    public final int x;
    public final int z;

    public BPLVec2i(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public BPLVec2i(Block block) {
        this(block.getX(), block.getZ());
    }

    public BPLVec2i(BPLVec3i vector3) {
        this(vector3.x, vector3.z);
    }

    public BPLVec3i toVector3(int y) {
        return new BPLVec3i(this.x, y, this.z);
    }

    public String toString() {
        return "BlockLocation2D{x:" + x + ",z:" + z + "}";
    }

    public boolean equals(Object obj) {
        if (obj instanceof BPLVec2i) {
            BPLVec2i blockLocation2D = (BPLVec2i) obj;
            return blockLocation2D.x == this.x && blockLocation2D.z == this.z;
        }
        return false;
    }

    public int hashCode() {
        return this.x + (this.z << 15);
    }
}
