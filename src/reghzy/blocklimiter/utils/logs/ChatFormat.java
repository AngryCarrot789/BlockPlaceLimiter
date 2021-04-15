package reghzy.blocklimiter.utils.logs;

import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * A class for formatting text/messages to be send to players/console
 */
public class ChatFormat {
    public static final String CONTAINS_SUBCOMMANDS_MSG = ChatColor.DARK_AQUA + "(This command holds sub commands)";

    /**
     * Puts apostrophies (') around the content, like so: 'content'
     */
    public static String apostrophise(String content) {
        return surround("'", content, "'");
    }

    /**
     * Puts brackets around the content, like so: [content]
     */
    public static String bracketise(String content) {
        return surround("[", content, "]");
    }

    public static String parenthesise(String content) {
        return surround("(", content, ")");
    }

    /**
     * Surrounds the content in on the left and right with the given values WITH NO SPACES, like so: [left][content][right]
     */
    public static String surround(String left, String content, String right) {
        return left + content + right;
    }

    /**
     * Uses the repeat() function twice with the '-' character with a max length of 'maxWidth / 2',
     * and surrounds the content in them and adds a <> between, like so: ------< content >------
     */
    public static String titliseContent(String content, int maxWidth) {
        int actualWidth = maxWidth - 2;
        int totalTitleChars = actualWidth - content.length() - 2;
        String repeatedChars = repeat('-', totalTitleChars / 2);
        return repeatedChars + "<" + content + ">" + repeatedChars;
    }

    /**
     * repeats the given character the given number of times
     */
    public static String repeat(char character, int times) {
        StringBuilder stringBuilder = new StringBuilder(times);
        for (int i = 0; i < times; i++) {
            stringBuilder.append(character);
        }
        return stringBuilder.toString();
    }

    /**
     * surrounds the value with whitespaces, like so: ( content )
     */
    public static String spacify(String content) {
        return surround(" ", content, " ");
    }

    /**
     * Formats the location with colours, RED/GREEN/BLUE for the XYZ respectively
     */
    public static String colouredXYZ(Location location) {
        return colouredXYZ(location.getX(), location.getY(), location.getZ());
    }

    /**
     * Formats the location with colours, RED/GREEN/BLUE for the XYZ respectively
     */
    public static String colouredXYZ(double x, double y, double z) {
        return "" + ChatColor.RED + x + ", " + ChatColor.GREEN + y + ", " + ChatColor.BLUE + z;
    }

    /**
     * Formats the location with colours, RED/GREEN for the XY respectively
     */
    public static String colouredXZ(double x, double z) {
        return "" + ChatColor.RED + x + ", " + ChatColor.BLUE + z;
    }

    /**
     * Makes the content green
     */
    public static String green(String content) {
        return ChatColor.GREEN + content;
    }

    /**
     * Makes the content gold
     */
    public static String gold(String content) {
        return ChatColor.GOLD + content;
    }

    /**
     * Makes the content red
     */
    public static String red(String content) {
        return ChatColor.RED + content;
    }
}
