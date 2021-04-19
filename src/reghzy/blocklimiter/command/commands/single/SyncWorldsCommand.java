package reghzy.blocklimiter.command.commands.single;

import org.bukkit.command.CommandSender;
import reghzy.blocklimiter.BlockPlaceLimiterPlugin;
import reghzy.blocklimiter.command.utils.CommandArgs;
import reghzy.blocklimiter.command.CommandLogger;
import reghzy.blocklimiter.command.ExecutableCommand;
import reghzy.blocklimiter.track.Synchroniser;
import reghzy.blocklimiter.track.world.TrackedBlock;
import reghzy.blocklimiter.utils.permissions.PermissionsHelper;

import java.util.ArrayList;

public class SyncWorldsCommand extends ExecutableCommand {
    public SyncWorldsCommand() {
        super(null, "sync", null, "Searches every tracked block in every world and checks if those blocks dont exist in the minecraft world (maybe due to worldedit)", "It then un-counts them from the owner of the block");
    }

    @Override
    public void execute(CommandSender sender, CommandLogger logger, CommandArgs args) {
        if (!PermissionsHelper.isConsoleOrHasPermsOrOp(sender, BlockPlaceLimiterPlugin.SyncCommandPermission)) {
            logger.logTranslate("&4You dont have permission for this command!");
            return;
        }

        ArrayList<TrackedBlock> unsynced = Synchroniser.scanForUnsynedBlocks();
        if (unsynced.size() > 0) {
            logger.logTranslate("&6Found &3" + unsynced.size() + " &6Unsynced blocks! They have been removed from the player's counter. The blocks:");
            for (TrackedBlock block : unsynced) {
                logger.logTranslate("&6ID/Meta: &3" + block.getBlockData().toString() +
                                    "&6, World: &3" + block.getWorldName() +
                                    "&6, Location: " + block.getLocation().formatColour());
            }
        }
        else {
            logger.logTranslate("&6Found no unsynced blocks!");
        }
    }
}
