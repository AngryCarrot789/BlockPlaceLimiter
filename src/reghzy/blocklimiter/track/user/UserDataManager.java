package reghzy.blocklimiter.track.user;

import org.bukkit.entity.Player;
import reghzy.blocklimiter.track.ServerBlockTracker;
import reghzy.blocklimiter.utils.logs.ChatLogger;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class UserDataManager {
    private final ServerBlockTracker serverBlockTracker;
    private final HashMap<String, User> users;
    private final HashMap<User, UserBlockData> userData;

    public UserDataManager(ServerBlockTracker serverBlockTracker) {
        this.serverBlockTracker = serverBlockTracker;
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
        File file = new File(PlayerDataLoader.PlayerDataFolder, user.getName() + ".dat");
        if (file.exists()) {
            return;
        }

        try {
            file.createNewFile();
        }
        catch (IOException ioException) {
            ChatLogger.logConsole("Failed to create user data file for player: " + user.getName());
            ioException.printStackTrace();
        }
    }
}
