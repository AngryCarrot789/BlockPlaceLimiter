package reghzy.blocklimiter.limit;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import reghzy.blocklimiter.config.Config;
import reghzy.blocklimiter.config.ConfigManager;
import reghzy.blocklimiter.track.ServerTracker;
import reghzy.blocklimiter.utils.StringHelper;
import reghzy.blocklimiter.utils.logs.ChatFormat;
import reghzy.blocklimiter.utils.logs.ChatLogger;
import reghzy.blocklimiter.utils.permissions.PermissionsHelper;

import javax.naming.OperationNotSupportedException;
import java.util.HashMap;

public class LimitManager {
    private static LimitManager instance;

    private final HashMap<Integer, BlockLimiter> limits;

    public LimitManager() throws OperationNotSupportedException {
        if (LimitManager.instance == null) {
            LimitManager.instance = this;
        }
        else {
            throw new OperationNotSupportedException("LimitManager was already initialised!");
        }

        this.limits = new HashMap<Integer, BlockLimiter>(256);
        loadLimits(ConfigManager.getLimitConfig());
    }

    public void loadLimits(Config config) {
        clearLimits();

        for (String blockIdKey : config.getKeys(false)) {
            Integer id = StringHelper.parseInteger(blockIdKey);
            if (id == null) {
                ChatLogger.logPlugin("Failed to parse limiter key as an integer. Key: " + ChatFormat.apostrophise(blockIdKey));
                if (StringHelper.countChar(blockIdKey, ':') > 1) {
                    ChatLogger.logPlugin("Make sure you dont put an ID and MetaData in the key, you must define the metadata within the block limiter section (see the example at the top of the config)");
                }
            }
            else {
                ConfigurationSection limitSection = config.getConfigurationSection(blockIdKey);
                if (limitSection == null) {
                    ChatLogger.logPlugin("ID " + blockIdKey + " had nothing in it");
                }
                else {
                    try {
                        BlockLimiter limiter = BlockLimiter.createFromConfigSection(limitSection, id);
                        if (limiter != null) {
                            addBlockLimiter(id, limiter);
                        }
                    }
                    catch (Exception e) {
                        ChatLogger.logConsole(e.getMessage());
                    }
                }
            }
        }
    }

    public boolean shouldCancelBlockBreak(Player player, Block block) {
        BlockLimiter limiter = this.limits.get(block.getTypeId());
        if (limiter == null)
            return false;

        MetaLimiter metaLimiter = limiter.getMetaLimit(block.getData());
        if (metaLimiter == null)
            return false;

        if (metaLimiter.playerBypassesChecks(player))
            return false;

        return ServerTracker.getInstance().shouldCancelBlockBreak(player, block, metaLimiter);
    }

    public boolean shouldCancelBlockPlace(Player player, Block block) {
        BlockLimiter limiter = this.limits.get(block.getTypeId());
        if (limiter == null)
            return false;

        MetaLimiter metaLimiter = limiter.getMetaLimit(block.getData());
        if (metaLimiter == null)
            return false;

        if (metaLimiter.playerBypassesChecks(player))
            return false;

        if (PermissionsHelper.hasPermission(player, metaLimiter.bypassPermission))
            return false;

        return ServerTracker.getInstance().shouldCancelBlockPlace(player, block, metaLimiter);
    }

    public void addBlockLimiter(int id, BlockLimiter limiter) {
        this.limits.put(id, limiter);
    }

    public BlockLimiter getLimiter(int id) {
        return limits.get(id);
    }

    public BlockLimiter getLimiter(Block block) {
        return getLimiter(block.getTypeId());
    }

    public void clearLimits() {
        this.limits.clear();
    }

    public int limitsCount() {
        return this.limits.size();
    }

    public static LimitManager getInstance() {
        return LimitManager.instance;
    }
}
