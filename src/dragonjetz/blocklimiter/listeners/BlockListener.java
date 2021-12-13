package dragonjetz.blocklimiter.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import dragonjetz.api.utils.BaseListener;
import dragonjetz.blocklimiter.limit.LimitManager;

public class BlockListener extends BaseListener implements Listener {
    private final LimitManager limitManager;

    public BlockListener(LimitManager limitManager, JavaPlugin plugin) {
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
}