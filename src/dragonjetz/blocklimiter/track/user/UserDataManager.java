package dragonjetz.blocklimiter.track.user;

import org.bukkit.entity.Player;
import dragonjetz.api.utils.ExceptionHelper;
import dragonjetz.blocklimiter.BlockPlaceLimiterPlugin;
import dragonjetz.blocklimiter.track.ServerTracker;
import dragonjetz.blocklimiter.track.user.data.PlayerDataLoader;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

public class UserDataManager {
    private final ServerTracker serverTracker;
    private final HashMap<String, User> users;
    private final HashMap<User, UserBlockData> userData;

    public UserDataManager(ServerTracker serverTracker) {
        this.serverTracker = serverTracker;
        this.users = new HashMap<String, User>(40);
        this.userData = new HashMap<User, UserBlockData>(40);
    }

    public User getUser(Player player) {
        return getUser(player.getName());
    }

    public User getUser(String name) {
        User user = getOrCreateUser(name);
        getOrCreateBlockData(user);
        return user;
    }

    public UserBlockData getBlockData(User user) {
        return getOrCreateBlockData(user);
    }

    public UserBlockData getBlockData(String username) {
        return getBlockData(getUser(username));
    }

    public Collection<User> getUsers() {
        return this.users.values();
    }

    public Collection<UserBlockData> getUsersData() {
        return this.userData.values();
    }

    public void unloadUser(User user) {
        userData.remove(user);
        users.remove(user.getName());
    }

    public void loadPlayer(String name) {
        ensureUserFileExists(getUser(name));
    }

    // if this is called after initialisation, it means the player had nothing placed
    private UserBlockData getOrCreateBlockData(User user) {
        UserBlockData data = userData.get(user);
        if (data == null) {
            data = new UserBlockData(user);
            userData.put(user, data);
        }
        return data;
    }

    // if this is called after initialisation, it means the player had nothing placed and was unloaded
    private User getOrCreateUser(String name) {
        User user = users.get(name);
        if (user == null) {
            user = new User(name);
            users.put(name, user);
        }
        return user;
    }

    private void ensureUserFileExists(User user) {
        File file = new File(PlayerDataLoader.PLAYER_DATA_FOLDER, user.getName() + ".dat");
        if (file.exists()) {
            return;
        }

        try {
            file.createNewFile();
        }
        catch (IOException ioException) {
            BlockPlaceLimiterPlugin.LOGGER.logFormatConsole("Failed to create user data file for player '{0}'", user.getName());
            ExceptionHelper.printException(ioException);
        }
    }
}
