package dragonjetz.blocklimiter;

import org.bukkit.plugin.java.JavaPlugin;
import dragonjetz.api.commands.CommandManager;
import dragonjetz.api.commands.utils.DJLogger;
import dragonjetz.api.config.Config;
import dragonjetz.api.config.ConfigLoadHandler;
import dragonjetz.api.config.ConfigManager;
import dragonjetz.api.config.ConfigPreSaveHandler;
import dragonjetz.api.playerdata.serialise.serialisers.BoolRefSerialiser;
import dragonjetz.api.utils.ExceptionHelper;
import dragonjetz.blocklimiter.command.commands.BPLCommandExecutor;
import dragonjetz.blocklimiter.command.commands.MyPlacedBlocksCommand;
import dragonjetz.blocklimiter.limit.LimitManager;
import dragonjetz.blocklimiter.listeners.BlockListener;
import dragonjetz.blocklimiter.listeners.PlayerListener;
import dragonjetz.blocklimiter.listeners.WorldListener;
import dragonjetz.blocklimiter.tasks.ConfigSaveTask;
import dragonjetz.blocklimiter.tasks.WorldSyncroniseTask;
import dragonjetz.blocklimiter.track.ServerTracker;
import dragonjetz.blocklimiter.track.user.data.PlayerDataLoader;
import dragonjetz.carrottools.playerdata.SerialisationDataArea;

import javax.naming.OperationNotSupportedException;

public class BlockPlaceLimiterPlugin extends JavaPlugin {
    public static BlockPlaceLimiterPlugin instance;

    public static final DJLogger LOGGER = new DJLogger("§7[§bBlock§3Limit§7]§r");

    private ServerTracker serverTracker;
    private LimitManager limitManager;
    private BlockListener blockListener;
    private PlayerListener playerListener;
    private WorldListener worldListener;
    private ConfigSaveTask saveTask;
    private WorldSyncroniseTask worldSyncTask;

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        if (instance != null) {
            throw new UnsupportedOperationException("Cannot re-enable BPL!");
        }

        instance = this;

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        LOGGER.logTranslateConsole("&6Registering serialiser '&ebpl-admin&6' with CTools...");
        try {
            SerialisationDataArea area = SerialisationDataArea.register("bpl-admin");
            area.registerSerialiser("msg-limitedbroken-nonexistant", new BoolRefSerialiser());
            LOGGER.logTranslateConsole("Registered!");
        }
        catch (Throwable e) {
            LOGGER.logTranslateConsole("Failed to register serialiser with CTools :(");
        }

        LOGGER.logTranslateConsole("Initialising config manager...");
        this.configManager = new ConfigManager(this);

        this.configManager.registerResourceConfig("config", new ConfigLoadHandler() {
            @Override
            public void onLoaded(Config config) {
                ConfigSaveTask.loadConfig(config);
                WorldSyncroniseTask.loadConfig(config);
            }
        }, (ConfigPreSaveHandler) null);

        this.configManager.registerResourceConfig("limits", new ConfigLoadHandler() {
            @Override
            public void onLoaded(Config config) {
                if (limitManager != null) {
                    limitManager.loadLimits(config);
                }
            }
        }, (ConfigPreSaveHandler) null);

        for (Config config : this.configManager.getConfigs()) {
            try {
                this.configManager.loadConfig(config);
            }
            catch (RuntimeException e) {
                LOGGER.logFormat("Failed to load config '{0}'", config.getConfigName());
                ExceptionHelper.printException(e, LOGGER, true);
            }
        }

        PlayerDataLoader.init(this);

        try {
            LOGGER.logTranslateConsole("Initialising Server Block Tracker...");
            this.serverTracker = new ServerTracker();
        }
        catch (OperationNotSupportedException e) {
            LOGGER.logTranslateConsole("Singleton Server Block Tracker already initialised! This is a bug, or this plugin was enabled externally");
        }

        try {
            LOGGER.logTranslateConsole("Initialising Limit Manager...");
            this.limitManager = new LimitManager();
        }
        catch (OperationNotSupportedException e) {
            LOGGER.logTranslateConsole("Singleton Limit Manager already initialised! This is a bug, or this plugin was enabled externally");
        }

        LOGGER.logTranslateConsole("Initialising listeners...");
        this.blockListener = new BlockListener(LimitManager.getInstance(), this);
        this.playerListener = new PlayerListener(ServerTracker.getInstance(), this);
        this.worldListener = new WorldListener(ServerTracker.getInstance(), this);
        this.saveTask = new ConfigSaveTask(this, ServerTracker.getInstance());
        this.saveTask.startTask();
        this.worldSyncTask = new WorldSyncroniseTask(this);
        this.worldSyncTask.startTask();

        CommandManager.registerMainClass(this, "blockplacelimiter", BPLCommandExecutor.class);
        CommandManager.registerSingleClass(this, "myblocks", MyPlacedBlocksCommand.class);

        for (Config config : this.configManager.getConfigs()) {
            try {
                this.configManager.loadConfig(config);
            }
            catch (RuntimeException e) {
                LOGGER.logFormat("Failed to load config '{0}'", config.getConfigName());
                ExceptionHelper.printException(e, LOGGER, true);
            }
        }

        LOGGER.logTranslateConsole("BlockPlaceLimiter v1.0.0 enabled!");
    }

    @Override
    public void onDisable() {
        LOGGER.logTranslateConsole("BlockPlaceLimiter v1.0.0 disabled :(");
    }

    public ConfigSaveTask getSaveTask() {
        return this.saveTask;
    }

    public WorldSyncroniseTask getWorldSyncTask() {
        return this.worldSyncTask;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
