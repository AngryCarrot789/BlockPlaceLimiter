package reghzy.blocklimiter.tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import reghzy.blocklimiter.config.Config;
import reghzy.blocklimiter.config.ConfigManager;
import reghzy.blocklimiter.exceptions.FailedFileCreationException;
import reghzy.blocklimiter.track.ServerBlockTracker;
import reghzy.blocklimiter.track.user.PlayerDataLoader;
import reghzy.blocklimiter.utils.debug.Debugger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfigSaveTask implements Runnable {
    private final JavaPlugin plugin;
    private int taskId;
    private ServerBlockTracker serverBlockTracker;
    private int delayTicks = 300;

    public static final String DelayTicksName = "SaveAllConfigsInterval";

    public ConfigSaveTask(JavaPlugin plugin, ServerBlockTracker serverBlockTracker) {
        this.plugin = plugin;
        this.serverBlockTracker = serverBlockTracker;
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
            int saved = serverBlockTracker.savePlayerData(PlayerDataLoader.PlayerDataFolder);
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