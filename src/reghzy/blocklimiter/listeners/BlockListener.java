package reghzy.blocklimiter.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import reghzy.api.utils.BaseListener;
import reghzy.blocklimiter.limit.LimitManager;

public class BlockListener extends BaseListener implements Listener {
    private final LimitManager limitManager;

    public BlockListener(LimitManager limitManager, Plugin plugin) {
        super(plugin);
        this.limitManager = limitManager;
        register(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (limitManager.shouldCancelBlockBreak(event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (limitManager.shouldCancelBlockPlace(event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
            event.getPlayer().closeInventory();
        }
    }

    // @EventHandler(priority = EventPriority.HIGHEST)
    // public void onBlockEvent(BlockPhysicsEvent event) {
    //     if (event.isCancelled()) {
    //         return;
    //     }
    //     if (limitManager.shouldCancelBlockPlace(event.getPlayer(), event.getBlock())) {
    //         event.setCancelled(true);
    //         event.getPlayer().closeInventory();
    //     }
    // }
}