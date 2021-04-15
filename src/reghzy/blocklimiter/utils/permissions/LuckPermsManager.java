package reghzy.blocklimiter.utils.permissions;

import org.bukkit.entity.Player;

public class LuckPermsManager implements IPermsManager {
    //private LuckPerms perms;
    //private UserManager userManager;

    public void init() throws NoPermissionManagerClassException {
        throw new NoPermissionManagerClassException();
        //try {
        //    //perms = LuckPermsProvider.get();
        //    //userManager = perms.getUserManager();
        //}
        //catch (NoClassDefFoundError e) {
        //    throw new NoPermissionManagerClassException();
        //}
    }

    @Override
    public boolean has(Player player, String permission) {
        //User user = userManager.getUser(player.getUniqueId());
        //if (user == null)
        //    return false;
        //return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
        return false;
    }
}
