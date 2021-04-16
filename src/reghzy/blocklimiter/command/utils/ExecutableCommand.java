package reghzy.blocklimiter.command.utils;

import org.bukkit.command.CommandSender;
import reghzy.blocklimiter.command.helpers.CommandArgs;

/**
 * An interface in which contains an executable command, requiring a sender, logger and arguments
 */
public abstract class ExecutableCommand {
    private final CommandDescriptor description;

    public ExecutableCommand(String mainCommand, String commandName, String args, String... descriptionLines) {
        this.description = new CommandDescriptor("bpl", mainCommand, commandName, args, descriptionLines);
    }

    /**
     * Executes the command, using the given sender, chat logger, and the command arguments
     *
     * @param sender The command sender (could be a player or console... i hope)
     * @param logger The chat logger
     * @param args   The arguments to the specific command. E.g, /maincommand command arg1 arg2, args contains arg1 and arg2
     */
    public abstract void execute(CommandSender sender, CommandLogger logger, CommandArgs args);

    public CommandDescriptor getDescription() {
        return description;
    }
}
