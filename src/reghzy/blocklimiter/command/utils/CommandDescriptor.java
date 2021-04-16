package reghzy.blocklimiter.command.utils;

import org.bukkit.ChatColor;

public class CommandDescriptor {
    private final String pluginPrefix;
    private final String mainCommands;
    private final String commandName;
    private final String args;
    private final String[] descriptionLines;
    private final boolean isMainCommand;

    public CommandDescriptor(String pluginPrefix, String mainCommands, String commandName, String args, String... descriptionLines) {
        this.pluginPrefix = pluginPrefix;
        this.mainCommands = mainCommands;
        this.commandName = commandName;
        this.args = args;
        this.descriptionLines = descriptionLines;
        this.isMainCommand = (commandName == null || commandName.length() < 2);
    }

    public String getFormatCommand() {
        if (isMainCommand) {
            StringBuilder string = new StringBuilder();

            string.append(ChatColor.GREEN).append('/').append(pluginPrefix).append(' ');
            if (mainCommands != null && mainCommands.length() > 0) {
                string.append(ChatColor.AQUA).append(mainCommands).append(' ');
            }
            if (commandName != null && commandName.length() > 0) {
                string.append(ChatColor.DARK_AQUA).append(commandName).append(' ');
            }
            string.append(ChatColor.YELLOW).append("(This command has sub-commands)\n");
            String[] lines = getDescriptionLines();
            for (int i = 0, len = lines.length, lenIndex = len - 1; i < len; i++) {
                string.append(ChatColor.DARK_GREEN).append(lines[i]);
                if (i < lenIndex)
                    string.append('\n');
            }
            return string.toString();
        }
        else {
            StringBuilder string = new StringBuilder();
            string.append(ChatColor.GREEN).append('/').append(pluginPrefix).append(' ');
            if (mainCommands != null && mainCommands.length() > 0) {
                string.append(ChatColor.AQUA).append(mainCommands).append(' ');
            }
            if (commandName != null && commandName.length() > 0) {
                string.append(ChatColor.DARK_AQUA).append(commandName).append(' ');
            }
            if (args != null && args.length() > 0) {
                string.append(ChatColor.LIGHT_PURPLE).append(getArgs());
            }
            string.append('\n');
            String[] lines = getDescriptionLines();
            for (int i = 0, len = lines.length, lenIndex = len - 1; i < len; i++) {
                string.append(ChatColor.DARK_GREEN).append(lines[i]);
                if (i < lenIndex)
                    string.append('\n');
            }
            return string.toString();
        }
    }

    public String getPluginPrefix() {
        return pluginPrefix;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getArgs() {
        return args;
    }

    public String[] getDescriptionLines() {
        return descriptionLines;
    }
}
