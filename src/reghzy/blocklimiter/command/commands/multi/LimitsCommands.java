package reghzy.blocklimiter.command.commands.multi;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import reghzy.blocklimiter.command.utils.CommandArgs;
import reghzy.blocklimiter.command.CommandLogger;
import reghzy.blocklimiter.command.ExecutableCommand;
import reghzy.blocklimiter.command.ExecutableSubCommands;
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
        super("limits", "Contains commands for listing limited blocks");
    }

    @Override
    public void registerCommands() {
        registerCommand("display", new DisplayLimitSubCommand());
    }

    private static class DisplayLimitSubCommand extends ExecutableCommand {
        public DisplayLimitSubCommand() {
            super("limits", "display", "<id> [metadata]", "Displays all of the metadata limits for a specific ID, or the info for a specific meta limit");
        }

        @Override
        public void execute(CommandSender sender, CommandLogger logger, CommandArgs args) {
            Integer id = args.getInteger(0);
            if (id == null) {
                logger.logPrefix("Failed to parse the first parameter (block ID) as an integer");
                return;
            }

            LimitManager limitManager = LimitManager.getInstance();
            BlockLimiter limiter = limitManager.getLimiter(id);
            if (limiter == null) {
                logger.logPrefix("A limiter with that ID doesnt exist");
                return;
            }

            Integer metadata = args.getInteger(1);
            logger.logGreen("Block limiter | ID: " + limiter.id + " |-----------------------------");

            if (metadata == null) {
                for (MetaLimiter meta : limiter.metadata.values()) {
                    logger.logTranslate("  &6Metadata: &3" + meta.dataPair.data);
                    logger.logTranslate("  &6Bypass Permission: &3" + ChatColor.DARK_AQUA + meta.bypassPermission);
                    logger.logTranslate("  &6No permission for any limit range (message):");
                    logger.logTranslate("    " + meta.noInitialPermsMsg);
                    logger.logTranslate("  &6Other players allow to break owner's block: &3" + meta.allowOthersToBreakOwnerBlock);
                    logger.logTranslate("  &6Owner block broken by other player (message):");
                    logger.logTranslate("    " + Translator.nullMessageCheck(meta.otherPlayerBrekeOwnerBlockMsg));
                    logger.logTranslate("  &6Other player attempt to break owner's block (message):");
                    logger.logTranslate("    " + Translator.nullMessageCheck(meta.otherPlayerBreakOwnerBlockAttemptMsg));
                    logger.logTranslate("  &dLimited Ranges: (these might not be in order) ----------");
                    Collection<Map.Entry<IntegerRange, RangeLimit>> entries = meta.getRangeLimits().getEntrySets();
                    for (Iterator<Map.Entry<IntegerRange, RangeLimit>> iterator = entries.iterator(); iterator.hasNext(); ) {
                        Map.Entry<IntegerRange, RangeLimit> entry = iterator.next();
                        IntegerRange range = entry.getKey();
                        RangeLimit rangeLimit = entry.getValue();
                        logger.logTranslate("    &6From &3" + range.min + " &6to &3" + range.max);
                        logger.logTranslate("    &6Permission:");
                        logger.logTranslate("      &3" + Translator.nullPermsCheck(rangeLimit.permission));
                        logger.logTranslate("    &6Deny Message:");
                        logger.logTranslate("      " + Translator.nullMessageCheck(rangeLimit.limitHitMessage));
                        if (iterator.hasNext()) {
                            logger.logTranslate("    &3--------------------------------------------");
                        }
                    }
                    logger.logTranslate("  &d-------------------------------------------------");
                    logger.logGreen("--------------------------------------------------");
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
                logger.logTranslate("  &6Metadata: &3" + meta.dataPair.data);
                logger.logTranslate("  &6Bypass Permission: &3" + ChatColor.DARK_AQUA + meta.bypassPermission);
                logger.logTranslate("  &6No permission for any limit range (message):");
                logger.logTranslate("   &3Ⱶ &r" + meta.noInitialPermsMsg);
                logger.logTranslate("  &6Other players allow to break owner's block: &3" + meta.allowOthersToBreakOwnerBlock);
                logger.logTranslate("  &6Owner block broken by other player (message):");
                logger.logTranslate("   &3Ⱶ &r" + Translator.nullMessageCheck(meta.otherPlayerBrekeOwnerBlockMsg));
                logger.logTranslate("  &6Other player attempt to break owner's block (message):");
                logger.logTranslate("   &3Ⱶ &r" + Translator.nullMessageCheck(meta.otherPlayerBreakOwnerBlockAttemptMsg));
                logger.logTranslate("  &dLimited Ranges: (these might not be in order) ----------");
                Collection<Map.Entry<IntegerRange, RangeLimit>> entries = meta.getRangeLimits().getEntrySets();
                for (Iterator<Map.Entry<IntegerRange, RangeLimit>> iterator = entries.iterator(); iterator.hasNext(); ) {
                    Map.Entry<IntegerRange, RangeLimit> entry = iterator.next();
                    IntegerRange range = entry.getKey();
                    RangeLimit rangeLimit = entry.getValue();
                    logger.logTranslate("    &6From &3" + range.min + " &6to &3" + range.max);
                    logger.logTranslate("    &6Permission:");
                    logger.logTranslate("      &3" + Translator.nullPermsCheck(rangeLimit.permission));
                    logger.logTranslate("    &6Deny Message:");
                    logger.logTranslate("      " + Translator.nullMessageCheck(rangeLimit.limitHitMessage));
                    if (iterator.hasNext()) {
                        logger.logTranslate("    &3--------------------------------------------");
                    }
                }
                logger.logTranslate("  &d-------------------------------------------------");
                logger.logGreen("--------------------------------------------------");
            }
        }
    }
}
