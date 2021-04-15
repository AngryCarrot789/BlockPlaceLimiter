package reghzy.blocklimiter.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import reghzy.blocklimiter.track.ServerBlockTracker;
import reghzy.blocklimiter.utils.logs.ChatLogger;

public class WorldListener extends BaseListener implements Listener {
    private final ServerBlockTracker serverBlockTracker;

    public WorldListener(ServerBlockTracker serverBlockTracker, JavaPlugin plugin) {
        super(plugin);
        this.serverBlockTracker = serverBlockTracker;
        registerEvent(this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWorldLoad(WorldLoadEvent event) {
        try {
            serverBlockTracker.onWorldLoad(event.getWorld());
        }
        catch (Exception e) {
            ChatLogger.logPlugin("Exception while loading a world");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWorldUnload(WorldUnloadEvent event) {
        try {
            serverBlockTracker.onWorldUnload(event.getWorld());
        }
        catch (Exception e) {
            ChatLogger.logPlugin("Exception while unloading a world");
        }
    }
}