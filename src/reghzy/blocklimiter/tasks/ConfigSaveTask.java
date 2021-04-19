package reghzy.blocklimiter.tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import reghzy.blocklimiter.config.Config;
import reghzy.blocklimiter.config.ConfigManager;
import reghzy.blocklimiter.exceptions.FailedFileCreationException;
import reghzy.blocklimiter.track.ServerTracker;
import reghzy.blocklimiter.track.user.PlayerDataLoader;
import reghzy.blocklimiter.utils.debug.Debugger;

import java.io.IOException;

public class ConfigSaveTask implements Runnable {
    private final JavaPlugin plugin;
    private int taskId;
    private ServerTracker serverTracker;
    private int delayTicks = 300;

    public static final String DelayTicksName = "SaveAllConfigsInterval";

    public ConfigSaveTask(JavaPlugin plugin, ServerTracker serverTracker) {
        this.plugin = plugin;
        this.serverTracker = serverTracker;
        loadConfig(ConfigManager.getMainConfig());
    }

    public void loadConfig(Config mainConfig) {
        this.delayTicks = mainConfig.getInt(DelayTicksName);
        restartTask();
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
            int saved = serverTracker.savePlayerData(PlayerDataLoader.PlayerDataFolder);
            Debugger.log("Saved " + saved + " user's data");
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
        catch (FailedFileCreationException e) {
            e.printStackTrace();
        }
    }
}
