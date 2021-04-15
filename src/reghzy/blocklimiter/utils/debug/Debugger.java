package reghzy.blocklimiter.utils.debug;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import reghzy.blocklimiter.track.block.TrackedBlock;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.utils.logs.ChatFormat;
import reghzy.blocklimiter.utils.logs.ChatLogger;

public final class Debugger {
    public static boolean allowDebugging = true;

    public static void logBlockBreak(User breaker, TrackedBlock block) {
        messageOps(ChatFormat.gold(breaker.getName() + " broke " + block.getOwner().getName() + "'s block with ID " + block.getBlockData().toString() + " at " + block.getLocation().toString()));
    }

    public static void logBlockPlace(TrackedBlock block) {
        messageOps(ChatFormat.gold(block.getOwner().getName() + " placed block with ID " + block.getBlockData().toString() + " at " + block.getLocation().toString()));
    }

    public static void log(String message) {
        if (allowDebugging) {
            ChatLogger.logConsole(ChatColor.GOLD + message);
        }
    }

    private static void messageOps(String message) {
        if (allowDebugging) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp()) {
                    player.sendMessage(message);
                }
            }
        }
    }
}
