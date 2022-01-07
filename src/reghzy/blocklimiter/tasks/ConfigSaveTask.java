package reghzy.blocklimiter.tasks;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import reghzy.api.utils.ExceptionHelper;
import reghzy.blocklimiter.exceptions.FailedFileCreationException;
import reghzy.blocklimiter.track.ServerTracker;
import reghzy.blocklimiter.track.user.data.PlayerDataLoader;

import java.io.IOException;

public class ConfigSaveTask implements Runnable {
    private static ConfigSaveTask instance;
    private static int delayTicks = 100;
    private final Plugin plugin;
    private final ServerTracker serverTracker;
    private int taskId;

    public static final String DelayTicksName = "SaveAllConfigsInterval";

    public ConfigSaveTask(Plugin plugin, ServerTracker serverTracker) {
        instance = this;
        this.plugin = plugin;
        this.serverTracker = serverTracker;
    }

    public static void loadConfig(ConfigurationSection mainConfig) {
        delayTicks = mainConfig.getInt(DelayTicksName);
        if (instance != null) {
            instance.restartTask();
        }
    }

    public void startTask() {
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, this, 0, delayTicks);
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
