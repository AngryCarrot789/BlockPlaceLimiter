package reghzy.blocklimiter.utils;

import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;

public class WorldHelper {
    public static ArrayList<Block> blocksBetweenSelection(Selection selection) {
        Location a = selection.getMinimumPoint();
        Location b = selection.getMinimumPoint();
        if (a == null || b == null) {
            return null;
        }

        return getBlocksBetween(a, b);
    }

    public static ArrayList<Block> getBlocksBetween(Location loc1, Location loc2) {
        ArrayList<Block> blocks = new ArrayList<Block>();

        int topBlockX = (Math.max(loc1.getBlockX(), loc2.getBlockX()));
        int bottomBlockX = (Math.min(loc1.getBlockX(), loc2.getBlockX()));
        int topBlockY = (Math.max(loc1.getBlockY(), loc2.getBlockY()));
        int bottomBlockY = (Math.min(loc1.getBlockY(), loc2.getBlockY()));
        int topBlockZ = (Math.max(loc1.getBlockZ(), loc2.getBlockZ()));
        int bottomBlockZ = (Math.min(loc1.getBlockZ(), loc2.getBlockZ()));

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    blocks.add(loc1.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        return blocks;
    }
}
