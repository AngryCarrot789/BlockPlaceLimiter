package reghzy.blocklimiter.command;

import reghzy.api.permission.IPermission;
import reghzy.api.permission.PermissionManager;

public class BPLPermission {
    public static final IPermission MY_BLOCKS = PermissionManager.registerPermission("BPL_MY_BLOCKS", "blocklimit.commands.myblocks");
    public static final IPermission MAIN_CMD = PermissionManager.registerPermission("BPL_MAIN_CMD", "blocklimit.maincommand");
    public static final IPermission SYNC = PermissionManager.registerPermission("BPL_SYNC", "blocklimit.commands.sync");
    public static final IPermission HELP = PermissionManager.registerPermission("BPL_HELP", "blocklimit.commands.help");
    public static final IPermission CLEAR_UNUSED = PermissionManager.registerPermission("BPL_CLEAR_UNUSED", "blocklimit.commands.clearunused");
    public static final IPermission RELOAD = PermissionManager.registerPermission("BPL_RELOAD", "blocklimit.perms.commands.reload");
    public static final IPermission PLAYER_STATS = PermissionManager.registerPermission("BPL_PLAYER_STATS", "blocklimit.perms.commands.stats");
    public static final IPermission PLAYERDATA_CMD = PermissionManager.registerPermission("BPL_PLAYERDATA_CMD", "blocklimit.playerdata");
    public static final IPermission LC_display = PermissionManager.registerPermission("BPL_LC_display", "blocklimit.playerdata.display");
    public static final IPermission playerDataCommands = PermissionManager.registerPermission("BPL_playerDataCommands", "blocklimit.playerdata");
    public static final IPermission PDC_advanced = PermissionManager.registerPermission("BPL_PDC_advanced", "blocklimit.playerdata.advanced");
    public static final IPermission PDC_basic = PermissionManager.registerPermission("BPL_PDC_basic", "blocklimit.playerdata.basic");
    public static final IPermission PDC_removeBlock = PermissionManager.registerPermission("BPL_PDC_removeBlock", "blocklimit.playerdata.removeblock");
    public static final IPermission PDC_setOwner = PermissionManager.registerPermission("BPL_PDC_setOwner", "blocklimit.playerdata.setowner");
}