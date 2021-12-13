package dragonjetz.blocklimiter.limit;

import org.bukkit.configuration.ConfigurationSection;
import dragonjetz.api.commands.utils.DJLogger;
import dragonjetz.api.utils.text.StringHelper;
import dragonjetz.blocklimiter.BlockPlaceLimiterPlugin;
import dragonjetz.blocklimiter.track.utils.IntegerRange;
import dragonjetz.blocklimiter.track.utils.RangeLimit;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BlockLimiter {
    public final int id;
    public final HashMap<Integer, MetaLimiter> metadata;

    public static final DJLogger LOGGER = BlockPlaceLimiterPlugin.LOGGER;
    
    public BlockLimiter(int id, HashMap<Integer, MetaLimiter> metadata) {
        this.id = id;
        this.metadata = metadata;
    }

    public static BlockLimiter createFromConfigSection(ConfigurationSection idSection, int id) {
        Set<String> metaIds = idSection.getKeys(false);
        if (metaIds == null || metaIds.size() == 0) {
            LOGGER.logFormatConsole("&cThere were no meta limits for the ID '{0}'", id);
            return null;
        }

        HashMap<Integer, MetaLimiter> metaLimits = new HashMap<Integer, MetaLimiter>(metaIds.size());
        for (String metaKey : metaIds) {
            Integer meta = StringHelper.parseInteger(metaKey);
            if (meta == null) {
                LOGGER.logFormatConsole("&cFailed to parse integer for ID '{0}' with metadata '{1}' ", id, metaKey);
                return null;
            }

            ConfigurationSection metaSection = idSection.getConfigurationSection(metaKey);
            if (metaSection == null) {
                LOGGER.logFormatConsole("&cMetadata limit had nothing in it for ID '{0}' with metadata '{1}' ", id, metaKey);
                return null;
            }

            String bypassPermission = LimitConfigHelper.getBypassPermission(metaSection, null);
            String noInitialPermsMsg = LimitConfigHelper.getNoInitialPermsMsg(metaSection, null);
            String bypassBreakPerms = LimitConfigHelper.getBypassBreakPermission(metaSection, null);
            boolean allowOthersToBreakOwnerBlock = LimitConfigHelper.getAllowOthersToBreakOwnerBlock(metaSection, true);
            String otherPlayerBrokeOwnerBlockMsg = LimitConfigHelper.getOtherPlayerBrokenOwnerBlockMsg(metaSection, null);
            String otherPlayerBreakOwnerBlockAttemptMsg = LimitConfigHelper.getOtherPlayerBreakOwnerBlockAttemptMsg(metaSection, null);
            String youBrokeOwnerBlockMsg = LimitConfigHelper.getYouBreakOwnerBlockMsg(metaSection, null);
            String youBreakOwnerBlockAttemptMsg = LimitConfigHelper.getYouBreakOwnerBlockAttemptMsg(metaSection, null);

            ConfigurationSection rangeLimitsSection = LimitConfigHelper.getRangeLimitSection(metaSection);
            if (rangeLimitsSection == null) {
                LOGGER.logFormatConsole("&cHad no limits section for ID '{0}' with metadata '{1}'!!", id, metaKey);
                return null;
            }

            RangeLimits rangeLimits = new RangeLimits();
            for(String rangeLimit : rangeLimitsSection.getKeys(false)) {
                IntegerRange range = parseRange(rangeLimit);
                if (range == null) {
                    LOGGER.logFormatConsole("&cRange limit wasn't formatted correctly ('{0}') for ID '{1}' with metadata '{2}' ", rangeLimit, id, metaKey);
                    LOGGER.logFormatConsole("Example: 0 to 10 <-- 2 numbers, and between them should be 'to' (spaces don't matter, they're ignored)");
                    return null;
                }

                if (range.min > range.max) {
                    LOGGER.logFormatConsole("&cThe min value ({0}) was bigger than the max value ({1}) for the limit '{2}' for ID '{3}' with metadata '{4}' ", range.min, range.max, rangeLimit, id, metaKey);
                    return null;
                }

                ConfigurationSection rangeSection = rangeLimitsSection.getConfigurationSection(rangeLimit);
                if (rangeSection == null) {
                    LOGGER.logFormatConsole("&cA Range limit's section had nothing in it for the limit '{0}' for ID '{1}' with metadata '{2}' ", rangeLimit, id, metaKey);
                    return null;
                }

                String permission  = LimitConfigHelper.getRangePlacePermission(rangeSection, null);
                if (permission == null) {
                    LOGGER.logFormatConsole("&cThere was no permission for the limit '{0}' for ID '{1}' with metadata '{2}' ", rangeLimit, id, metaKey);
                    return null;
                }

                rangeLimits.addPermission(range, new RangeLimit(permission, LimitConfigHelper.getLimitHitMessage(rangeSection, "&3You have reached your limit for how many of this block you can place")));
            }

            // check ranges to ensure the config is done properly
            int min = Integer.MAX_VALUE;
            int max = 0;
            int total = 0;
            for(Map.Entry<IntegerRange, RangeLimit> entry : rangeLimits.getEntrySets()) {
                IntegerRange range = entry.getKey();
                if (range.max > max) {
                    max = range.max;
                }
                if (range.min < min) {
                    min = range.min;
                }

                total += (range.max - range.min);
            }


            if (min > 0) {
                LOGGER.logFormatConsole("&cWarning, Badly configured range: The smallest 'from' range was {0}, it must be 0! For the limiter with ID '{1}' with metadata '{2}'", min, id, metaKey);
                return null;
            }

            total += (rangeLimits.getEntrySets().size() - 1);
            if (total != max) {
                LOGGER.logFormatConsole("&cWarning: Badly configured range: The ranges were badly done for the limiter with ID '{0}' with metadata '{1}'. Total was {2}, expected was {3}", id, metaKey, total, max);
                return null;
            }

            metaLimits.put(meta, new MetaLimiter(
                    id, meta,
                    bypassPermission,
                    noInitialPermsMsg,
                    bypassBreakPerms,
                    allowOthersToBreakOwnerBlock,
                    otherPlayerBrokeOwnerBlockMsg,
                    otherPlayerBreakOwnerBlockAttemptMsg,
                    youBrokeOwnerBlockMsg,
                    youBreakOwnerBlockAttemptMsg,
                    rangeLimits));
        }

        return new BlockLimiter(id, metaLimits);
    }

    public MetaLimiter getMetaLimit(int data) {
        MetaLimiter ignore = getIgnoreMeta();
        if (ignore == null) {
            return this.metadata.get(data);
        }
        return ignore;
    }

    public boolean containsSpecificMetaLimit(int data) {
        return this.metadata.containsKey(data);
    }

    public MetaLimiter getIgnoreMeta() {
        return this.metadata.get(-1);
    }

    @Override
    public int hashCode() {
        return this.id + (this.metadata.size() << 12);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockLimiter) {
            BlockLimiter limit = (BlockLimiter) obj;
            return limit.id == this.id && limit.metadata.equals(this.metadata);
        }
        return false;
    }

    public static IntegerRange parseRange(String value) {
        value = StringHelper.remove(value, ' ', 0);
        int index = value.indexOf("to");
        if (index == -1) {
            return null;
        }

        Integer from = StringHelper.parseInteger(value.substring(0, index));
        if (from == null) {
            return null;
        }

        Integer to = StringHelper.parseInteger(value.substring(index + 2));
        if (to == null) {
            return null;
        }

        return new IntegerRange(from, to);
    }
}
