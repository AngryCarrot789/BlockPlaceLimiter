package dragonjetz.blocklimiter.track.user.data;

import org.bukkit.plugin.Plugin;
import dragonjetz.api.utils.text.DJFormats;
import dragonjetz.blocklimiter.exceptions.FailedFileCreationException;
import dragonjetz.blocklimiter.exceptions.IncorrectDataFormatException;
import dragonjetz.blocklimiter.track.ServerTracker;
import dragonjetz.blocklimiter.track.user.User;

import java.io.File;
import java.io.IOException;

public abstract class PlayerDataLoader {
    public static File PLAYER_DATA_FOLDER;

    protected final ServerTracker serverTracker;

    /**
     * Initialises the player data folder
     */
    public static void init(Plugin plugin) {
        PLAYER_DATA_FOLDER = new File(plugin.getDataFolder(), "players");
        if (PLAYER_DATA_FOLDER.exists()) {
            return;
        }

        if (!PLAYER_DATA_FOLDER.mkdir())
            throw new RuntimeException("Failed to create player data folder!");
    }

    public PlayerDataLoader(ServerTracker serverTracker) {
        this.serverTracker = serverTracker;
    }

    public File getOrCreateUserFile(String username) throws IOException, FailedFileCreationException {
        File file = new File(PLAYER_DATA_FOLDER, username + ".dat");
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new FailedFileCreationException(DJFormats.format("Failed to create file for user '{0}'", username));
            }
        }

        return file;
    }

    /**
     * loads all of the placed blocks in the file for the given user, and loads it into the world
     * @param file     The location to the player's config file. This file must exist
     * @param username The players username. this is the same as the file's name but without the extension
     * @throws IOException                  If an exception is thrown due to IO errors (reading files)
     * @throws IncorrectDataFormatException If the format of the data is wrong (possibly due to corruption)
     */
    public abstract void loadPlayer(File file, String username) throws IOException, IncorrectDataFormatException;

    /**
     * Saves the given user's data to the given file
     * @param file             The file to save the data to
     * @param user             The user to save
     * @param forceIfUnchanged If the user's data hasn't changed, but this value is true, then save anyway. Otherwise, don't save their data
     * @return Whether their data was saved. This will always be true if the parameter forceIfUnchanged is true
     * @throws IOException If an IO exception occurred while saving the player's data
     * @throws FailedFileCreationException If the file didn't exist, and couldn't be created due to an unknown reason
     */
    public abstract boolean savePlayer(File file, User user, boolean forceIfUnchanged) throws IOException, FailedFileCreationException;
}
