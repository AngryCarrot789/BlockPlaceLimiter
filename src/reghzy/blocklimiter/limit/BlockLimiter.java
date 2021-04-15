package reghzy.blocklimiter.limit;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import reghzy.blocklimiter.track.utils.IntegerRange;
import reghzy.blocklimiter.track.utils.PermissionMessagePair;
import reghzy.blocklimiter.utils.StringHelper;
import reghzy.blocklimiter.utils.logs.ChatFormat;
import reghzy.blocklimiter.utils.logs.ChatLogger;

import java.util.HashMap;
import java.util.Set;

public class BlockLimiter {
    public final int id;
    public final HashMap<Integer, MetaLimiter> metadata;

    public BlockLimiter(int id, HashMap<Integer, MetaLimiter> metadata) {
        this.id = id;
        this.metadata = metadata;
    }

    public static BlockLimiter createFromConfigSection(ConfigurationSection idSection, int id) {
        Set<String> metaIds = idSection.getKeys(false);
        if (metaIds == null || metaIds.size() == 0) {
            ChatLogger.logPlugin(ChatColor.RED + "Failed to parse integer for ID: " + ChatFormat.apostrophise(String.valueOf(id)));
            return null;
        }

        HashMap<Integer, MetaLimiter> metaLimits = new HashMap<Integer, MetaLimiter>(metaIds.size());
        for (String metaKey : metaIds) {
            Integer meta = StringHelper.parseInteger(metaKey);
            if (meta == null) {
                ChatLogger.logPlugin(ChatColor.RED + "Failed to parse integer for ID: " + ChatFormat.apostrophise(String.valueOf(id)) + " metadata: " + ChatFormat.apostrophise(metaKey));
                return null;
            }

            ConfigurationSection metaSection = idSection.getConfigurationSection(metaKey);
            if (metaSection == null) {
                ChatLogger.logPlugin(ChatColor.RED + "Metadata limit had nothing in it for ID: " + ChatFormat.apostrophise(String.valueOf(id)) + " metadata: " + ChatFormat.apostrophise(metaKey));
                return null;
            }

            String bypassPermission = LimitConfigHelper.getBypassPermission(metaSection, null);
            String noInitialPermsMsg = LimitConfigHelper.getNoInitialPermsMsg(metaSection, null);
            boolean allowOthersToBreakOwnerBlock = LimitConfigHelper.getAllowOthersToBreakOwnerBlock(metaSection, true);
            String otherPlayerBrokeOwnerBlockMsg = LimitConfigHelper.getOtherPlayerBrokenOwnerBlockMsg(metaSection, null);
            String otherPlayerBreakOwnerBlockAttemptMsg = LimitConfigHelper.getOtherPlayerBreakOwnerBlockAttemptMsg(metaSection, null);

            ConfigurationSection limitsSection = LimitConfigHelper.getRangeLimitSection(metaSection);
            if (limitsSection == null) {
                ChatLogger.logPlugin(ChatColor.RED + "Limits section for ID: " + ChatFormat.apostrophise(String.valueOf(id)) + " metadata: " + ChatFormat.apostrophise(metaKey));
                return null;
            }

            RangeLimits rangeLimits = new RangeLimits();
            for(String limitKey : limitsSection.getKeys(false)) {
                IntegerRange range = StringHelper.parseRange(limitKey, "to", 0);
                if (range == null) {
                    ChatLogger.logPlugin(ChatColor.RED + "Range limit wasn't formatted correctly for the limit: " + ChatFormat.apostrophise(limitKey) + ", for ID: " + ChatFormat.apostrophise(String.valueOf(id)) + " metadata: " + ChatFormat.apostrophise(metaKey));
                    ChatLogger.logPlugin("Example: 0 to 10 <-- 2 numbers, and between them should be 'to' (spaces probably not required)");
                    return null;
                }

                ConfigurationSection rangeSection = limitsSection.getConfigurationSection(limitKey);
                if (rangeSection == null) {
                    ChatLogger.logPlugin(ChatColor.RED + "A Range limit's section had nothing in it for the limit: " + ChatFormat.apostrophise(limitKey) + ", for ID: " + ChatFormat.apostrophise(String.valueOf(id)) + " metadata: " + ChatFormat.apostrophise(metaKey));
                    return null;
                }

                String permission  = LimitConfigHelper.getRangePlacePermission(rangeSection, null);
                if (permission == null) {
                    ChatLogger.logPlugin(ChatColor.RED + "There was no permission for the limit: " + ChatFormat.apostrophise(limitKey) + ", for ID: " + ChatFormat.apostrophise(String.valueOf(id)) + " metadata: " + ChatFormat.apostrophise(metaKey));
                    return null;
                }

                String limitHitMsg = LimitConfigHelper.getLimitHitMessage(rangeSection, "&3You have reached your limit for how many of this block you can place");
                rangeLimits.addPermission(range, new PermissionMessagePair(permission, limitHitMsg));
            }

            metaLimits.put(meta, new MetaLimiter(
                    id, meta,
                    bypassPermission,
                    noInitialPermsMsg,
                    allowOthersToBreakOwnerBlock,
                    otherPlayerBrokeOwnerBlockMsg,
                    otherPlayerBreakOwnerBlockAttemptMsg,
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
}
