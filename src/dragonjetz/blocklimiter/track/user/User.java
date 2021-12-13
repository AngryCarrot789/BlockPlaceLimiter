package dragonjetz.blocklimiter.track.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import dragonjetz.blocklimiter.track.ServerTracker;

public class User {
    private static final User unknownUser = new User("(Unknown)");

    private final String name;
    private UserBlockData userData;

    protected User(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(getName());
    }

    public UserBlockData getData() {
        if (userData == null) {
            userData = ServerTracker.getInstance().getUserManager().getBlockData(this);
        }

        return userData;
    }

    public static User unknownUser() {
        return unknownUser;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        else if (obj instanceof User) {
            return ((User) obj).name.equals(this.name);
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "User{" + this.name + '}';
    }
}
