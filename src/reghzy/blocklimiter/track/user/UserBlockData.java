package reghzy.blocklimiter.track.user;

import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.world.TrackedBlock;
import reghzy.carrottools.utils.collections.multimap.HashSetMultiMap;
import reghzy.carrottools.utils.collections.multimap.MultiMapEntry;

import java.util.Collection;
import java.util.HashSet;

public class UserBlockData {
    private final User user;
    private final HashSetMultiMap<BlockDataPair, TrackedBlock> placedBlocks;

    private boolean hasDataChanged;

    public UserBlockData(User user) {
        this.user = user;
        this.placedBlocks = new HashSetMultiMap<BlockDataPair, TrackedBlock>();
    }

    public int getPlacedBlocks(BlockDataPair blockData) {
        return this.placedBlocks.valuesSize(blockData);
    }

    public int getPlacedIDs() {
        return this.placedBlocks.keysSize();
    }

    public int getTotalPlacedBlocks() {
        int count = 0;
        for(BlockDataPair pair : placedBlocks.getKeys()) {
            count += placedBlocks.valuesSize(pair);
        }

        return count;
    }

    public HashSet<TrackedBlock> getBlocks(BlockDataPair blockData) {
        return placedBlocks.getValues(blockData);
    }

    public Collection<MultiMapEntry<BlockDataPair, TrackedBlock>> getBlockEntries() {
        return placedBlocks.getEntrySet();
    }

    public boolean addBlock(TrackedBlock block) {
        this.hasDataChanged = true;
        return placedBlocks.put(block.getBlockData(), block);
    }

    public boolean removeBlock(BlockDataPair dataPair, TrackedBlock location) {
        this.hasDataChanged = true;
        return placedBlocks.remove(dataPair, location);
    }

    public boolean removeBlock(TrackedBlock block) {
        return removeBlock(block.getBlockData(), block);
    }

    public boolean shouldUnload() {
        return getPlacedIDs() == 0;
    }

    public boolean hasDataChanged() {
        return this.hasDataChanged;
    }

    public void setDataChanged(boolean value) {
        this.hasDataChanged = value;
    }

    public User getUser() {
        return user;
    }
}
