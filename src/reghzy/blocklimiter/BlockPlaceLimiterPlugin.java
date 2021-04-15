package reghzy.blocklimiter;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import reghzy.blocklimiter.config.ConfigManager;
import reghzy.blocklimiter.config.FileHelper;
import reghzy.blocklimiter.limit.LimitManager;
import reghzy.blocklimiter.listeners.BlockListener;
import reghzy.blocklimiter.listeners.PlayerListener;
import reghzy.blocklimiter.listeners.WorldListener;
import reghzy.blocklimiter.track.ServerBlockTracker;
import reghzy.blocklimiter.track.user.ConfigSaveTask;
import reghzy.blocklimiter.track.user.PlayerDataLoader;
import reghzy.blocklimiter.utils.logs.ChatLogger;
import reghzy.blocklimiter.utils.permissions.PermissionsHelper;

import javax.naming.OperationNotSupportedException;

public class BlockPlaceLimiterPlugin extends JavaPlugin {
    public static final String ChatPrefix = ChatColor.AQUA + "[BlockLimit]" + ChatColor.RESET;
    public static BlockPlaceLimiterPlugin instance;

    private ServerBlockTracker serverBlockTracker;
    private LimitManager limitManager;
    private BlockListener blockListener;
    private PlayerListener playerListener;
    private WorldListener worldListener;
    private ConfigSaveTask saveTask;

    @Override
    public void onEnable() {
        instance = this;

        ChatLogger.logPlugin("Initialising permissions...");
        PermissionsHelper.init();
        ChatLogger.logPlugin("Creating data folder...");
        FileHelper.ensurePluginFolderExists(this);
        ConfigManager.initialise();
        PlayerDataLoader.init(this);

        try {
            ChatLogger.logPlugin("Initialising Server Block Tracker...");
            this.serverBlockTracker = new ServerBlockTracker();
        }
        catch (OperationNotSupportedException e) {
            ChatLogger.logPlugin("Server Block Tracker already initialised! This is a bug, or this plugin was enabled externally");
        }

        try {
            ChatLogger.logPlugin("Initialising Limit Manager...");
            this.limitManager = new LimitManager();
        }
        catch (OperationNotSupportedException e) {
            ChatLogger.logPlugin("Limit Manager already initialised! This is a bug, or this plugin was enabled externally");
        }

        ChatLogger.logPlugin("Initialising listeners...");
        this.blockListener = new BlockListener(LimitManager.getInstance(), this);
        this.playerListener = new PlayerListener(ServerBlockTracker.getInstance(), this);
        this.worldListener = new WorldListener(ServerBlockTracker.getInstance(), this);
        this.saveTask = new ConfigSaveTask(this, ServerBlockTracker.getInstance());
        this.saveTask.restartTask();

        ChatLogger.logPlugin("BlockPlaceLimiter v1.0.0 enabled!");
    }

    @Override
    public void onDisable() {
        ChatLogger.logPlugin("BlockPlaceLimiter v1.0.0 disabled :(");
    }
}
