package reghzy.blocklimiter.command.commands.multi;

import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import reghzy.blocklimiter.BlockPlaceLimiterPlugin;
import reghzy.blocklimiter.command.utils.CommandArgs;
import reghzy.blocklimiter.command.CommandLogger;
import reghzy.blocklimiter.command.ExecutableCommand;
import reghzy.blocklimiter.command.ExecutableSubCommands;
import reghzy.blocklimiter.exceptions.BlockAlreadyBrokenException;
import reghzy.blocklimiter.exceptions.BlockAlreadyPlacedException;
import reghzy.blocklimiter.track.ServerBlockTracker;
import reghzy.blocklimiter.track.block.TrackedBlock;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.track.user.UserBlockData;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.world.Vector3;
import reghzy.blocklimiter.track.world.WorldBlockTracker;
import reghzy.blocklimiter.utils.WorldEditHelper;
import reghzy.blocklimiter.utils.WorldHelper;
import reghzy.blocklimiter.utils.collections.multimap.MultiMapEntrySet;
import reghzy.blocklimiter.utils.permissions.PermissionsHelper;

import java.util.ArrayList;
import java.util.Locale;

public class PlayerDataCommands extends ExecutableSubCommands {
    public PlayerDataCommands() {
        super("players", "Provides commands for viewing player data, e.g. placed blocks");
    }

