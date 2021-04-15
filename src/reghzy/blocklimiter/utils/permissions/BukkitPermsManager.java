package reghzy.blocklimiter.utils.permissions;

import org.bukkit.entity.Player;

public class BukkitPermsManager implements IPermsManager {
    @Override
    public void init() {

    }

    @Override
    public boolean has(Player player, String permission) {
        return player.hasPermission(permission);
    }
}
