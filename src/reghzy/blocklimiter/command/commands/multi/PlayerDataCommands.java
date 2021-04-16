package reghzy.blocklimiter.command.commands.multi;

import org.bukkit.command.CommandSender;
import reghzy.blocklimiter.BlockPlaceLimiterPlugin;
import reghzy.blocklimiter.command.helpers.CommandArgs;
import reghzy.blocklimiter.command.utils.CommandLogger;
import reghzy.blocklimiter.command.utils.ExecutableCommand;
import reghzy.blocklimiter.command.utils.ExecutableSubCommands;
import reghzy.blocklimiter.track.ServerBlockTracker;
import reghzy.blocklimiter.track.block.TrackedBlock;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.track.user.UserBlockData;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.utils.collections.multimap.MultiMapEntrySet;
import reghzy.blocklimiter.utils.permissions.PermissionsHelper;

public class PlayerDataCommands extends ExecutableSubCommands {
    public PlayerDataCommands() {
        super("player", "Provides commands for viewing player data, e.g. placed blocks");
    }

    @Override
    public void registerCommands() {
        registerCommand("basic", new DisplayBasicInfoSubCommand());
        registerCommand("advanced", new DisplayAdvancedInfoSubCommand());
    }

    private static class DisplayBasicInfoSubCommand extends ExecutableCommand {

        public DisplayBasicInfoSubCommand() {
            super("player", "basic", "<player> <ID:Meta>", "Displays the data for a specific player");
        }

        @Override
        public void execute(CommandSender sender, CommandLogger logger, CommandArgs args) {
            if (!PermissionsHelper.isConsoleOrHasPermsOrOp(sender, BlockPlaceLimiterPlugin.DisplayLimiterPermission)) {
                logger.logGold("You dont have permission for this command!");
                return;
            }

            if (args.getArgsLength() < 2) {
                logger.logGold("You havent provided enough arguments. must supply a player name and ID:Meta (or just ID)");
                return;
            }

            String name = args.getString(0);

            User user = ServerBlockTracker.getInstance().getUserManager().getUser(name);
            UserBlockData data = user.getData();
            logger.logTranslate("&6Username: &3" + user.getName());
            logger.logTranslate("&6Total placed (unique) limited blocks: &3" + data.getPlacedIDs());
            BlockDataPair dataPair = args.getBlockData(1, false);
            if (dataPair != null) {
                int placedTracked = data.getPlacedBlocks(dataPair);
                logger.logTranslate("  &6Total placed of " + dataPair.toString() + ": &3" + placedTracked);
            }
        }
    }

    private static class DisplayAdvancedInfoSubCommand extends ExecutableCommand {
        public DisplayAdvancedInfoSubCommand() {
            super("player", "advanced", "<player> [ID:Meta]", "Displays all of the data for a specific player");
        }

        @Override
        public void execute(CommandSender sender, CommandLogger logger, CommandArgs args) {
            if (!PermissionsHelper.isConsoleOrHasPermsOrOp(sender, BlockPlaceLimiterPlugin.DisplayLimiterPermission)) {
                logger.logGold("You dont have permission for this command!");
                return;
            }

            if (args.getArgsLength() < 1) {
                logger.logGold("You havent provided a player name");
                return;
            }

            String name = args.getString(0);
            User user = ServerBlockTracker.getInstance().getUserManager().getUser(name);
            UserBlockData data = user.getData();
            logger.logTranslate("&6Username: &3" + user.getName());
            logger.logTranslate("&6Total placed (unique) limited blocks: &3" + data.getPlacedIDs());
            logger.logTranslate("&6And those are: &3-------------------------------");
            for(MultiMapEntrySet<BlockDataPair, TrackedBlock> entries : data.getBlockEntries()) {
                logger.logTranslate("  &6Block ID:Meta: &3" + entries.getKey().toString());
                logger.logTranslate("  &6Placed number of this block: &3" + entries.getValues().size());
                logger.logTranslate("  &6Placed locations:");
                for(TrackedBlock block : entries.getValues()) {
                    logger.logTranslate("    &6World: &3 " + block.getWorldName() + "&6, Location: " + block.getLocation().formatColour());
                }
            }
            logger.logTranslate("&3------------------------------------------------");
        }
    }
}
