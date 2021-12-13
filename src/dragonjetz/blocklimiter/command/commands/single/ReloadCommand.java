package dragonjetz.blocklimiter.command.commands.single;

import org.bukkit.command.CommandSender;
import dragonjetz.api.commands.ExecutableCommand;
import dragonjetz.api.commands.ITabCompletable;
import dragonjetz.api.commands.utils.CommandArgs;
import dragonjetz.api.commands.utils.DJLogger;
import dragonjetz.api.utils.ExceptionHelper;
import dragonjetz.api.utils.memory.Lists;
import dragonjetz.blocklimiter.BlockPlaceLimiterPlugin;
import dragonjetz.blocklimiter.command.BPLPermission;
import dragonjetz.api.permission.IPermission;
import dragonjetz.api.utils.text.DJFormats;
import dragonjetz.api.config.Config;
import dragonjetz.api.config.ConfigManager;

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
    public void execute(CommandSender sender, DJLogger logger, CommandArgs args) {
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
                    logger.logFormat(DJFormats.format("&6Loaded '&3{0}&6'", config.getConfigName()));
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
            logger.logFormat(DJFormats.format("&6Loaded '&3{0}&6'", config.getConfigName()));
        }
        catch (RuntimeException e) {
            logger.logFormat("Failed to reload config {0}. See console...", config.getConfigName());
            ExceptionHelper.printException(e, logger, true);
        }
    }

    @Override
    public List<String> doTabComplete(CommandSender sender, DJLogger logger, CommandArgs args, int rawArgsIndex) {
        return Lists.stringStartsWith(getConfigManager().getConfigNames(), args.getString(0, ""));
    }

    public static ConfigManager getConfigManager() {
        return BlockPlaceLimiterPlugin.instance.getConfigManager();
    }
}
