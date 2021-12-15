package reghzy.blocklimiter.command.commands;

import reghzy.api.commands.MainCommandExecutor;
import reghzy.api.commands.predefined.HelpCommand;
import reghzy.api.commands.utils.RZLogger;
import reghzy.blocklimiter.BlockPlaceLimiterPlugin;
import reghzy.blocklimiter.command.BPLPermission;
import reghzy.blocklimiter.command.commands.multi.LimitsCommands;
import reghzy.blocklimiter.command.commands.multi.PlayerDataCommands;
import reghzy.blocklimiter.command.commands.single.ClearUnusedDataCommand;
import reghzy.blocklimiter.command.commands.single.ReloadCommand;
import reghzy.blocklimiter.command.commands.single.StatsCommand;
import reghzy.blocklimiter.command.commands.single.SyncWorldsCommand;
import reghzy.api.permission.IPermission;

public class BPLCommandExecutor extends MainCommandExecutor {
    public static final RZLogger BPLLogger = BlockPlaceLimiterPlugin.LOGGER;

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