    @Override
    public void registerCommands() {
        registerCommand("basic", new DisplayBasicInfoSubCommand());
        registerCommand("advanced", new DisplayAdvancedInfoSubCommand());
        registerCommand("setowner", new SetBlockOwnerSubCommand());
        //registerCommand("setownerwe", new SetBlockOwnerWorldEditSubCommand());
        registerCommand("remove", new RemoveBlockSubCommand());
        //registerCommand("removewe", new RemoveBlocksWorldEditSubCommand());
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
            User user = ServerBlockTracker.getInstance().getUserManager().getUser(name);
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
            super("players", "remove", "<x> <y> <z> [world]",
                  "Removes a block at the given X/Y/Z coordinates",
                  "It will automatically get the tracked block at that location and remove it from the owner's counter");
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

            ServerBlockTracker serverBlockTracker = ServerBlockTracker.getInstance();
            WorldBlockTracker worldBlockTracker = serverBlockTracker.getWorldTracker(worldName);
            try {
                TrackedBlock block = worldBlockTracker.breakBlock(location);
                logger.logTranslate("&6Broke &3" + block.getBlockData().toString() + " &6at " + block.getLocation().formatColour() + " &6in world &3" + block.getWorldName());
                logger.logTranslate("&6The owner of that block was: &3" + block.getOwner().getName());
            }
            catch (BlockAlreadyBrokenException e) {
                logger.logTranslate("&4That block wasnt a tracked block (aka owned by noone)");
            }
        }
    }

    private static class RemoveBlocksWorldEditSubCommand extends ExecutableCommand {
        public RemoveBlocksWorldEditSubCommand() {
            super("players", "removewe", "[break]",
                  "Removes all of the tracked blocks between the min and max values of your worldedit selection (if theyre not tracked, theyre not removed)",
                  "Optional parameter to break the TRACKED block (set it to air). default value of true");
        }

        @Override
        public void execute(CommandSender sender, CommandLogger logger, CommandArgs args) {
            if (!PermissionsHelper.isConsoleOrHasPermsOrOp(sender, BlockPlaceLimiterPlugin.PlayerEditorPermission)) {
                logger.logGold("You dont have permission for this command!");
                return;
            }

            if (!(sender instanceof Player)) {
                logger.logConsole("You're not a player lol");
                return;
            }

            Boolean breakBlocks = args.getBoolean(0);
            if (breakBlocks == null) {
                breakBlocks = true;
            }

            Selection selection = WorldEditHelper.getPlayerSelection((Player) sender);
            if (selection == null) {
                logger.logTranslate("You haven't selected any blocks");
                return;
            }

            //Location pos = selection.getMinimumPoint();
            //if (selection == null) {
            //    logger.logTranslate("You havent selected a block: minimum po");
            //    return;
            //}
            //
            //String worldName = .getWorld().getName();
            //Vector3 location = new Vector3(selection.getMinimumPoint());
            //ServerBlockTracker serverBlockTracker = ServerBlockTracker.getInstance();
            //WorldBlockTracker worldBlockTracker = serverBlockTracker.getWorldTracker(worldName);
            //try {
            //    TrackedBlock block = worldBlockTracker.breakBlock(location);
            //    logger.logTranslate("&6Broke &3" + block.getBlockData().toString() + " &6at " + block.getLocation().formatColour() + " &6in world &3" + block.getWorldName());
            //    logger.logTranslate("&6The owner of that block was: &3" + block.getOwner().getName());
            //}
            //catch (BlockAlreadyBrokenException e) {
            //    logger.logTranslate("&4That block wasnt a tracked block (aka owned by noone)");
            //}
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

            ServerBlockTracker serverBlockTracker = ServerBlockTracker.getInstance();
            WorldBlockTracker worldBlockTracker = serverBlockTracker.getWorldTracker(worldName);
            if (worldBlockTracker.getBlock(location) == null) {
                logger.logTranslate("&4That block wasnt a tracked block (aka owned by noone)");
                return;
            }

            TrackedBlock block;
            try {
                block = worldBlockTracker.breakBlock(location);
                logger.logTranslate("&6Broke &3" + block.getBlockData().toString() + " &6at " + block.getLocation().formatColour() + " &6in world &3" + block.getWorldName());
                logger.logTranslate("&6The owner of that block was: &3" + block.getOwner().getName());
            }
            catch (BlockAlreadyBrokenException e) {
                logger.logTranslate("&4That block wasnt a tracked block (aka owned by noone)");
                return;
            }

            try {
                block = serverBlockTracker.placeBlockAt(worldBlockTracker, serverBlockTracker.getUserManager().getUser(playerName), block.getBlockData(), block.getLocation());
                logger.logTranslate("&6Placed &3" + block.getBlockData().toString() + " &6at " + block.getLocation().formatColour() + " &6in world &3" + block.getWorldName());
                logger.logTranslate("&6The owner of that block is now: &3" + block.getOwner().getName());
            }
            catch (BlockAlreadyPlacedException e) {
                logger.logTranslate("&4That block was already placed");
                return;
            }
        }
    }

    private static class SetBlockOwnerWorldEditSubCommand extends ExecutableCommand {
        public SetBlockOwnerWorldEditSubCommand() {
            super("players", "setownerwe", "<player name>", "Sets the owner of the blocks you selected in your worldedit selection as the given player (must be their exact name)");
        }

        @Override
        public void execute(CommandSender sender, CommandLogger logger, CommandArgs args) {
            if (!PermissionsHelper.isConsoleOrHasPermsOrOp(sender, BlockPlaceLimiterPlugin.PlayerEditorPermission)) {
                logger.logGold("You dont have permission for this command!");
                return;
            }

            if (!(sender instanceof Player)) {
                logger.logConsole("You're not a player lol");
                return;
            }

            String username = args.getString(0);
            if (username == null) {
                logger.logConsole("You haven't provided a username");
                return;
            }

            Selection selection = WorldEditHelper.getPlayerSelection((Player) sender);
            if (selection == null) {
                logger.logTranslate("You haven't selected any blocks");
                return;
            }

            ArrayList<Block> blocks = WorldHelper.blocksBetweenSelection(selection);
            if (blocks == null || blocks.size() == 0) {
                logger.logTranslate("&6You havent selected any blocks");
                return;
            }

            World world = ((Player) sender).getWorld();
            ServerBlockTracker serverBlockTracker = ServerBlockTracker.getInstance();
            WorldBlockTracker worldBlockTracker = serverBlockTracker.getWorldTracker(world);

            User user = serverBlockTracker.getUserManager().getUser(username);

            for (Block bukkitBlock : blocks) {
                TrackedBlock block = worldBlockTracker.getBlock(bukkitBlock);
                if (block == null) {
                    continue;
                }

                Vector3 location = block.getLocation();
                try {
                    block = worldBlockTracker.breakBlock(location);
                    logger.logTranslate("&6Broke &3" + block.getBlockData().toString() + " &6at " + block.getLocation().formatColour() + " &6in world &3" + block.getWorldName());
                    logger.logTranslate("&6The owner of that block was: &3" + block.getOwner().getName());
                }
                catch (BlockAlreadyBrokenException e) {

                }

                try {
                    block = serverBlockTracker.placeBlockAt(worldBlockTracker, user, block.getBlockData(), block.getLocation());
                    logger.logTranslate("&6Placed &3" + block.getBlockData().toString() + " &6at " + block.getLocation().formatColour() + " &6in world &3" + block.getWorldName());
                    logger.logTranslate("&6The owner of that block is now: &3" + block.getOwner().getName());
                }
                catch (BlockAlreadyPlacedException e) {
                    logger.logTranslate("&4That block was already placed");
                    return;
                }
            }
        }
    }
}
