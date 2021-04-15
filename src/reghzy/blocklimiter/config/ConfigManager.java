package reghzy.blocklimiter.config;

import reghzy.blocklimiter.BlockPlaceLimiterPlugin;

public class ConfigManager {
    private static Config mainConfig;
    private static Config limitConfig;

    public static void initialise() {
        try {
            mainConfig = Config.createInPlugin(BlockPlaceLimiterPlugin.instance, "config.yml");
            mainConfig.tryLoadYaml();
            limitConfig = Config.createInPlugin(BlockPlaceLimiterPlugin.instance, "limits.yml");
            limitConfig.loadConfig();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Config getMainConfig() {
        return mainConfig;
    }

    public static Config getLimitConfig() {
        return limitConfig;
    }
}
