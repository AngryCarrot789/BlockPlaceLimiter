package reghzy.blocklimiter.command.commands.single;

import org.bukkit.command.CommandSender;
import reghzy.api.commands.ExecutableCommand;
import reghzy.api.commands.ITabCompletable;
import reghzy.api.commands.utils.CommandArgs;
import reghzy.api.commands.utils.RZLogger;
import reghzy.api.utils.ExceptionHelper;
import reghzy.api.utils.memory.Lists;
import reghzy.blocklimiter.BlockPlaceLimiterPlugin;
import reghzy.blocklimiter.command.BPLPermission;
import reghzy.api.permission.IPermission;
import reghzy.api.utils.text.RZFormats;
import reghzy.api.config.Config;
import reghzy.api.config.ConfigManager;

import java.util.List;

public class ReloadCommand extends ExecutableCommand implements ITabCompletable {
    public ReloadCommand() {
        super("bpl", null, "reload", "[config name] or [all]", "Reloads a given config (aka, loads it into ram)");
    }

    @Override
    public IPermission getPermission() {
        return BPLPermission.RELOAD;
    }

    @Override
    public void execute(CommandSender sender, RZLogger logger, CommandArgs args) {
        ConfigManager manager = getConfigManager();
        if (!args.hasAnyArgs()) {
            logger.logFormat("You haven't provided a config name!");
            logger.logFormat("Available configs: ");
            for (String name : manager.getConfigNames()) {
                logger.logFormat("  &3{0}", name);
            }

            logger.logFormat("&6(could also do &3/lf reload all&6)");
            return;
        }

        String configName = args.getString(0);
        if (configName.equalsIgnoreCase("all")) {
            for (Config config : manager.getConfigs()) {
                try {
                    manager.loadConfig(config);
                    logger.logFormat(RZFormats.format("&6Loaded '&3{0}&6'", config.getConfigName()));
                }
                catch (RuntimeException e) {
                    logger.logFormat("Failed to reload config {0}. See console...", config.getConfigName());
                    ExceptionHelper.printException(e, logger, true);
                }
            }

            logger.logFormat("Reloaded all configs!");
            return;
        }

        if (!manager.doesConfigExist(configName)) {
            logger.logFormat("That config doesn't exist!");
            return;
        }

        Config config = manager.getConfig(configName);
        try {
            manager.loadConfig(config);
            logger.logFormat(RZFormats.format("&6Loaded '&3{0}&6'", config.getConfigName()));
        }
        catch (RuntimeException e) {
            logger.logFormat("Failed to reload config {0}. See console...", config.getConfigName());
            ExceptionHelper.printException(e, logger, true);
        }
    }

    @Override
    public List<String> doTabComplete(CommandSender sender, RZLogger logger, CommandArgs args, int rawArgsIndex) {
        return Lists.stringStartsWith(getConfigManager().getConfigNames(), args.getString(0, ""));
    }

    public static ConfigManager getConfigManager() {
        return BlockPlaceLimiterPlugin.instance.getConfigManager();
    }
}
