package reghzy.blocklimiter.command.commands.single;

import org.bukkit.command.CommandSender;
import reghzy.api.commands.ExecutableCommand;
import reghzy.api.commands.utils.CommandArgs;
import reghzy.api.commands.utils.RZLogger;
import reghzy.api.permission.IPermission;
import reghzy.blocklimiter.command.BPLPermission;
import reghzy.blocklimiter.track.Synchroniser;
import reghzy.blocklimiter.track.world.TrackedBlock;

import java.util.List;

public class SyncWorldsCommand extends ExecutableCommand {
    public SyncWorldsCommand() {
        super("bpl", null, "sync", "[-noload] [block:id:meta]",
              "Searches every tracked block in every world and checks if those blocks dont exist in the minecraft world (maybe due to worldedit)",
              "It then un-counts them from the owner of the block",
              "Optionally provide the ID:Data for the type of block to search for, e.g only spotloaders");
    }

    @Override
    public IPermission getPermission() {
        return BPLPermission.SYNC;
    }

    @Override
    public void execute(CommandSender sender, RZLogger logger, CommandArgs args) {
        List<TrackedBlock> unsynced;
        if (args.hasAnyArgs()) {
            unsynced = Synchroniser.scanForUnsynedBlocks(!args.hasFlag("noload"), args.getMappedArgs().assertGetItemData("block"));
        }
        else {
            unsynced = Synchroniser.scanForUnsynedBlocks(true);
        }

        if (unsynced.size() > 0) {
            logger.logTranslate("&6Found &3" + unsynced.size() + " &6Unsynced blocks! They have been removed from the player's counter. The blocks:");
            for (TrackedBlock block : unsynced) {
                logger.logTranslate("&6Owner: &3" + block.getOwner().getName() +
                                    "&6, ID/Meta: &3" + block.getBlockData().toString() +
                                    "&6, World: &3" + block.getWorldName() +
                                    "&6, Location: " + block.getLocation().formatColour());
            }
        }
        else {
            logger.logTranslate("&6Found no unsynced blocks!");
        }
    }
}
