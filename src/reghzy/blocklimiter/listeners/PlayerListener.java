package reghzy.blocklimiter.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import reghzy.blocklimiter.track.ServerTracker;

public class PlayerListener extends BaseListener implements Listener {
    private final ServerTracker serverTracker;

    public PlayerListener(ServerTracker serverTracker, JavaPlugin plugin) {
        super(plugin);
        this.serverTracker = serverTracker;
        registerEvent(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player == null)
            return;

        serverTracker.onPlayerJoin(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player == null)
            return;

        serverTracker.onPlayerLeave(player);
    }
}