package reghzy.blocklimiter.command.commands.multi;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import reghzy.api.commands.ExecutableCommand;
import reghzy.api.commands.ExecutableSubCommands;
import reghzy.api.commands.utils.CommandArgs;
import reghzy.api.commands.utils.RZLogger;
import reghzy.api.permission.IPermission;
import reghzy.blocklimiter.command.BPLPermission;
import reghzy.blocklimiter.limit.BlockLimiter;
import reghzy.blocklimiter.limit.LimitManager;
import reghzy.blocklimiter.limit.MetaLimiter;
import reghzy.blocklimiter.track.utils.IntegerRange;
import reghzy.blocklimiter.track.utils.RangeLimit;
import reghzy.blocklimiter.utils.Translator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class LimitsCommands extends ExecutableSubCommands {
    public LimitsCommands() {
        super("bpl", "limits", "Contains commands for listing limited blocks");
    }

    @Override
    public void registerCommands() {
        registerClass(DisplayLimitSubCommand.class);
    }

    @Override
    public IPermission getPermission() {
        return BPLPermission.PLAYERDATA_CMD;
    }

    public static class DisplayLimitSubCommand extends ExecutableCommand {
        public DisplayLimitSubCommand() {
            super("bpl", "limits", "display", "<id> [metadata]", "Displays all of the metadata limits for a specific ID, or the info for a specific meta limit");
        }

        @Override
        public IPermission getPermission() {
            return BPLPermission.LC_display;
        }

        @Override
        public void execute(CommandSender sender, RZLogger logger, CommandArgs args) {
            Integer id = args.getInteger(0);
            if (id == null) {
                logger.logFormat("Failed to parse the first parameter (block ID) as an integer");
                return;
            }

            LimitManager limitManager = LimitManager.getInstance();
            BlockLimiter limiter = limitManager.getLimiter(id);
            if (limiter == null) {
                logger.logFormat("A limiter with that ID doesnt exist");
                return;
            }

            Integer metadata = args.getInteger(1);
            logger.logFormat("&aBlock limiter | ID: " + limiter.id + " |-----------------------------");

            if (metadata == null) {
                for (MetaLimiter meta : limiter.metadata.values()) {
                    logger.logFormat("  &6Metadata: &3{0}", meta.dataPair.data);
                    logger.logFormat("  &6Bypass Permission: &3{0}{1}", ChatColor.DARK_AQUA, meta.bypassPermission);
                    logger.logFormat("  &6No permission for any limit range (message):");
                    logger.logFormat("    &3'&r{0}&3'", meta.noInitialPermsMsg);
                    logger.logFormat("  &6Other players allow to break owner's block: &3{0}", meta.allowOthersToBreakOwnerBlock);
                    logger.logFormat("  &6Owner block broken by other player (message):");
                    logger.logFormat("    &3'&r{0}&3'", Translator.nullMessageCheck(meta.otherPlayerBreakOwnerBlockMsg));
                    logger.logFormat("  &6Other player attempt to break owner's block (message):");
                    logger.logFormat("    &3'&r{0}&3'", Translator.nullMessageCheck(meta.otherPlayerBreakOwnerBlockAttemptMsg));
                    logger.logFormat("  &dLimited Ranges: (these might not be in order) ----------");
                    Collection<Map.Entry<IntegerRange, RangeLimit>> entries = meta.getRangeLimits().getEntrySets();
                    for (Iterator<Map.Entry<IntegerRange, RangeLimit>> iterator = entries.iterator(); iterator.hasNext(); ) {
                        Map.Entry<IntegerRange, RangeLimit> entry = iterator.next();
                        IntegerRange range = entry.getKey();
                        RangeLimit rangeLimit = entry.getValue();
                        logger.logFormat("    &6From &3{0} &6to &3{1}", range.min, range.max);
                        logger.logFormat("    &6Permission:");
                        logger.logFormat("      &3'&6{0}&3'", Translator.nullPermsCheck(rangeLimit.permission));
                        logger.logFormat("    &6Deny Message:");
                        logger.logFormat("      &3'&r{0}&3'", Translator.nullMessageCheck(rangeLimit.limitHitMessage));
                        if (iterator.hasNext()) {
                            logger.logTranslate("    &3--------------------------------------------");
                        }
                    }
                    logger.logTranslate("  &d-------------------------------------------------");
                    logger.logFormat("--------------------------------------------------");
                }
            }
            else {
                if (!limiter.containsSpecificMetaLimit(metadata)) {
                    if (metadata == -1) {
                        logger.logTranslate("&6A limiter for that metadata doesn't exist, but this limiter is using the -1 meta, so it applies to all metadata");
                    }
                    else {
                        logger.logTranslate("&6A limiter for that metadata doesn't exist");
                        return;
                    }
                }

                MetaLimiter meta = limiter.getMetaLimit(metadata);
                logger.logFormat("  &6Metadata: &3{0}", meta.dataPair.data);
                logger.logFormat("  &6Bypass Permission: &3{0}{1}", ChatColor.DARK_AQUA, meta.bypassPermission);
                logger.logFormat("  &6No permission for any limit range (message):");
                logger.logFormat("    &3'&r{0}&3'", meta.noInitialPermsMsg);
                logger.logFormat("  &6Other players allow to break owner's block: &3{0}", meta.allowOthersToBreakOwnerBlock);
                logger.logFormat("  &6Owner block broken by other player (message):");
                logger.logFormat("    &3'&r{0}&3'", Translator.nullMessageCheck(meta.otherPlayerBreakOwnerBlockMsg));
                logger.logFormat("  &6Other player attempt to break owner's block (message):");
                logger.logFormat("    &3'&r{0}&3'", Translator.nullMessageCheck(meta.otherPlayerBreakOwnerBlockAttemptMsg));
                logger.logFormat("  &dLimited Ranges: (these might not be in order) ----------");
                Collection<Map.Entry<IntegerRange, RangeLimit>> entries = meta.getRangeLimits().getEntrySets();
                for (Iterator<Map.Entry<IntegerRange, RangeLimit>> iterator = entries.iterator(); iterator.hasNext(); ) {
                    Map.Entry<IntegerRange, RangeLimit> entry = iterator.next();
                    IntegerRange range = entry.getKey();
                    RangeLimit rangeLimit = entry.getValue();
                    logger.logFormat("    &6From &3{0} &6to &3{1}", range.min, range.max);
                    logger.logFormat("    &6Permission:");
                    logger.logFormat("      &3'&6{0}&3'", Translator.nullPermsCheck(rangeLimit.permission));
                    logger.logFormat("    &6Deny Message:");
                    logger.logFormat("      &3'&r{0}&3'", Translator.nullMessageCheck(rangeLimit.limitHitMessage));
                    if (iterator.hasNext()) {
                        logger.logTranslate("    &3--------------------------------------------");
                    }
                }
                logger.logTranslate("  &d-------------------------------------------------");
                logger.logFormat("--------------------------------------------------");
            }
        }
    }
}
