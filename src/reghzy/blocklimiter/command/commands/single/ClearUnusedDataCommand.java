package reghzy.blocklimiter.command.commands.single;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import reghzy.api.commands.ExecutableCommand;
import reghzy.api.commands.utils.CommandArgs;
import reghzy.api.commands.utils.RZLogger;
import reghzy.blocklimiter.command.BPLPermission;
import reghzy.blocklimiter.track.user.data.PlayerDataLoader;
import reghzy.api.permission.IPermission;

import java.io.File;

public class ClearUnusedDataCommand extends ExecutableCommand {
    public ClearUnusedDataCommand() {
        super("bpl", null, "clear", null, "Clears player files if they have no limits (to increase loading/unloading speed)");
    }

    @Override
    public IPermission getPermission() {
        return BPLPermission.CLEAR_UNUSED;
    }

    @Override
    public void execute(CommandSender sender, RZLogger logger, CommandArgs args) {
        File directory = PlayerDataLoader.PLAYER_DATA_FOLDER;
        if (!directory.exists()) {
            logger.logFormat("The player data folder directory doesn't exist!");
            return;
        }

        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            logger.logFormat("There were no files!");
            return;
        }

        int totalCount = 0;
        int cleared = 0;
        for (File file : files) {
            totalCount++;
            if (file.length() < 4) {
                String fileName = file.getName();
                String userName = fileName.substring(0, fileName.lastIndexOf("."));
                Player player = Bukkit.getPlayer(userName);
                if (player == null || (!player.isOnline())) {
                    if (file.delete()) {
                        cleared++;
                    }
                }
            }
        }

        logger.logTranslate("&6There were &3" + totalCount + " &6total files. Cleared &3" + cleared + " &6of them");
    }
}
