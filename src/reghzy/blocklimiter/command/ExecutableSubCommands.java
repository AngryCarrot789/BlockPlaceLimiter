package reghzy.blocklimiter.command;

import org.bukkit.command.CommandSender;
import reghzy.blocklimiter.command.utils.CommandArgs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class ExecutableSubCommands extends ExecutableCommand { // implements TabCompleter {
    private final HashMap<String, ExecutableCommand> commands;

    public ExecutableSubCommands(String mainCommands, String... descriptionLines) {
        super(mainCommands, null, null, descriptionLines);
        this.commands = new HashMap<String, ExecutableCommand>(8);
        registerCommands();
    }

    @Override
    public void execute(CommandSender sender, CommandLogger logger, CommandArgs args) {
        if (args.getArgsLength() == 0) {
            displayHelp(logger);
            return;
        }

        String commandName = args.getString(0);
        if (commandName == null || commandName.equalsIgnoreCase("help")) {
            displayHelp(logger);
            return;
        }

        ExecutableCommand executableCommand = getCommand(commandName);
        if (executableCommand == null) {
            logger.logPrefix("That command doesn't exist!");
            return;
        }

        args.removeStart();

        try {
            executableCommand.execute(sender, logger, args);
        }
        catch (Exception e) {
            logger.logPrefix("An internal exception has occurred. read the console for more info");
            logger.logConsole("An exception occurred while executing command: " + commandName);
            e.printStackTrace();
        }
    }

    public void registerCommand(String name, ExecutableCommand command) {
        this.commands.put(name, command);
    }

    public ExecutableCommand getCommand(String name) {
        return this.commands.get(name);
    }

    public List<String> getTabCompleter(CommandSender sender, CommandArgs args) {
        ArrayList<String> tabs = new ArrayList<String>();
        for(ExecutableCommand command : getCommands()) {
            tabs.add(command.getDescription().getCommandName());
        }
        return tabs;
    }

    public Collection<ExecutableCommand> getCommands() {
        return this.commands.values();
    }

    public void displayHelp(CommandLogger logger) {
        for (ExecutableCommand command : getCommands()) {
            logger.log(command.getDescription().getFormatCommand());
        }
    }

    public abstract void registerCommands();

    //@Override
    //public List<String> onTabComplete(CommandSender commandSender, Command command, String cmd, String[] args) {
    //    return getTabCompleter(commandSender, new CommandArgs(args));
    //}
}
