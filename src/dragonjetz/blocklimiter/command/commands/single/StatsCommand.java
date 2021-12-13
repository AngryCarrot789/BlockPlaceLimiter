package dragonjetz.blocklimiter.command.commands.single;

import org.bukkit.command.CommandSender;
import dragonjetz.api.commands.ExecutableCommand;
import dragonjetz.api.commands.utils.CommandArgs;
import dragonjetz.api.commands.utils.DJLogger;
import dragonjetz.api.utils.MathsHelper;
import dragonjetz.blocklimiter.command.BPLPermission;
import dragonjetz.blocklimiter.track.ServerTracker;
import dragonjetz.blocklimiter.track.user.User;
import dragonjetz.blocklimiter.track.utils.BlockDataPair;
import dragonjetz.blocklimiter.track.world.TrackedBlock;
import dragonjetz.blocklimiter.track.world.WorldTracker;
import dragonjetz.blocklimiter.utils.collections.multimap.MultiMapEntry;
import dragonjetz.api.permission.IPermission;

import java.util.Collection;

public class StatsCommand extends ExecutableCommand {
    public StatsCommand() {
        super("bpl", null, "stats", null, "Gives in-depth data about all the block limits and users currently loaded in RAM");
    }

    @Override
    public IPermission getPermission() {
        return BPLPermission.PLAYER_STATS;
    }

    @Override
    public void execute(CommandSender sender, DJLogger logger, CommandArgs args) {
        int bytesCount = 0;
        ServerTracker server = ServerTracker.getInstance();
        Collection<User> users = server.getUserManager().getUsers();
        logger.logFormat("&6There are &e{0} &6users loaded into RAM &5----------", users.size());
        for(User user : users) {
            int totalBlocks = 0;
            for(MultiMapEntry<BlockDataPair, TrackedBlock> entry : user.getData().getBlockEntries()) {
                for(TrackedBlock block : entry.getValues()) {
                    totalBlocks++;
                    bytesCount += (84);
                }
            }

            logger.logFormat("&a{0} &6has &b{1} &6total blocks (&3{2} &6unique IDs)", user.getName(), totalBlocks, user.getData().getPlacedIDs());
            bytesCount += 16;
        }
        logger.logFormat("&5----------------------------------------");
        Collection<WorldTracker> worlds = server.getWorldTrackers();
        logger.logFormat("&6There are &e{0} &6worlds loaded into RAM &5----------", worlds.size());
        for (WorldTracker world : worlds) {
            bytesCount += 2072;
            logger.logFormat("&a{0} &6has &b{1} &6total blocks", world.getWorldName(), world.getAllBlocks().size());
        }
        logger.logFormat("&5----------------------------------------");
        double kb = MathsHelper.round(((float) bytesCount) / 1024.0d, 2);
        double mb = MathsHelper.round(kb / 1024.0d, 2);
        logger.logFormat("&6&lAverage&6 RAM usage is &b{0} &6KB (&3{1} &6MB)", kb, mb);
    }
}
