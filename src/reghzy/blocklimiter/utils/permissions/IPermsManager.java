package reghzy.blocklimiter.utils.permissions;

import org.bukkit.entity.Player;

public interface IPermsManager {
    void init() throws NoPermissionManagerClassException;

    boolean has(Player player, String permission);
}
