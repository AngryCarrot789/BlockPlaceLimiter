package reghzy.blocklimiter.utils;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WorldEditHelper {
    private static WorldEditPlugin worldEditPlugin;

    public static void init() {
        worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
    }

    public static Selection getPlayerSelection(Player player) {
        return worldEditPlugin.getSelection(player);
    }
}
