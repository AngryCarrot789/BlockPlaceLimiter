package reghzy.blocklimiter.tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import reghzy.api.config.Config;
import reghzy.api.utils.ExceptionHelper;
import reghzy.blocklimiter.exceptions.FailedFileCreationException;
import reghzy.blocklimiter.track.ServerTracker;
import reghzy.blocklimiter.track.user.data.PlayerDataLoader;

import java.io.IOException;

public class ConfigSaveTask implements Runnable {
    private final JavaPlugin plugin;
    private final ServerTracker serverTracker;
    private int taskId;
    private static int delayTicks = 100;

    public static final String DelayTicksName = "SaveAllConfigsInterval";

    public ConfigSaveTask(JavaPlugin plugin, ServerTracker serverTracker) {
        this.plugin = plugin;
        this.serverTracker = serverTracker;
    }

    public static void loadConfig(Config mainConfig) {
        delayTicks = mainConfig.getInt(DelayTicksName);
    }

    public void startTask() {
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, delayTicks);
    }

    public void stopTask() {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

    public void restartTask() {
        stopTask();
        startTask();
    }

    @Override
    public void run() {
        try {
            int saved = serverTracker.savePlayerData(PlayerDataLoader.PLAYER_DATA_FOLDER, false);
            // if (saved > 0) {
            //     ChatLogger.logPlugin("Saved " + saved + " user data files");
            // }
        }
        catch (IOException ioException) {
            ExceptionHelper.printException(ioException);
        }
        catch (FailedFileCreationException e) {
            ExceptionHelper.printException(e);
        }
    }
}
