package reghzy.blocklimiter.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import reghzy.blocklimiter.BlockPlaceLimiterPlugin;
import reghzy.blocklimiter.utils.Translator;

public class CommandLogger {
    private CommandSender sender;
    private ChatColor colour;

    public CommandLogger(CommandSender sender, ChatColor defaultColour) {
        this.sender = sender;
        this.colour = defaultColour;
    }

    public void logNoColour(String text) {
        if (sender == null) {
            logConsole(text);
        }
        else {
            sender.sendMessage(text);
        }
    }

    public void log(String text) {
        logNoColour(this.colour + text);
    }

    public void logPrefix(String text) {
        logNoColour(BlockPlaceLimiterPlugin.ChatPrefix + ' ' + this.colour + text);
    }

    public void logRed(String text) {
        logNoColour(ChatColor.RED + text);
    }

    public void logGreen(String text) {
        logNoColour(ChatColor.GREEN + text);
    }

    public void logCyan(String text) {
        logNoColour(ChatColor.DARK_AQUA + text);
    }

    public void logGold(String text) {
        logNoColour(ChatColor.GOLD + text);
    }

    public void logTranslate(String text) {
        logNoColour(Translator.translateColourCode('&', text));
    }

    public void logTranslateArgs(String... args) {
        StringBuilder builder = new StringBuilder(args.length * 8);
        for (String arg : args) {
            builder.append(arg).append(' ');
        }
        logTranslate(builder.toString());
    }

    public void logArgs(String... args) {
        StringBuilder builder = new StringBuilder(args.length * 8);
        for(String arg : args) {
            builder.append(arg).append(' ');
        }
        logNoColour(builder.toString());
    }

    public void logConsole(String text) {
        Bukkit.getConsoleSender().sendMessage(text);
    }

    public void setSender(CommandSender sender) {
        this.sender = sender;
    }

    public CommandSender getSender() {
        return this.sender;
    }
}
