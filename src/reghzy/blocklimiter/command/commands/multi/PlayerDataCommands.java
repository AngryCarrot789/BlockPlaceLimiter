package reghzy.blocklimiter.command.commands.multi;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import reghzy.api.commands.ExecutableCommand;
import reghzy.api.commands.ExecutableSubCommands;
import reghzy.api.commands.exception.Assert;
import reghzy.api.commands.utils.CommandArgs;
import reghzy.api.commands.utils.CommandMappedArgs;
import reghzy.api.commands.utils.RZLogger;
import reghzy.blocklimiter.command.BPLPermission;
import reghzy.blocklimiter.track.ServerTracker;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.track.user.UserBlockData;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.world.TrackedBlock;
import reghzy.blocklimiter.track.world.Vector3;
import reghzy.blocklimiter.track.world.WorldTracker;
import reghzy.blocklimiter.utils.collections.multimap.MultiMapEntry;
import reghzy.api.permission.IPermission;

public class PlayerDataCommands extends ExecutableSubCommands {
    public PlayerDataCommands() {
        super("bpl", "players", "Provides commands for viewing player data, e.g. placed blocks");
    }

    @Override
    public void registerCommands() {
        registerClass(DisplayBasicInfoSubCommand.class);
        registerClass(DisplayAdvancedInfoSubCommand.class);
        registerClass(SetBlockOwnerSubCommand.class);
        registerClass(RemoveBlockSubCommand.class);
    }

    @Override
    public IPermission getPermission() {
        return BPLPermission.playerDataCommands;
    }

    public static class DisplayBasicInfoSubCommand extends ExecutableCommand {
        public DisplayBasicInfoSubCommand() {
            super("bpl", "players", "at", "<xyz:xyz> [w:world]", "Displays the data for a specific player");
        }

        @Override
        public void execute(CommandSender sender, RZLogger logger, CommandArgs args) {
            Assert.biggerThanOrEqual(args.getArgsLength(), 3, "You haven't provided enough arguments. You must supply atleast the x, y and z coords");

            CommandMappedArgs mappedArgs = args.getMappedArgs();
            int x = mappedArgs.assertGetInteger("x", "Problem with the X coordinate!");
            int y = mappedArgs.assertGetInteger("y", "Problem with the Y coordinate!");
            int z = mappedArgs.assertGetInteger("z", "Problem with the Z coordinate!");
            String worldName = args.getString(3);
            if (worldName == null) {
                if (sender instanceof Player) {
                    worldName = ((Player) sender).getWorld().getName();
                }
                else {
                    logger.logFormat("Console must provide a world name!");
                    return;
                }
            }

            TrackedBlock block = ServerTracker.getInstance().getBlockAt(worldName, x, y, z);
            if (block == null) {
                logger.logFormat("The block at those coordinates isn't tracked");
                return;
            }

            logger.logFormat("Player: " + block.getOwner().getName());
        }

        @Override
        public IPermission getPermission() {
            return BPLPermission.LC_display;
        }
    }

    public static class DisplayAdvancedInfoSubCommand extends ExecutableCommand {
        public DisplayAdvancedInfoSubCommand() {
            super("bpl", "players", "advanced", "<player> [ID:Meta]", "Displays all of the data for a specific player");
        }

        @Override
        public IPermission getPermission() {
            return BPLPermission.PDC_advanced;
        }

        @Override
        public void execute(CommandSender sender, RZLogger logger, CommandArgs args) {
            if (args.getArgsLength() < 1) {
                logger.logFormat("You haven't provided a player name");
                return;
            }

            String name = args.getString(0);
            User user = ServerTracker.getInstance().getUserManager().getUser(name);
            UserBlockData data = user.getData();
            logger.logFormat("&6Username: &3{0}", user.getName());
            logger.logFormat("&6Total placed (unique) limited blocks: &3{0}", data.getPlacedIDs());
            logger.logFormat("&6And those are: &3------------------------------");
            for(MultiMapEntry<BlockDataPair, TrackedBlock> entries : data.getBlockEntries()) {
                logger.logFormat("  &6Block ID:Meta: &3{0}", entries.getKey().toString());
                logger.logFormat("  &6Placed number of this block: &3{0}", entries.getValues().size());
                logger.logFormat("  &6Placed locations:");
                for(TrackedBlock block : entries.getValues()) {
                    logger.logFormat("    &6World: &3{0}&6, Location: {1}", block.getWorldName(), block.getLocation().formatColour());
                }
            }
            logger.logTranslate("&3---------------------------------------------");
        }
    }

    public static class RemoveBlockSubCommand extends ExecutableCommand {
        public RemoveBlockSubCommand() {
            super("bpl", "players", "remove", "<xyz:xyz> [w:world] [b:break]",
                  "Removes a block at the given X/Y/Z coordinates (in the specific world, or the one you're in)",
                  "It will automatically get the tracked block at that location and remove it from the owner's counter",
                  "The break param breaks the block in the world, false by default");
        }

        @Override
        public IPermission getPermission() {
            return BPLPermission.PDC_removeBlock;
        }

