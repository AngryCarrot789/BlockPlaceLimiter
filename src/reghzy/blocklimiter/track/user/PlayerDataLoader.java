package reghzy.blocklimiter.track.user;

import org.bukkit.plugin.Plugin;
import reghzy.blocklimiter.config.Config;
import reghzy.blocklimiter.exceptions.FailedFileCreationException;
import reghzy.blocklimiter.exceptions.IncorrectDataFormatException;
import reghzy.blocklimiter.track.ServerBlockTracker;
import reghzy.blocklimiter.track.block.TrackedBlock;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.world.Vector3;
import reghzy.blocklimiter.track.world.WorldBlockTracker;
import reghzy.blocklimiter.utils.StringHelper;
import reghzy.blocklimiter.utils.SplitString;
import reghzy.blocklimiter.utils.collections.multimap.MultiMapEntrySet;
import reghzy.blocklimiter.utils.logs.ChatFormat;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

public final class PlayerDataLoader {
    public static File PlayerDataFolder;
    private static final char WorldVectorSplit = ':';
    private static final char BlockSplitter = '=';
    private static final char LocationSplit = '|';

    private final ServerBlockTracker serverBlockTracker;

    public static void init(Plugin plugin) {
        PlayerDataFolder = new File(plugin.getDataFolder(), "players");
        PlayerDataFolder.mkdir();
    }

    public PlayerDataLoader(ServerBlockTracker serverBlockTracker) {
        this.serverBlockTracker = serverBlockTracker;
    }

    public File getOrCreateUserFile(String username) throws IOException, FailedFileCreationException {
        File file = new File(PlayerDataFolder, username + ".dat");
        if (!file.exists()) {
            if (!file.createNewFile())
                throw new FailedFileCreationException("Failed to create file for user: " + ChatFormat.apostrophise(username));
        }
        return file;
    }

    /**
     * loads all of the placed blocks in the file for the given user, and loads it into the world
     * @param file     The location to the player's config file. This file must exist
     * @param username The players username. this is the same as the file's name but without the extension
     * @throws IOException                  Thrown if an exception is thrown due to IO errors (reading files)
     * @throws IncorrectDataFormatException Thrown if the format of the data is wrong (possibly due to corruption)
     */
    public void loadPlayer(File file, String username) throws IOException, IncorrectDataFormatException {
        FileInputStream fileInput = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInput));

        while(true) {
            String line = reader.readLine();
            if (line == null)
                break;

            SplitString blockLocationSplit = SplitString.split(line, '=');
            if (blockLocationSplit == null) {
                throw new IncorrectDataFormatException("The split data between the block ID:Meta and the placed blocks data was incorrect. Data: " + line);
            }

            SplitString block = SplitString.split(blockLocationSplit.before, ':');
            if (block == null) {
                throw new IncorrectDataFormatException("The split data for the ID and Metadata was incorrect. Data: " + blockLocationSplit.before);
            }

            User user = serverBlockTracker.getUserManager().getUser(username);
            BlockDataPair blockData = new BlockDataPair(
                    StringHelper.parseInteger(block.before),
                    StringHelper.parseInteger(block.after));

            for(String location : StringHelper.split(blockLocationSplit.after, LocationSplit, 0)) {
                SplitString worldVector = SplitString.split(location, WorldVectorSplit);
                if (worldVector == null) {
                    throw new IncorrectDataFormatException("The split data between the world and location was incorrect. Data: " + location);
                }

                Vector3 parsed = Vector3.deserialise(worldVector.after);
                if (parsed == null) {
                    throw new IncorrectDataFormatException("A parsed vector3 was not formatted correctly: " + worldVector.after);
                }

                loadTrackedBlock(new TrackedBlock(user, worldVector.before, blockData, parsed));
            }
        }
    }

    public void savePlayer(File file, User user) throws IOException, FailedFileCreationException {
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new FailedFileCreationException("Failed to create user data file for player: " + user);
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

        ServerBlockTracker tracker = ServerBlockTracker.getInstance();
        UserDataManager manager = tracker.getUserManager();
        for(MultiMapEntrySet<BlockDataPair, TrackedBlock> limiters : manager.getBlockData(user).getBlockEntries()) {
            BlockDataPair blockData = limiters.getKey();
            Collection<TrackedBlock> blocks = limiters.getValues();
            if (blocks.size() > 0) {
                writer.append(blockData.toString()).append(BlockSplitter);
                for (TrackedBlock block : blocks) {
                    writer.append(block.getWorldName()).
                            append(WorldVectorSplit).
                            append(Vector3.serialise(block.getLocation())).
                            append(LocationSplit);
                }
                writer.write('\n');
            }
        }

        writer.close();
    }

    /**
     * gets whatever world the block is in, and "places" it in that world
     * @param block The block to be placed
     */
    public void loadTrackedBlock(TrackedBlock block) {
        this.serverBlockTracker.getWorldTracker(block.getWorldName()).placeBlock(block);
    }

    public void loadTrackedBlocks(WorldBlockTracker worldBlockTracker, ArrayList<TrackedBlock> blocks) {
        for(TrackedBlock block : blocks) {
            worldBlockTracker.placeBlock(block);
        }
    }
}
