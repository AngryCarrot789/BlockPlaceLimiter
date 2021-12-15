package reghzy.blocklimiter.limit;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import reghzy.api.commands.utils.RZLogger;
import reghzy.api.config.Config;
import reghzy.api.utils.text.StringHelper;
import reghzy.blocklimiter.BlockPlaceLimiterPlugin;
import reghzy.blocklimiter.track.ServerTracker;

import javax.naming.OperationNotSupportedException;
import java.util.HashMap;

public class LimitManager {
    private static LimitManager instance;

    public final HashMap<Integer, BlockLimiter> limits;

    public static final RZLogger LOGGER = BlockPlaceLimiterPlugin.LOGGER;

    public LimitManager() throws OperationNotSupportedException {
        if (LimitManager.instance == null) {
            LimitManager.instance = this;
        }
        else {
            throw new OperationNotSupportedException("LimitManager was already initialised!");
        }

        this.limits = new HashMap<Integer, BlockLimiter>(256);
    }

    public void loadLimits(Config config) {
        clearLimits();
        for (String blockIdKey : config.getKeys(false)) {
            Integer id = StringHelper.parseInteger(blockIdKey);
            if (id == null) {
                LOGGER.logFormatConsole("Failed to parse limiter key '{0}' as an integer", blockIdKey);
                if (StringHelper.countChar(blockIdKey, ':') > 1) {
                    LOGGER.logTranslateConsole("Make sure you don't put an ID and MetaData in the key, you must define the metadata within the block limiter section (see the example at the top of the config)");
                }
            }
            else {
                ConfigurationSection limitSection = config.getConfigurationSection(blockIdKey);
                if (limitSection == null) {
                    LOGGER.logFormatConsole("ID {0} had nothing in it", blockIdKey);
                }
                else {
                    try {
                        BlockLimiter limiter = BlockLimiter.createFromConfigSection(limitSection, id);
                        if (limiter != null) {
                            addBlockLimiter(id, limiter);
                        }
                    }
                    catch (Exception e) {
                        LOGGER.logTranslateConsole(e.getMessage());
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

    public static Config getLimitConfig() {
        return BlockPlaceLimiterPlugin.instance.getConfigManager().getConfig("limits");
    }
}
