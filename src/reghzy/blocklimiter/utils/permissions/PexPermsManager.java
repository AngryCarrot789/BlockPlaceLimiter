package reghzy.blocklimiter.utils.permissions;

import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public final class PexPermsManager implements IPermsManager {
    private PermissionManager manager;

    @Override
    public void init() throws NoPermissionManagerClassException {
        try {
            this.manager = PermissionsEx.getPermissionManager();
        }
        catch (NoClassDefFoundError e) {
            throw new NoPermissionManagerClassException();
        }
    }

    @Override
    public boolean has(Player player, String permission) {
        return manager.has(player, permission);
    }
}
