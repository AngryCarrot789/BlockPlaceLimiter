package reghzy.blocklimiter.command.commands.multi.fix;

import guava10.com.google.common.collect.ArrayListMultimap;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import reghzy.api.commands.ExecutableCommand;
import reghzy.api.commands.ExecutableSubCommands;
import reghzy.api.commands.utils.CommandArgs;
import reghzy.api.commands.utils.RZLogger;
import reghzy.api.commands.utils.SWRCDataV2;
import reghzy.api.permission.IPermission;
import reghzy.api.utils.NMSAPI;
import reghzy.api.utils.types.StringPair;
import reghzy.blocklimiter.command.BPLPermission;
import reghzy.blocklimiter.command.commands.multi.fix.results.FixedTrackedBlockResult;
import reghzy.blocklimiter.command.commands.multi.fix.results.NonExistentTrackedBlockResult;
import reghzy.blocklimiter.command.commands.multi.fix.results.OverlimitedBlockResult;
import reghzy.blocklimiter.limit.BlockLimiter;
import reghzy.blocklimiter.limit.LimitManager;
import reghzy.blocklimiter.limit.MetaLimiter;
import reghzy.blocklimiter.track.ServerTracker;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.world.TrackedBlock;
import reghzy.blocklimiter.track.world.BPLVec3i;
import reghzy.blocklimiter.track.world.WorldTracker;
import reghzy.carrottools.playerdata.PlayerData;
import reghzy.carrottools.playerdata.PlayerRegister;
import reghzy.carrottools.playerdata.results.ResultLine;

import java.util.ArrayList;
import java.util.Map;

public class FixCommand extends ExecutableSubCommands {
    public FixCommand() {
        super("bpl", "fix", "Commands for fixing unsynced blocks due to corruption");
    }

    @Override
    public void registerCommands() {
        registerClass(FindNonExistentBlocksCommand.class);
        registerClass(BatchSetOwnerCommand.class);
    }

    @Override
    public IPermission getPermission() {
        return BPLPermission.PDC_advanced;
    }

    public static class FindNonExistentBlocksCommand extends ExecutableCommand {
        public FindNonExistentBlocksCommand() {
            super("bpl", "fix", "findnonexistant", "<swrc> [-b] [-i]", "Finds blocks that don't exist");
            this.formattedArgs = new StringPair[] {
                    new StringPair("-b", "Check if the TileEntity owner bypasses the limit, e.g if they have the '*' permission, then ignore it. This should usually always be provided"),
                    new StringPair("-i", "Ignores cases where the TileEntity's NBT Owner is null/unavailable. This should NEVER usually be provided, because you can't do much without the owner")
            };
        }

        @Override
        public IPermission getPermission() {
            return BPLPermission.PDC_advanced;
        }

        @Override
        public void execute(CommandSender sender, RZLogger logger, CommandArgs args) {
            boolean checkBypass = args.hasFlag('b');
            boolean ignoreNoOwner = args.hasFlag('i');
            LimitManager manager = LimitManager.getInstance();
            ServerTracker serverTracker = ServerTracker.getInstance();
            ArrayListMultimap<World, Chunk> map = SWRCDataV2.swrc().generateWorldChunk(sender, args);
            PlayerData data = PlayerRegister.getData(sender);
            data.clearResults();
            for(World world : map.keySet()) {
                WorldTracker tracker = serverTracker.getWorldTracker(world);
                for(Chunk chunk : map.get(world)) {
                    Map<ChunkPosition, TileEntity> tileMap = NMSAPI.getTileEntityMap(chunk);
                    for(Map.Entry<ChunkPosition, TileEntity> entry : tileMap.entrySet()) {
                        TileEntity tile = entry.getValue();
                        TrackedBlock tracked = tracker.getBlock(tile.field_70329_l, tile.field_70330_m, tile.field_70327_n);
                        if (tracked != null) {
                            continue;
                        }

                        BlockLimiter limiter = manager.getLimiter(NMSAPI.getBlockId(chunk, tile.field_70329_l, tile.field_70330_m, tile.field_70327_n));
                        if (limiter == null) {
                            continue;
                        }

                        MetaLimiter metaLimit = limiter.getMetaLimit(NMSAPI.getBlockData(chunk, tile.field_70329_l, tile.field_70330_m, tile.field_70327_n));
                        if (metaLimit == null) {
                            continue;
                        }

                        NBTTagCompound nbt = NMSAPI.writeToNbt(tile);
                        String tag = null;
                        String owner;
                        if (nbt.func_74764_b("owner")) {
                            tag = "owner";
                        }
                        else if (nbt.func_74764_b("Owner")) {
                            tag = "Owner";
                        }

                        if (tag == null) {
                            if (ignoreNoOwner) {
                                owner = null;
                            }
                            else {
                                continue;
                            }
                        }
                        else {
                            NBTBase ownerNbt = nbt.func_74781_a(tag);
                            if (ownerNbt instanceof NBTTagString) {
                                owner = ((NBTTagString) ownerNbt).field_74751_a;
                            }
                            else if (ignoreNoOwner) {
                                owner = null;
                            }
                            else {
                                continue;
                            }
                        }

                        if (!ignoreNoOwner && checkBypass && metaLimit.playerBypassesChecks(NMSAPI.getWorldName(tile), owner)) {
                            continue;
                        }

                        data.addResult(new NonExistentTrackedBlockResult(tile, limiter, metaLimit, owner));
                    }
                }
            }

            data.updateHeader(NonExistentTrackedBlockResult.HEADER);
            data.printFormatFirstPageAfterAction(sender, logger);
        }
    }

