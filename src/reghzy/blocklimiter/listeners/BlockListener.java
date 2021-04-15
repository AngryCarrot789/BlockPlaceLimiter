package reghzy.blocklimiter.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import reghzy.blocklimiter.limit.LimitManager;

public class BlockListener extends BaseListener implements Listener {
    private final LimitManager serverBlockTracker;

    public BlockListener(LimitManager serverBlockTracker, JavaPlugin plugin) {
        super(plugin);
        this.serverBlockTracker = serverBlockTracker;
        registerEvent(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (serverBlockTracker.shouldCancelBlockBreak(event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (serverBlockTracker.shouldCancelBlockPlace(event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
        }
    }
}