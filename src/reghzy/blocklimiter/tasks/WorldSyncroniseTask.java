package reghzy.blocklimiter.tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import reghzy.blocklimiter.config.Config;
import reghzy.blocklimiter.config.ConfigManager;
import reghzy.blocklimiter.track.Synchroniser;
import reghzy.blocklimiter.track.block.TrackedBlock;
import reghzy.blocklimiter.utils.logs.ChatLogger;

import java.util.ArrayList;

public class WorldSyncroniseTask implements Runnable {
    private final JavaPlugin plugin;
    private int taskId;
    private int delayTicks = 300;

    public static final String DelayTicksName = "SyncWorldsDelay";

    public WorldSyncroniseTask(JavaPlugin plugin) {
        this.plugin = plugin;
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
            ArrayList<TrackedBlock> unsynced = Synchroniser.scanForUnsynedBlocks();
            if (unsynced.size() > 0) {
                ChatLogger.logConsole("Found " + unsynced.size() + " Unsynced blocks! They have been removed from the player's counter");
            }
        }
        catch (Exception e) {

        }
    }
}