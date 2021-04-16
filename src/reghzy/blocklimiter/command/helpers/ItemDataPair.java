package reghzy.blocklimiter.command.helpers;

import org.bukkit.block.Block;

public class ItemDataPair {
    public final int id;
    public final int data;

    public ItemDataPair(int id, int data) {
        this.id = id;
        this.data = data;
    }

    public boolean match(Block block) {
        return block.getTypeId() == this.id && (this.data == -1 || block.getData() == this.data);
    }

    @Override
    public String toString() {
        return id + ":" + data;
    }
}