        @Override
        public void execute(CommandSender sender, RZLogger logger, CommandArgs args) {
            Assert.biggerThanOrEqual(args.getArgsLength(), 3, "You haven't supplied enough arguments for the X, Y, Z or World value");
            CommandMappedArgs mappedArgs = args.getMappedArgs();
            int x = mappedArgs.assertGetInteger("x", "You haven't provided an integer as the X value");
            int y = mappedArgs.assertGetInteger("y", "You haven't provided an integer as the Y value");
            int z = mappedArgs.assertGetInteger("z", "You haven't provided an integer as the Z value");
            String worldName = args.getString(3);
            if (worldName == null) {
                if (sender instanceof Player) {
                    worldName = ((Player) sender).getWorld().getName();
                }
                else {
                    logger.logFormat("You haven't provided a world name");
                    return;
                }
            }

            Boolean breakBlock = args.getBoolean(4);
            if (breakBlock == null) {
                breakBlock = false;
            }

            Vector3 loc = new Vector3(x, y, z);
            ServerTracker serverTracker = ServerTracker.getInstance();
            TrackedBlock block = serverTracker.breakBlockAt(worldName, loc);
            if (block == null) {
                logger.logFormat("&4Failed to break the block, &6there was no tracked block in '&3{0}&6' at the location &c{1}, &a{2}, &b{3}", worldName, x, y, z);
            }
            else {
                if (breakBlock) {
                    World world = block.getBukkitWorld();
                    if (world == null) {
                        logger.logFormat("&6Removed '&a{0}&6' from &e{1}&6's counter at &c{2}&6, &a{3}&6, &b{4}", block.getBlockData().toString(), block.getOwner().getName(), x, y, z);
                        logger.logFormat("&cCould not break the block, as the world was not loaded");
                    }
                    else {
                        block.getBlock(world).breakNaturally();
                        logger.logFormat("&6Broke and removed the block '&a{0}&6' from &e{1}&6's counter at &c{2}&6, &a{3}&6, &b{4}", block.getBlockData().toString(), block.getOwner().getName(), x, y, z);
                    }
                }
                else {
                    logger.logFormat("&6Removed '&a{0}&6' from &e{1}&6's counter at &c{2}&6, &a{3}&6, &b{4} &6(Did not break the block though)", block.getBlockData().toString(), block.getOwner().getName(), x, y, z);
                }

                logger.logFormat("&6The owner of that block was: &3{0}", block.getOwner().getName());
            }
        }
    }

    public static class SetBlockOwnerSubCommand extends ExecutableCommand {
        public SetBlockOwnerSubCommand() {
            super("bpl", "players", "setowner", "<n:username> <xyz:xyz> [w:world]", "Sets the owner of the block to the given player's name (must be their exact name)");
        }

        @Override
        public IPermission getPermission() {
            return BPLPermission.PDC_setOwner;
        }

        @Override
        public void execute(CommandSender sender, RZLogger logger, CommandArgs args) {
            Assert.biggerThanOrEqual(args.getArgsLength(), 4, "You must provide atleast 4 arguments (x, y, z and player name)");
            CommandMappedArgs mappedArgs = args.getMappedArgs();
            String playerName = mappedArgs.assertGetString("n", "You must provide the username of who to set the block owner to");
            int x = mappedArgs.assertGetInteger("x", "You must provide an X coordinate");
            int y = mappedArgs.assertGetInteger("y", "You must provide an Y coordinate");
            int z = mappedArgs.assertGetInteger("z", "You must provide an Z coordinate");
            String worldName = mappedArgs.getString("w");
            if (worldName == null) {
                if (sender instanceof Player) {
                    worldName = ((Player) sender).getWorld().getName();
                }
                else {
                    logger.logFormat("You haven't provided a world! Mr console >:(");
                    return;
                }
            }

            ServerTracker serverTracker = ServerTracker.getInstance();
            WorldTracker worldTracker = serverTracker.getWorldTracker(worldName);
            Vector3 loc = new Vector3(x, y, z);
            if (worldTracker.getBlock(loc) == null) {
                logger.logFormat("&4The block at &c{0}&4, &a{1}&4, &b{2} &4wasn't a tracked block (no-one owned it)", x, y, z);
                return;
            }

            TrackedBlock block = serverTracker.breakBlockAt(worldName, loc);
            if (block == null) {
                logger.logFormat("&4The block at &c{0}&4, &a{1}&4, &b{2} &4wasn't tracked... Cannot set the owner!");
                return;
            }
            else {
                logger.logFormat("&6Broke '&3{0}&6' at {1} &6in world &3{2}", block.getBlockData().toString(), block.getLocation().formatColour(), block.getWorldName());
                logger.logFormat("&6The owner of that block was: &3" + block.getOwner().getName());
            }

            block = serverTracker.placeNewBlockAt(worldName, serverTracker.getUserManager().getUser(playerName), block.getBlockData(), block.getLocation());
            logger.logFormat("&6Placed &3'{0}&6' at {1} &6in world &3{2}", block.getBlockData(), block.getLocation().formatColour(), block.getWorldName());
            logger.logFormat("&6The owner of that block is now: &3" + block.getOwner().getName());
        }
    }
}
