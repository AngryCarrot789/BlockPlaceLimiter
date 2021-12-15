package reghzy.blocklimiter.listeners;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import reghzy.api.utils.BaseListener;
import reghzy.blocklimiter.track.ServerTracker;

public class WorldListener extends BaseListener implements Listener {
    private final ServerTracker serverTracker;

    public WorldListener(ServerTracker serverTracker, JavaPlugin plugin) {
        super(plugin);
        this.serverTracker = serverTracker;
        register(this);
    }

    // @EventHandler(priority = EventPriority.MONITOR)
    // public void onWorldLoad(WorldLoadEvent event) {
    //     try {
    //         serverTracker.onWorldLoad(event.getWorld());
    //     }
    //     catch (Exception e) {
    // 
    //     }
    // }

    // @EventHandler(priority = EventPriority.HIGH)
    // public void onWorldUnload(WorldUnloadEvent event) {
    //     try {
    //         serverTracker.onWorldUnload(event.getWorld());
    //     }
    //     catch (Exception e) {
    //         ChatLogger.logPlugin("Exception while unloading a world");
    //     }
    // }
}