package reghzy.blocklimiter.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import reghzy.api.commands.SingleCommandExecutor;
import reghzy.api.commands.utils.CommandArgs;
import reghzy.api.commands.utils.RZLogger;
import reghzy.api.permission.PermissionManager;
import reghzy.blocklimiter.command.BPLPermission;
import reghzy.blocklimiter.limit.BlockLimiter;
import reghzy.blocklimiter.limit.LimitManager;
import reghzy.blocklimiter.limit.MetaLimiter;
import reghzy.blocklimiter.track.ServerTracker;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.track.user.UserBlockData;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.utils.IntegerRange;
import reghzy.blocklimiter.track.utils.RangeLimit;
import reghzy.blocklimiter.track.world.TrackedBlock;
import reghzy.blocklimiter.utils.collections.multimap.MultiMapEntry;
import reghzy.api.permission.IPermission;

import java.util.Map;

public class MyPlacedBlocksCommand extends SingleCommandExecutor {
    public MyPlacedBlocksCommand() {
        super("myblocks", null, BPLCommandExecutor.BPLLogger, "Shows all of the limited blocks you have placed (e.g. spotloaders, quarries, etc)");
    }

    @Override
    public IPermission getPermission() {
        return BPLPermission.MY_BLOCKS;
    }

    @Override
    public void execute(CommandSender sender, RZLogger logger, CommandArgs args) {
        String name = sender.getName();
        User user = ServerTracker.getInstance().getUserManager().getUser(name);
        UserBlockData data = user.getData();
        int placed = data.getPlacedIDs();

        if (!(sender instanceof Player)) {
            logger.logFormat("You must be a player to use this!");
            return;
        }

        logger.logFormat("&6Username: &3{0}", user.getName());
        if (placed == 0) {
            logger.logFormat("You have no limited blocks placed!");
        }
        else {
            logger.logFormat("&6Total placed (unique) limited blocks: &3{0}", placed);
            logger.logFormat("&6And those are: &3-------------------------------");
            for (MultiMapEntry<BlockDataPair, TrackedBlock> entries : data.getBlockEntries()) {
                logger.logFormat("  &6Block ID:Meta: &3{0}", entries.getKey().toString());
                int placedOf = entries.getValues().size();
                logger.logFormat("  &6Placed number of this block: &3{0}", placedOf);
                int available = calculateRemainingBlocks((Player) sender, entries.getKey());
                if (available != -1) {
                    logger.logFormat("  &6Remaining blocks: &3{0}", available - placedOf);
                }
                logger.logFormat("  &6Placed locations:");
                for (TrackedBlock block : entries.getValues()) {
                    logger.logFormat("    &6World: &3{0}&6, Location: {1}", block.getWorldName(), block.getLocation().formatColour());
                }
            }
            logger.logFormat("&3------------------------------------------");
        }
        logger.logFormat("&3-------------------------------------------");
    }

    private static int calculateRemainingBlocks(Player player, BlockDataPair key) {
        BlockLimiter blockLimiter = LimitManager.getInstance().getLimiter(key.id);
        if (blockLimiter == null) {
            return -1;
        }

        MetaLimiter limiter = blockLimiter.getMetaLimit(key.data);
        if (limiter == null) {
            return -1;
        }

        int placeable = ServerTracker.getInstance().getUserManager().getBlockData(player.getName()).getPlacedBlocks(limiter.dataPair);
        for(Map.Entry<IntegerRange, RangeLimit> entry : limiter.getRangeLimits().getEntrySets()) {
            if (entry.getKey().max > placeable && PermissionManager.hasPermission(player, entry.getValue().permission)) {
                placeable = entry.getKey().max;
            }
        }

        return placeable;
    }
}
