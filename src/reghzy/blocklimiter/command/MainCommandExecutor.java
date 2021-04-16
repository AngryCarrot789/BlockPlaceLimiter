package reghzy.blocklimiter.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import reghzy.blocklimiter.BlockPlaceLimiterPlugin;
import reghzy.blocklimiter.command.commands.multi.LimitsCommands;
import reghzy.blocklimiter.command.commands.multi.PlayerDataCommands;
import reghzy.blocklimiter.command.commands.single.ClearUnusedDataCommand;
import reghzy.blocklimiter.command.commands.single.HelpCommand;
import reghzy.blocklimiter.command.commands.single.ReloadCommand;
import reghzy.blocklimiter.command.commands.single.SyncWorldsCommand;
import reghzy.blocklimiter.command.utils.CommandArgs;
import reghzy.blocklimiter.utils.permissions.PermissionsHelper;

public final class MainCommandExecutor extends ExecutableSubCommands implements CommandExecutor {
    private final CommandLogger logger;

    public MainCommandExecutor() {
        super("bpl", "The main command for BlockPlaceLimiter");
        this.logger = new CommandLogger(null, ChatColor.GOLD);
    }

    @Override
    public void registerCommands() {
        HelpCommand help = new HelpCommand();
        registerCommand("help", help.registerPassthrough(help));
        registerCommand("limits", help.registerPassthrough(new LimitsCommands()));
        registerCommand("players", help.registerPassthrough(new PlayerDataCommands()));
        registerCommand("clear", help.registerPassthrough(new ClearUnusedDataCommand()));
        registerCommand("sync", help.registerPassthrough(new SyncWorldsCommand()));
        registerCommand("reload", help.registerPassthrough(new ReloadCommand()));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String cmd, String[] args) {
        if (!PermissionsHelper.isConsoleOrHasPermsOrOp(commandSender, BlockPlaceLimiterPlugin.CommandsPermission)) {
            commandSender.sendMessage(ChatColor.GOLD + "You dont have permission for this plugin's commands!");
            return true;
        }

        this.logger.setSender(commandSender);
        if (args == null || args.length == 0) {
            displayHelp(logger);
            return true;
        }

        ExecutableCommand executableCommand = getCommand(args[0]);
        if (executableCommand == null) {
            logger.logPrefix("That command doesn't exist!");
            return true;
        }

        CommandArgs commandArgs = new CommandArgs(args, 1);

        try {
            executableCommand.execute(commandSender, logger, commandArgs);
        }
        catch (Exception e) {
            logger.logPrefix("An internal exception has occurred. read the console for more info");
            logger.logConsole("An exception occurred while executing command: " + args[0]);
            e.printStackTrace();
        }

        this.logger.setSender(null);
        return true;
    }

    @Override
    public void execute(CommandSender sender, CommandLogger logger, CommandArgs args) {

    }

    @Override
    public void displayHelp(CommandLogger logger) {
        logger.logTranslate("&6---------- Block Place Limiter ----------");
        logger.logTranslate("&6 A plugin for limiting the placement of  ");
        logger.logTranslate("&6 specific blocks, and tracking their     ");
        logger.logTranslate("&6 location                                ");
        logger.logTranslate("&6 do &3/bpl help &6for help!              ");
        logger.logTranslate("&6-----------------------------------------");
    }
}