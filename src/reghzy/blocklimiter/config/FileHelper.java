package reghzy.blocklimiter.config;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileHelper {
    /**
     * gets a file instance which locates to a file in the plugin's data folder
     */
    public static File getFileInDataFolder(Plugin plugin, String fileName) {
        return new File(plugin.getDataFolder(), fileName);
    }

    /**
     * Tries to get a file with the given name that exists in the plugin's jar file. returns null if one doesnt exist
     * @param plugin
     * @param configNameWithExtension
     * @return
     */
    public static InputStream getDefaultConfig(Plugin plugin, String configNameWithExtension) {
        return plugin.getResource(configNameWithExtension);
    }

    /**
     * Creates the plugin's data folder (using the mkdir() function)
     * @param plugin
     * @return
     */
    public static boolean ensurePluginFolderExists(Plugin plugin) {
        return plugin.getDataFolder().mkdir();
    }

    /**
     * Copies an input stream to the given destination
     */
    public static boolean copyResourceTo(InputStream input, File destination) {
        try {
            FileOutputStream output = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];

            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.close();
            input.close();
            return true;
        }
        catch (IOException ignored) {
            return false;
        }
    }
}
