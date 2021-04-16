package reghzy.blocklimiter.command.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.utils.SplitString;
import reghzy.blocklimiter.utils.StringHelper;

import java.util.Iterator;

/**
 * Provides functions for easily manipulating arguments
 */
public class CommandArgs implements Iterable<String> {
    private String[] arguments;

    public CommandArgs(String[] arguments) {
        this.arguments = arguments;
        ensureArgsNotNull();
    }

    public CommandArgs(String[] arguments, int startIndex) {
        this(CommandParser.extractArray(arguments, startIndex));
    }

    /**
     * Returns the value at the given index
     * @param index The index to extract the value at
     */
    public String getString(int index) {
        if (withinRange(index)) {
            return arguments[index];
        }

        return null;
    }

    public String getLast() {
        if (getArgsLength() > 0) {
            return arguments[arguments.length - 1];
        }
        return null;
    }

    public String getLast(int backwards) {
        int index = arguments.length - 1 - backwards;
        if (withinRange(index)) {
            return arguments[index];
        }
        return null;
    }

    /**
     * Tries to parse the value at the given index as an integer value (no decimals)
     * @param index The index to extract the value at
     */
    public Integer getInteger(int index) {
        if (withinRange(index)) {
            return StringHelper.parseInteger(arguments[index]);
        }
        return null;
    }

    /**
     * Tries to parse the value at the given index as a double value (including decimals)
     * @param index The index to extract the value at
     */
    public Double getDouble(int index) {
        if (withinRange(index)) {
            return StringHelper.parseDouble(arguments[index]);
        }
        return null;
    }

    /**
     * Tries to parse the value at the given index as a boolean (if its equal to "true" or "t")
     * @param index The index to extract the value at
     */
    public Boolean getBoolean(int index) {
        if (withinRange(index)) {
            String value = arguments[index].toLowerCase();
            return value.equals("true") || value.equalsIgnoreCase("t");
        }
        return null;
    }

    /**
     * Tries to parse the value at the given index as a boolean using the given
     * values as true cases, meaning the value must be equal to any of the given values.
     * if not, it returns false
     * @param index              The index to extract the value at
     * @param lowercaseTrueCases The collection of 'true cases'
     */
    public Boolean getBooleanCases(int index, String... lowercaseTrueCases) {
        if (withinRange(index)) {
            String value = arguments[index].toLowerCase();
            for(String trueCase : lowercaseTrueCases) {
                if (value.equalsIgnoreCase(trueCase)) {
                    return true;
                }
            }
            return false;
        }

        return null;
    }

    /**
     * Tries to parse the value at the given index as a player's name,
     * then gets it using the Bukkit.getPlayer() function
     * @param index The index to extract the player name at
     */
    public Player getPlayer(int index) {
        if (withinRange(index)) {
            return Bukkit.getPlayer(arguments[index]);
        }

        return null;
    }

    /**
     * Tries to parse the value at the given index as an offline player's name,
     * then gets it using the Bukkit.getOfflinePlayer() function
     *
     * @param index The index to extract the offline player name at
     */
    public OfflinePlayer getOfflinePlayer(int index) {
        if (withinRange(index)) {
            return Bukkit.getOfflinePlayer(arguments[index]);
        }

        return null;
    }

    /**
     * Tries to parse the value at the given index as a world,
     * then gets it using the Bukkit.getWorld() function
     * @param index The index to extract the world name at
     */
    public World getWorld(int index) {
        if (withinRange(index)) {
            return Bukkit.getWorld(arguments[index]);
        }

        return null;
    }

    /**
     * Parses the value at the given index and the 2 values after as X, Y and
     * Z values and puts into a location instance (using the given world reference)
     * @param startIndex The starting index to parse the X value and the next 3 values
     */
    public Location getLocation(World world, int startIndex) {
        if (withinRange(startIndex + 2)) {
            Double x = StringHelper.parseDouble(arguments[startIndex + 0]);
            Double y = StringHelper.parseDouble(arguments[startIndex + 1]);
            Double z = StringHelper.parseDouble(arguments[startIndex + 2]);
            if (x == null || y == null || z == null) {
                return null;
            }

            return new Location(world, x, y, z);
        }

        return null;
    }

