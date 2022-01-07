package reghzy.blocklimiter.tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import reghzy.api.commands.utils.RZLogger;
import reghzy.api.config.Config;
import reghzy.blocklimiter.BlockPlaceLimiterPlugin;
import reghzy.blocklimiter.track.Synchroniser;
import reghzy.blocklimiter.track.world.TrackedBlock;

import java.util.List;

public class WorldSyncroniseTask implements Runnable {
    private static final String DelayTicksName = "SyncWorldsDelay";
    private static WorldSyncroniseTask instance;
    private static int delayTicks = 300;
    private final Plugin plugin;
    private int taskId;


    public WorldSyncroniseTask(Plugin plugin) {
        instance = this;
        this.plugin = plugin;
    }

    public static void loadConfig(Config mainConfig) {
        delayTicks = mainConfig.getInt(DelayTicksName);
        if (instance != null) {
            instance.restartTask();
        }
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
            List<TrackedBlock> blocks = Synchroniser.scanForUnsynedBlocks(false);
            if (blocks.size() > 0) {
                RZLogger logger = BlockPlaceLimiterPlugin.LOGGER;
                logger.logFormatConsole("&6Found &3{0} &6Un-synced blocks! They have been removed from the player's counter. The blocks: ", blocks.size());
                for (TrackedBlock block : blocks) {
                    logger.logFormatConsole(
                            "&6Owner: &3{0}&6, ID/Meta: &3{1}&6, World: &3{2}&6, Location: {3}",
                            block.getOwner().getName(),
                            block.getBlockData().toString(),
                            block.getWorldName(),
                            block.getLocation().formatColour());
                }
            }
        }
        catch (Exception ignored) {

        }
    }
}