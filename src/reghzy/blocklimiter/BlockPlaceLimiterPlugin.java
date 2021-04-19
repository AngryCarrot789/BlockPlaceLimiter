package reghzy.blocklimiter;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import reghzy.blocklimiter.command.MainCommandExecutor;
import reghzy.blocklimiter.config.ConfigManager;
import reghzy.blocklimiter.config.FileHelper;
import reghzy.blocklimiter.limit.LimitManager;
import reghzy.blocklimiter.listeners.BlockListener;
import reghzy.blocklimiter.listeners.PlayerListener;
import reghzy.blocklimiter.listeners.WorldListener;
import reghzy.blocklimiter.tasks.WorldSyncroniseTask;
import reghzy.blocklimiter.track.ServerTracker;
import reghzy.blocklimiter.tasks.ConfigSaveTask;
import reghzy.blocklimiter.track.user.PlayerDataLoader;
import reghzy.blocklimiter.utils.WorldEditHelper;
import reghzy.blocklimiter.utils.logs.ChatLogger;
import reghzy.blocklimiter.utils.permissions.PermissionsHelper;

import javax.naming.OperationNotSupportedException;
import java.util.logging.Level;

public class BlockPlaceLimiterPlugin extends JavaPlugin {
    public static final String ChatPrefix = ChatColor.AQUA + "[BlockLimit]" + ChatColor.RESET;
    public static BlockPlaceLimiterPlugin instance;

    public static final String CommandsPermission = "blocklimit.perms.commands";
    public static final String SyncCommandPermission = "blocklimit.perms.commands.sync";
    public static final String DisplayLimiterPermission = "blocklimit.perms.commands.display";
    public static final String ReloadConfigsPermission = "blocklimit.perms.commands.reload";
    public static final String PlayerEditorPermission = "blocklimit.perms.commands.playeredit";

    private ServerTracker serverTracker;
    private LimitManager limitManager;
    private BlockListener blockListener;
    private PlayerListener playerListener;
    private WorldListener worldListener;
    private ConfigSaveTask saveTask;
    private WorldSyncroniseTask worldSyncTask;

    private static boolean initialised;

    @Override
    public void onEnable() {
        if (instance != null) {
            getLogger().log(Level.SEVERE, "BlockPlaceLimiter was already initialised... oh well");
        }

        instance = this;

        ChatLogger.logPlugin("Initialising permissions...");
        PermissionsHelper.init();
        ChatLogger.logPlugin("Creating data folder...");
        FileHelper.ensurePluginFolderExists(this);
        ConfigManager.initialise();
        PlayerDataLoader.init(this);

        try {
            ChatLogger.logPlugin("Initialising Server Block Tracker...");
            this.serverTracker = new ServerTracker();
        }
        catch (OperationNotSupportedException e) {
            ChatLogger.logPlugin("Singleton Server Block Tracker already initialised! This is a bug, or this plugin was enabled externally");
        }

        try {
            ChatLogger.logPlugin("Initialising Limit Manager...");
            this.limitManager = new LimitManager();
        }
        catch (OperationNotSupportedException e) {
            ChatLogger.logPlugin("Singleton Limit Manager already initialised! This is a bug, or this plugin was enabled externally");
        }

        ChatLogger.logPlugin("Initialising listeners...");
        this.blockListener = new BlockListener(LimitManager.getInstance(), this);
        this.playerListener = new PlayerListener(ServerTracker.getInstance(), this);
        this.worldListener = new WorldListener(ServerTracker.getInstance(), this);
        this.saveTask = new ConfigSaveTask(this, ServerTracker.getInstance());
        this.saveTask.restartTask();
        this.worldSyncTask = new WorldSyncroniseTask(this);
        this.worldSyncTask.restartTask();;

        MainCommandExecutor commandExecutor = new MainCommandExecutor();
        getCommand("blockplacelimiter").setExecutor(commandExecutor);
        //getCommand("blockplacelimiter").setTabCompleter(commandExecutor);

        try {
            WorldEditHelper.init();
        }
        catch (NoClassDefFoundError e) {
            ChatLogger.logConsole("Failed to load worldedit helper");
        }

        ChatLogger.logPlugin("BlockPlaceLimiter v1.0.0 enabled!");
    }

    @Override
    public void onDisable() {
        ChatLogger.logPlugin("BlockPlaceLimiter v1.0.0 disabled :(");
    }

    public ConfigSaveTask getSaveTask() {
        return this.saveTask;
    }

    public WorldSyncroniseTask getWorldSyncTask() {
        return this.worldSyncTask;
    }
}