    /**
     * Parses the value at the given index as a world, and the 3
     * values after as X, Y and Z values and puts into a location instance
     * @param startIndex The starting index to parse the world and the next 3 values
     */
    public Location getLocation(int startIndex) {
        if (withinRange(startIndex + 3)) {
            World world = Bukkit.getWorld(arguments[startIndex]);
            Double x = StringHelper.parseDouble(arguments[startIndex + 1]);
            Double y = StringHelper.parseDouble(arguments[startIndex + 2]);
            Double z = StringHelper.parseDouble(arguments[startIndex + 3]);
            if (world == null || x == null || y == null || z == null) {
                return null;
            }

            return new Location(world, x, y, z);
        }

        return null;
    }

    public BlockDataPair getBlockData(int startIndex, boolean separateArgs) {
        if (separateArgs) {
            return null;
        }

        if (withinRange(startIndex)) {
            SplitString string = SplitString.split(getString(startIndex), ':');
            if (string == null) {
                return null;
            }

            Integer id = StringHelper.parseInteger(string.before);
            Integer data = StringHelper.parseInteger(string.after);
            if (id == null || data == null) {
                return null;
            }

            return new BlockDataPair(id, data);
        }

        return null;
    }

    /**
     * Combines all of the values in the internal array into a
     * single string, joining it with the whitespace character (' ')
     */
    public String concat() {
        return StringHelper.joinArray(this.arguments, 0, ' ');
    }

    /**
     * Combines all of the values in the internal array into a single string
     *
     * @param joinText The character to add in between each element
     */
    public String concat(String joinText) {
        return StringHelper.joinArray(this.arguments, 0, joinText);
    }

    /**
     * Combines all of the values in the internal array into a single string starting at the given start index
     * @param index         The start index. 0 will concat every element, 1 concatenates all but the first
     * @param joinCharacter The character to add in between each element
     */
    public String concat(int index, Character joinCharacter) {
        return StringHelper.joinArray(this.arguments, index, joinCharacter);
    }

    /**
     * Combines the values in the internal array into a single string starting at the given start index
     *
     * @param index    The start index. 0 will concat every element, 1 concatenates all but the first
     * @param joinText The text to add in between each element
     */
    public String concat(int index, String joinText) {
        return StringHelper.joinArray(this.arguments, index, joinText);
    }


    /**
     * Returns the internal array (same reference)
     */
    public String[] getArguments() {
        return this.arguments;
    }

    public int getArgsLength() {
        return this.arguments.length;
    }

    /**
     * Removes the value at the given index (resizing the internal array)
     * @param index The index to remove from the internal array
     */
    public void remove(int index) {
        this.arguments = CommandParser.removeElement(this.arguments, index);
        ensureArgsNotNull();
    }

    /**
     * Removes the first element from the internal array
     */
    public void removeStart() {
        removeStart(1);
    }

    /**
     * Removes the given number of args from the start of the internal array
     * @param count The number of args to remove from the start. 0 removes none, 2 removes the first 2
     */
    public void removeStart(int count) {
        this.arguments = CommandParser.extractArray(this.arguments, count);
        ensureArgsNotNull();
    }

    /**
     * Checks if the given index can index within the internal array without throwing an IndexOutOfBoundsException
     * @param index The index that you need to index to within the internal array
     * @return False if the index is bigger than or equal to the length of the internal array. Otherwise, true
     */
    private boolean withinRange(int index) {
        return index < arguments.length;
    }

    private void ensureArgsNotNull() {
        if (this.arguments == null) {
            this.arguments = new String[0];
        }
    }

    @Override
    public Iterator<String> iterator() {
        return new CommandArgsIterator();
    }

    private class CommandArgsIterator implements Iterator<String> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < arguments.length;
        }

        @Override
        public String next() {
            return arguments[index++];
        }

        @Override
        public void remove() {

        }
    }
}
