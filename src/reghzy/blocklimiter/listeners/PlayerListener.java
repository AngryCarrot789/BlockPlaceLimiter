package reghzy.blocklimiter.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import reghzy.blocklimiter.track.ServerBlockTracker;

public class PlayerListener extends BaseListener implements Listener {
    private final ServerBlockTracker serverBlockTracker;

    public PlayerListener(ServerBlockTracker serverBlockTracker, JavaPlugin plugin) {
        super(plugin);
        this.serverBlockTracker = serverBlockTracker;
        registerEvent(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player == null)
            return;

        serverBlockTracker.onPlayerJoin(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player == null)
            return;

        serverBlockTracker.onPlayerLeave(player);
    }
}