    public static class BatchSetOwnerCommand extends ExecutableCommand {
        public BatchSetOwnerCommand() {
            super("bpl", "fix", "batchsetowners", "[-s] [-cl]",
                  "Ensure you have results from /bpl fix findnonexistant",
                  "As long as the TileEntity owner is available, it will create the tracked blocks based on that owner");
            this.formattedArgs = new StringPair[] {
                    new StringPair("-s", "If the tracked block somehow exists now, then ignore it. This should be provided, because you don't want to re-create a tracked block"),
                    new StringPair("-cl", "If the player cannot place anymore blocks, and this is true, then ignore the block. This should be provided, allowing you to manually break the existing blocks (e.g if they cheated)")
            };
        }

        @Override
        public IPermission getPermission() {
            return BPLPermission.PDC_advanced;
        }

        @Override
        public void execute(CommandSender sender, RZLogger logger, CommandArgs args) {
            boolean skipExisting = args.hasFlag('s');
            boolean checkLimitExceeded = args.hasFlag("cl");
            ServerTracker serverTracker = ServerTracker.getInstance();
            PlayerData data = PlayerRegister.getData(sender);
            ArrayList<ResultLine> toAddResults = new ArrayList<ResultLine>(data.getResults().size());
            for(ResultLine line : data.getResults()) {
                if (line instanceof NonExistentTrackedBlockResult) {
                    NonExistentTrackedBlockResult result = (NonExistentTrackedBlockResult) line;
                    String owner = result.getNbtOwner();
                    String worldName = result.getWorldName();
                    if (owner == null) {
                        continue;
                    }

                    if (skipExisting && result.getTrackedBlock() != null) {
                        continue;
                    }

                    MetaLimiter limiter = result.getMetaLimit();
                    BlockDataPair itemData = result.getMetaLimit().dataPair;
                    User user = serverTracker.getUserManager().getUser(owner);
                    int x = result.getX();
                    int y = result.getY();
                    int z = result.getZ();

                    if (checkLimitExceeded) {
                        if (!limiter.canPlayerPlaceNoLog(owner, worldName, user.getData().getPlacedBlocks(itemData))) {
                            toAddResults.add(new OverlimitedBlockResult(result.getTileEntity(), result.getLimiter(), result.getMetaLimit(), result.getNbtOwner()));
                            continue;
                        }
                    }

                    TrackedBlock newBlock = serverTracker.placeNewBlockAt(worldName, user, itemData, new BPLVec3i(x, y, z));
                    toAddResults.add(new FixedTrackedBlockResult(newBlock, result.getTileEntity(), result.getLimiter(), result.getMetaLimit(), result.getNbtOwner()));
                }
            }

            data.clearResults();
            data.getResults().addAll(toAddResults);
            data.printFormatFirstPageAfterAction(sender, logger);
        }
    }
}
