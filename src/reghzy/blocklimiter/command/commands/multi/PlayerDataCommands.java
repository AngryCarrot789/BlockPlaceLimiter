package reghzy.blocklimiter.command.commands.multi;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import reghzy.blocklimiter.BlockPlaceLimiterPlugin;
import reghzy.blocklimiter.command.CommandLogger;
import reghzy.blocklimiter.command.ExecutableCommand;
import reghzy.blocklimiter.command.ExecutableSubCommands;
import reghzy.blocklimiter.command.utils.CommandArgs;
import reghzy.blocklimiter.track.ServerTracker;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.track.user.UserBlockData;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.world.TrackedBlock;
import reghzy.blocklimiter.track.world.Vector3;
import reghzy.blocklimiter.track.world.WorldTracker;
import reghzy.blocklimiter.utils.collections.multimap.MultiMapEntrySet;
import reghzy.blocklimiter.utils.permissions.PermissionsHelper;

public class PlayerDataCommands extends ExecutableSubCommands {
    public PlayerDataCommands() {
        super("players", "Provides commands for viewing player data, e.g. placed blocks");
    }

    @Override
    public void registerCommands() {
        registerCommand("basic", new DisplayBasicInfoSubCommand());
        registerCommand("advanced", new DisplayAdvancedInfoSubCommand());
        registerCommand("setowner", new SetBlockOwnerSubCommand());
        registerCommand("remove", new RemoveBlockSubCommand());
    }

    private static class DisplayBasicInfoSubCommand extends ExecutableCommand {

        public DisplayBasicInfoSubCommand() {
            super("players", "basic", "<player> <ID:Meta>", "Displays the data for a specific player");
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

            User user = ServerTracker.getInstance().getUserManager().getUser(name);
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
            super("players", "advanced", "<player> [ID:Meta]", "Displays all of the data for a specific player");
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
            User user = ServerTracker.getInstance().getUserManager().getUser(name);
            UserBlockData data = user.getData();
            logger.logTranslate("&6Username: &3" + user.getName());
            logger.logTranslate("&6Total placed (unique) limited blocks: &3" + data.getPlacedIDs());
            logger.logTranslate("&6And those are: &3---------------------------------");
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

    private static class RemoveBlockSubCommand extends ExecutableCommand {
        public RemoveBlockSubCommand() {
            super("players", "remove", "<x> <y> <z> [world] [break]",
                  "Removes a block at the given X/Y/Z coordinates",
                  "It will automatically get the tracked block at that location and remove it from the owner's counter",
                  "(Optionally) break the block in the world");
        }

        @Override
        public void execute(CommandSender sender, CommandLogger logger, CommandArgs args) {
            if (!PermissionsHelper.isConsoleOrHasPermsOrOp(sender, BlockPlaceLimiterPlugin.PlayerEditorPermission)) {
                logger.logGold("You dont have permission for this command!");
                return;
            }

            if (args.getArgsLength() < 3) {
                logger.logGold("You havent supplied enough arguments for the X, Y, Z or World value");
                return;
            }
            Vector3 location = args.getVector3(0);
            if (location == null) {
                logger.logGold("You havent supplied the X, Y and Z coordinates. they cannot have decimals if you used decimals");
                return;
            }
            String worldName = args.getString(3);
            if (worldName == null) {
                if (sender instanceof Player) {
                    worldName = ((Player) sender).getWorld().getName();
                }
                else {
                    logger.logGold("You havent provided a world name");
                    return;
                }
            }

            Boolean breakBlock = args.getBoolean(4);
            if (breakBlock == null) {
                breakBlock = true;
            }

            ServerTracker serverTracker = ServerTracker.getInstance();
            TrackedBlock block = serverTracker.breakBlockAt(worldName, location);
            if (block == null) {
                logger.logTranslate("&4Failed to break the block. &6There was no tracked block at that location (" + location.formatColour() + "&6)");
            }
            else {
                if (breakBlock) {
                    World world = block.getBukkitWorld();
                    if (world != null) {
                        block.getBlock(world).breakNaturally();
                    }
                    logger.logTranslate("&6Broke &3" + block.getBlockData().toString() + " &6at " + block.getLocation().formatColour() + " &6in world &3" + block.getWorldName());
                }
                else {
                    logger.logTranslate("&6Did not break the block, but did remove it from the player's counter: &3" + block.getBlockData().toString() + " &6at " + block.getLocation().formatColour() + " &6in world &3" + block.getWorldName());
                }
                logger.logTranslate("&6The owner of that block was: &3" + block.getOwner().getName());
            }
        }
    }

    private static class SetBlockOwnerSubCommand extends ExecutableCommand {
        public SetBlockOwnerSubCommand() {
            super("players", "setowner", "<player name> <x> <y> <z> [world]", "Sets the owner of the block to the given player's name (must be their exact name)");
        }

        @Override
        public void execute(CommandSender sender, CommandLogger logger, CommandArgs args) {
            if (!PermissionsHelper.isConsoleOrHasPermsOrOp(sender, BlockPlaceLimiterPlugin.PlayerEditorPermission)) {
                logger.logGold("You dont have permission for this command!");
                return;
            }

            if (args.getArgsLength() < 4) {
                logger.logGold("You havent supplied enough arguments for the Player name, X, Y, Z or World value");
                return;
            }

            String playerName = args.getString(0);
            if (playerName == null) {
                logger.logGold("You havent supplied the player's name");
                return;
            }

            Vector3 location = args.getVector3(1);
            if (location == null) {
                logger.logGold("You havent supplied the X, Y and Z coordinates. they cannot have decimals if you used decimals");
                return;
            }

            String worldName = args.getString(4);
            if (worldName == null) {
                if (sender instanceof Player) {
                    worldName = ((Player) sender).getWorld().getName();
                }
                else {
                    logger.logGold("You havent provided a world name");
                    return;
                }
            }

            ServerTracker serverTracker = ServerTracker.getInstance();
            WorldTracker worldTracker = serverTracker.getWorldTracker(worldName);
            if (worldTracker.getBlock(location) == null) {
                logger.logTranslate("&4That block wasnt a tracked block (aka owned by noone)");
                return;
            }

            TrackedBlock block = serverTracker.breakBlockAt(worldName, location);
            if (block == null) {
                logger.logTranslate("&6That block wasn't a tracked block (it might be limited, but wasn't tracked)");
                return;
            }
            else {
                logger.logTranslate("&6Broke &3" + block.getBlockData().toString() + " &6at " + block.getLocation().formatColour() + " &6in world &3" + block.getWorldName());
                logger.logTranslate("&6The owner of that block was: &3" + block.getOwner().getName());
            }

            block = serverTracker.placeNewBlockAt(worldName, serverTracker.getUserManager().getUser(playerName), block.getBlockData(), block.getLocation());
            logger.logTranslate("&6Placed &3" + block.getBlockData().toString() + " &6at " + block.getLocation().formatColour() + " &6in world &3" + block.getWorldName());
            logger.logTranslate("&6The owner of that block is now: &3" + block.getOwner().getName());
        }
    }
}
