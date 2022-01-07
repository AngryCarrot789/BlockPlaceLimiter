package reghzy.blocklimiter.utils;

import org.bukkit.entity.Player;
import reghzy.api.utils.text.RZFormats;
import reghzy.blocklimiter.track.world.BPLVec3i;

public class Translator {
    public static String translateWildcards(String message, Player player, BPLVec3i location) {
        return translateWildcards(message, player.getName(), player.getWorld().getName(), location);
    }

    public static String translateWildcards(String message, String username, String worldName, BPLVec3i location) {
        StringBuilder newMessage = new StringBuilder(message.length() * 4);
        for (int i = 0, len = message.length(), lastIndex = len - 1; i < len; i++) {
            char c = message.charAt(i);
            if (c == '%') {
                newMessage.append(getWildcard(message.charAt(++i), username, worldName, location));
            }
            else if (c == '&' && i < lastIndex && RZFormats.ACCEPTABLE_COLOURS.indexOf(message.charAt(i + 1)) != -1) {
                newMessage.append((char) 167).append(message.charAt(++i));
            }
            else {
                newMessage.append(c);
            }
        }
        return newMessage.toString();
    }

    public static String getWildcard(char wildcard, String username, String world, BPLVec3i location) {
        if (wildcard == 'u') {
            return username;
        }
        else if (wildcard == 'w') {
            return world;
        }
        else if (wildcard == 'l') {
            return location == null ? "[Location Unavailable]" : location.toString();
        }
        else {
            return "%" + wildcard;
        }
    }

    public static String nullPermsCheck(String permission) {
        return permission == null ? "[No permission]" : permission;
    }

    public static String nullMessageCheck(String permission) {
        return permission == null ? "[No message]" : permission;
    }
}
