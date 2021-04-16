package reghzy.blocklimiter.command.commands.single;

import org.bukkit.command.CommandSender;
import reghzy.blocklimiter.command.helpers.CommandArgs;
import reghzy.blocklimiter.command.utils.CommandDescriptor;
import reghzy.blocklimiter.command.utils.CommandLogger;
import reghzy.blocklimiter.command.utils.ExecutableCommand;

import java.util.ArrayList;

public class HelpCommand extends ExecutableCommand {
    private static final int DisplayAmount = 5;

    private final ArrayList<CommandDescriptor> commandDescriptors;

    public HelpCommand() {
        super(null,
              "help",
              "<page>",
              "Displays help on a specific page");

        this.commandDescriptors = new ArrayList<CommandDescriptor>(16);
    }

    @Override
    public void execute(CommandSender sender, CommandLogger logger, CommandArgs args) {
        Integer page = args.getInteger(0);
        if (page == null || page < 1) {
            page = 1;
        }

        if (!displayPage(page - 1, logger)) {
            logger.logPrefix("That help page doesnt exist");
        }
    }

    public <T extends ExecutableCommand> T addPassThrough(T command) {
        this.commandDescriptors.add(command.getDescription());
        return command;
    }

    private boolean displayPage(int pageIndex, CommandLogger logger) {
        if ((DisplayAmount * pageIndex) >= this.commandDescriptors.size()) {
            return false;
        }

        printDescriptors(DisplayAmount * pageIndex, logger);
        return true;
    }

    private void printDescriptors(int start, CommandLogger logger) {
        int count = HelpCommand.DisplayAmount * (start + 1);
        for(int i = start, length = this.commandDescriptors.size(); i < count; i++) {
            if (i >= length) {
                return;
            }

            logger.logNoColour(this.commandDescriptors.get(i).getFormatCommand());
        }
    }
}
