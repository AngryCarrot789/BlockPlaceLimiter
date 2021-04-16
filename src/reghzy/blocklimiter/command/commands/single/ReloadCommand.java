package reghzy.blocklimiter.command.commands.single;

import org.bukkit.command.CommandSender;
import reghzy.blocklimiter.BlockPlaceLimiterPlugin;
import reghzy.blocklimiter.command.utils.CommandArgs;
import reghzy.blocklimiter.command.CommandLogger;
import reghzy.blocklimiter.command.ExecutableCommand;
import reghzy.blocklimiter.config.ConfigManager;
import reghzy.blocklimiter.limit.LimitManager;
import reghzy.blocklimiter.utils.permissions.PermissionsHelper;

public class ReloadCommand extends ExecutableCommand {
    public ReloadCommand() {
        super(null, "reload", "<config name>", "Reloads the given config");
    }

    @Override
    public void execute(CommandSender sender, CommandLogger logger, CommandArgs args) {
        if (!PermissionsHelper.isConsoleOrHasPermsOrOp(sender, BlockPlaceLimiterPlugin.ReloadConfigsPermission)) {
            logger.logGold("You dont have permission to reload configs!");
            return;
        }

        if (args.getArgsLength() == 0) {
            logger.logTranslate("&6You havent provided a config name. Theres &3main &6and &3limits");
            return;
        }

        String config = args.getString(0);
        if (config.equalsIgnoreCase("main")) {
            if (ConfigManager.getMainConfig().tryLoadYaml()) {
                BlockPlaceLimiterPlugin.instance.getWorldSyncTask().loadConfig(ConfigManager.getMainConfig());
                BlockPlaceLimiterPlugin.instance.getSaveTask().loadConfig(ConfigManager.getMainConfig());
                logger.logGreen("Reloaded main config!");
            }
        }
        else if (config.equalsIgnoreCase("limits")) {
            if (ConfigManager.getLimitConfig().tryLoadYaml()) {
                LimitManager.getInstance().clearLimits();
                LimitManager.getInstance().loadLimits(ConfigManager.getLimitConfig());
                logger.logGreen("Reloaded limits config!");
            }
        }
        else {
            logger.logGreen("That config doesnt exist");
        }
    }
}
