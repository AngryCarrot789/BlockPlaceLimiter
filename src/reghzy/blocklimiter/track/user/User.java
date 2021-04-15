package reghzy.blocklimiter.track.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import reghzy.blocklimiter.track.ServerBlockTracker;

public class User {
    private final String name;

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
        return ServerBlockTracker.getInstance().getUserManager().getBlockData(this);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return ((User) obj).name.equals(this.name);
        }
        return false;
    }

    @Override
    public String toString() {
        return "User{" + this.name + '}';
    }
}
