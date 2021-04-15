package reghzy.blocklimiter.utils.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import reghzy.blocklimiter.utils.logs.ChatLogger;

public class PermissionsHelper {
    private static IPermsManager perms;

    public static void init() {
        try {
            perms = new PexPermsManager();
            perms.init();
            ChatLogger.logPlugin("Using PermissionsEx :)");
        }
        catch (NoPermissionManagerClassException e) {
            ChatLogger.logPlugin("Failed to find PermissionEx. Trying LuckPerms...");
            try {
                perms = new LuckPermsManager();
                perms.init();
                ChatLogger.logPlugin("Using LuckPerms :)");
            }
            catch (NoPermissionManagerClassException a) {
                ChatLogger.logPlugin("Failed to find LuckPerms. Using default bukkit permissions");
                try {
                    perms = new BukkitPermsManager();
                    perms.init();
                    ChatLogger.logPlugin("Using Bukkit Permissions :)) :(");
                }
                catch (NoPermissionManagerClassException aa) {
                    ChatLogger.logPlugin("What");
                }
            }
        }
    }

    public static boolean hasPermission(Player player, String permission) {
        return perms.has(player, permission);
    }

    public static boolean hasPermissionOrOp(Player player, String permission) {
        return hasPermission(player, permission) || player.isOp();
    }

    public static boolean isConsoleOrHasPermsOrOp(CommandSender sender, String permission) {
        if (sender instanceof ConsoleCommandSender)
            return true;
        return hasPermissionOrOp((Player) sender, permission);
    }
}
