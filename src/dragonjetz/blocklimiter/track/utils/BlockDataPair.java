package dragonjetz.blocklimiter.track.utils;

import org.bukkit.block.Block;

public class BlockDataPair {
    public final int id;
    public final int data;

    public BlockDataPair(int id, int data) {
        this.id = id;
        this.data = data;
    }

    public BlockDataPair(Block block) {
        this(block.getTypeId(), block.getData());
    }

    public boolean match(Block block) {
        return block.getTypeId() == this.id && (this.data == -1 || this.data == block.getData());
    }

    public boolean match(int id, int data) {
        return id == this.id && (this.data == -1 || this.data == data);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockDataPair) {
            BlockDataPair pair = (BlockDataPair) obj;
            return pair.id == this.id && pair.data == this.data;
        }
        return false;
    }

    @Override
    public String toString() {
        return id + ":" + data;
    }

    @Override
    public int hashCode() {
        return this.id + (this.data << 12);
    }
}
