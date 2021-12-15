package reghzy.blocklimiter.utils;

import org.bukkit.entity.Player;
import reghzy.blocklimiter.track.world.Vector3;

public class Translator {
    public static String translateWildcards(String message, Player player, Vector3 location) {
        return translateWildcards(message, player.getName(), player.getWorld().getName(), location);
    }

    public static String translateWildcards(String message, String username, String worldName, Vector3 location) {
        StringBuilder newMessage = new StringBuilder(message.length() * 4);
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c == '%') {
                newMessage.append(getWildcard(message.charAt(++i), username, worldName, location));
            }
            else if (c == '&') {
                newMessage.append((char) 167).append(message.charAt(++i));
            }
            else {
                newMessage.append(c);
            }
        }
        return newMessage.toString();
    }

    public static String getWildcard(char wildcard, String username, String world, Vector3 location) {
        if (wildcard == 'u')
            return username;

        if (wildcard == 'w') {
            return world;
        }

        if (wildcard == 'l') {
            return location == null ? "[Location Unavailable]" : location.toString();
        }

        return "%" + wildcard;
    }

    public static String nullPermsCheck(String permission) {
        if (permission == null)
            return "[No permission]";
        return permission;
    }

    public static String nullMessageCheck(String permission) {
        if (permission == null)
            return "[No message]";
        return permission;
    }
}
