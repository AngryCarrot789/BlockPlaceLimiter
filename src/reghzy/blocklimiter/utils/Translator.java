package reghzy.blocklimiter.utils;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class Translator {
    public static String translateWildcards(String message, Player player) {
        StringBuilder newMessage = new StringBuilder(message.length() * 4);
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c == '%') {
                newMessage.append(getWildcard(message.charAt(++i), player));
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

    public static String getWildcard(char wildcard, Player player) {
        if (wildcard == 'u')
            return player.getName();

        if (wildcard == 'w') {
            World world = player.getWorld();
            if (world != null) {
                String name = world.getName();
                if (name != null)
                    return name;
            }
            return "[Unknown world]";
        }

        return "";
    }

    public static String translateColourCode(char character, String text) {
        final String codes = "0123456789abcdefklmnor";
        char[] chars = text.toCharArray();
        StringBuilder string = new StringBuilder(chars.length);
        for(int i = 0, len = chars.length, lenIndex = len - 1; i < len; i++) {
            char c = chars[i];
            if (c == character) {
                if (i == lenIndex) {
                    string.append(c);
                    return string.toString();
                }
                if (StringHelper.containsChar(codes, chars[++i])) {
                    string.append((char)167).append(chars[i]);
                }
            }
            else {
                string.append(c);
            }
        }
        return string.toString();
    }

    private static String nullCheckPermission(String permission) {
        if (permission == null)
            return "[No permission]";
        return permission;
    }
}
