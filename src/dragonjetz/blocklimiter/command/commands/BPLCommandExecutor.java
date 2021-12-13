package dragonjetz.blocklimiter.command.commands;

import dragonjetz.api.commands.MainCommandExecutor;
import dragonjetz.api.commands.predefined.HelpCommand;
import dragonjetz.api.commands.utils.DJLogger;
import dragonjetz.blocklimiter.BlockPlaceLimiterPlugin;
import dragonjetz.blocklimiter.command.BPLPermission;
import dragonjetz.blocklimiter.command.commands.multi.LimitsCommands;
import dragonjetz.blocklimiter.command.commands.multi.PlayerDataCommands;
import dragonjetz.blocklimiter.command.commands.single.ClearUnusedDataCommand;
import dragonjetz.blocklimiter.command.commands.single.ReloadCommand;
import dragonjetz.blocklimiter.command.commands.single.StatsCommand;
import dragonjetz.blocklimiter.command.commands.single.SyncWorldsCommand;
import dragonjetz.api.permission.IPermission;

public class BPLCommandExecutor extends MainCommandExecutor {
    public static final DJLogger BPLLogger = BlockPlaceLimiterPlugin.LOGGER;

    public BPLCommandExecutor() {
        super("bpl", BPLLogger, "Main command for block place limiter!");
    }

    @Override
    public void registerCommands() {
        registerCommand(new HelpCommand("bpl", this.getCommands(), BPLPermission.HELP));
        registerClass(LimitsCommands.class);
        registerClass(PlayerDataCommands.class);
        registerClass(ReloadCommand.class);
        registerClass(ClearUnusedDataCommand.class);
        registerClass(SyncWorldsCommand.class);
        registerClass(StatsCommand.class);
    }

    @Override
    public IPermission getPermission() {
        return BPLPermission.MAIN_CMD;
    }
}
