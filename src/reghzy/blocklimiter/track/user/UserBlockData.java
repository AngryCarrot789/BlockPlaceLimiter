package reghzy.blocklimiter.track.user;

import reghzy.blocklimiter.track.block.TrackedBlock;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.utils.collections.multimap.HashSetMultiMap;
import reghzy.blocklimiter.utils.collections.multimap.MultiMapEntrySet;

import java.util.Collection;
import java.util.HashSet;

public class UserBlockData {
    private final User user;
    private final HashSetMultiMap<BlockDataPair, TrackedBlock> placedBlocks;

    public UserBlockData(User user) {
        this.user = user;
        this.placedBlocks = new HashSetMultiMap<BlockDataPair, TrackedBlock>();
    }

    public int getPlacedBlocks(BlockDataPair blockData) {
        return placedBlocks.valuesSize(blockData);
    }

    public int getPlacedIDs() {
        return placedBlocks.keysSize();
    }

    public HashSet<TrackedBlock> getBlocks(BlockDataPair blockData) {
        return placedBlocks.getValues(blockData);
    }

    public Collection<MultiMapEntrySet<BlockDataPair, TrackedBlock>> getBlockEntries() {
        return placedBlocks.getEntrySet();
    }

    public boolean addBlocks(BlockDataPair blockData, Collection<TrackedBlock> blocks) {
        return placedBlocks.putAll(blockData, blocks);
    }

    public boolean addBlock(TrackedBlock block) {
        return placedBlocks.put(block.getBlockData(), block);
    }

    public boolean removeBlock(TrackedBlock block) {
        return placedBlocks.remove(block.getBlockData(), block);
    }

    public boolean removeBlock(BlockDataPair dataPair, TrackedBlock location) {
        return placedBlocks.remove(dataPair, location);
    }

    public boolean shouldUnload() {
        return getPlacedIDs() == 0;
    }

    public User getUser() {
        return user;
    }
}
