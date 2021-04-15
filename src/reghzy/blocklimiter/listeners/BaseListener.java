package reghzy.blocklimiter.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A base class for every listener class
 */
public abstract class BaseListener {
    public final JavaPlugin plugin;

    public BaseListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers a listener to the bukkit plugin manager using the plugin this instance contains
     */
    public void registerEvent(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this.plugin);
    }
}
