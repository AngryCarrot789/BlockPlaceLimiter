package reghzy.blocklimiter.command.commands.single;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import reghzy.blocklimiter.command.helpers.CommandArgs;
import reghzy.blocklimiter.command.utils.CommandLogger;
import reghzy.blocklimiter.command.utils.ExecutableCommand;
import reghzy.blocklimiter.track.user.PlayerDataLoader;

import java.io.File;

public class ClearUnusedDataCommand extends ExecutableCommand {
    public ClearUnusedDataCommand() {
        super(null, "clear", null, "Clears player files if they have no limits (to increase loading/unloading speed)");
    }

    @Override
    public void execute(CommandSender sender, CommandLogger logger, CommandArgs args) {
        File directory = PlayerDataLoader.PlayerDataFolder;
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        int totalCount = 0;
        int cleared = 0;
        for (File file : files) {
            totalCount++;
            if (file.length() < 4) {
                String name = file.getName();
                String user = name.substring(0, name.lastIndexOf("."));
                if (Bukkit.getPlayer(user) == null) {
                    cleared++;
                    file.delete();
                }
            }
        }

        logger.logTranslate("&6There were &3" + totalCount + " &6total files. Cleared &3" + cleared + " &6of them");
    }
}
