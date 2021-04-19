package reghzy.blocklimiter.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import reghzy.blocklimiter.track.ServerTracker;
import reghzy.blocklimiter.utils.logs.ChatLogger;

public class WorldListener extends BaseListener implements Listener {
    private final ServerTracker serverTracker;

    public WorldListener(ServerTracker serverTracker, JavaPlugin plugin) {
        super(plugin);
        this.serverTracker = serverTracker;
        registerEvent(this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWorldLoad(WorldLoadEvent event) {
        try {
            serverTracker.onWorldLoad(event.getWorld());
        }
        catch (Exception e) {
            ChatLogger.logPlugin("Exception while loading a world");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWorldUnload(WorldUnloadEvent event) {
        try {
            serverTracker.onWorldUnload(event.getWorld());
        }
        catch (Exception e) {
            ChatLogger.logPlugin("Exception while unloading a world");
        }
    }
}