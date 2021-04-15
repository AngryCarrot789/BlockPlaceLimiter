package reghzy.blocklimiter.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import reghzy.blocklimiter.utils.logs.ChatFormat;
import reghzy.blocklimiter.utils.logs.ChatLogger;

import java.io.File;
import java.io.InputStream;

public class Config extends YamlConfiguration {
    protected final File file;
    protected final JavaPlugin plugin;

    public static Config createInPlugin(JavaPlugin plugin, String name) {
        return new Config(plugin, FileHelper.getFileInDataFolder(plugin, name));
    }

    public Config(JavaPlugin plugin, File configFile) {
        this.plugin = plugin;
        this.file = configFile;
    }

    /**
     * Tries to load YAML data from this config's file
     */
    public void loadConfig() {
        String formatName = ChatFormat.apostrophise(this.file.getName());
        if (file.exists()) {
            try {
                this.map.clear();
                load(this.file);
            }
            catch (Exception e) {
                ChatLogger.logPlugin("Failed to load " + formatName);
                e.printStackTrace();
            }
        }
        else {
            FileHelper.ensurePluginFolderExists(this.plugin);
            ChatLogger.logPlugin("Trying to find a default " + formatName);
            InputStream defaultConfig = FileHelper.getDefaultConfig(this.plugin, this.file.getName());
            if (defaultConfig == null) {
                ChatLogger.logPlugin("Default config file not found, creating an empty config file");
                try {
                    if (!file.createNewFile()) {
                        ChatLogger.logPlugin("Failed to create empty config file");
                    }
                }
                catch (Exception e) {
                    ChatLogger.logPlugin("Failed to create empty config file");
                    e.printStackTrace();
                }
            }
            else {
                ChatLogger.logPlugin("Default " + formatName + " found! Saving to the data folder");
                FileHelper.copyResourceTo(defaultConfig, this.file);
                if (tryLoadYaml()) {
                    ChatLogger.logPlugin("Loaded config!");
                }
                else {
                    ChatLogger.logPlugin("Failed to load config!");
                }
            }
        }
    }

    /**
     * Tries to save the YAML data to the config file
     */
    public void saveConfig() {
        if (this.trySaveYaml()) {
            ChatLogger.logPlugin("Saved " + ChatFormat.apostrophise(file.getName()) + "!");
        }
        else {
            ChatLogger.logPlugin("Failed to save " + ChatFormat.apostrophise(file.getName()) + "!");
        }
    }

    public boolean tryLoadYaml() {
        try {
            this.loadConfig();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean trySaveYaml() {
        try {
            super.save(this.file);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public void clearMap() {
        this.map.clear();
    }
}
