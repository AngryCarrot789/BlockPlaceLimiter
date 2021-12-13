package dragonjetz.blocklimiter.track.user.data;

import dragonjetz.api.utils.text.DJFormats;
import dragonjetz.api.utils.text.StringHelper;
import dragonjetz.api.utils.types.SplitString;
import dragonjetz.blocklimiter.exceptions.FailedFileCreationException;
import dragonjetz.blocklimiter.exceptions.IncorrectDataFormatException;
import dragonjetz.blocklimiter.track.ServerTracker;
import dragonjetz.blocklimiter.track.user.User;
import dragonjetz.blocklimiter.track.user.UserBlockData;
import dragonjetz.blocklimiter.track.utils.BlockDataPair;
import dragonjetz.blocklimiter.track.world.TrackedBlock;
import dragonjetz.blocklimiter.track.world.Vector3;
import dragonjetz.blocklimiter.utils.collections.multimap.MultiMapEntry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

public class StringBasedPlayerDataLoader extends PlayerDataLoader {
    private static final char WorldVectorSplit = ':';
    private static final char BlockSplitter = '=';
    private static final char LocationSplit = '|';

    public StringBasedPlayerDataLoader(ServerTracker serverTracker) {
        super(serverTracker);
    }

    @Override
    public void loadPlayer(File file, String username) throws IOException, IncorrectDataFormatException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }

            if (line.isEmpty()) {
                continue;
            }

            SplitString blockLocationSplit = SplitString.split(line, BlockSplitter);
            if (blockLocationSplit == null) {
                throw new IncorrectDataFormatException(DJFormats.format("The split data between the block ID:Meta and the placed blocks data (split by a '{0}' char) was incorrect. Data: '{1}'", BlockSplitter, line));
            }

            SplitString block = SplitString.split(blockLocationSplit.before, WorldVectorSplit);
            if (block == null) {
                throw new IncorrectDataFormatException(DJFormats.format("The data for the ID and Metadata (split by a '{0}' char) was incorrect. Data: '{1}'", WorldVectorSplit, blockLocationSplit.before));
            }

            Integer id = StringHelper.parseInteger(block.before);
            if (id == null) {
                throw new IncorrectDataFormatException(DJFormats.format("The ID for a specific block wasn't an integer! ID Data: '{0}'", block.before));
            }

            Integer meta = StringHelper.parseInteger(block.after);
            if (meta == null) {
                throw new IncorrectDataFormatException(DJFormats.format("The Metadata for a specific block wasn't an integer! Meta Data: '{0}'", block.after));
            }

            User user = this.serverTracker.getUserManager().getUser(username);
            BlockDataPair blockData = new BlockDataPair(id, meta);
            for (String location : StringHelper.split(blockLocationSplit.after, LocationSplit, 0)) {
                if (location.length() < 2) {
                    continue;
                }

                SplitString worldVector = SplitString.split(location, WorldVectorSplit);
                if (worldVector == null) {
                    throw new IncorrectDataFormatException(DJFormats.format("The split data between the world and block location (split by a '{0}' char) was incorrect. Data: '{1}'", WorldVectorSplit, location));
                }

                Vector3 parsedLocation = Vector3.deserialise(worldVector.after);
                if (parsedLocation == null) {
                    throw new IncorrectDataFormatException(DJFormats.format("A parsed Vector3 (X,Y,Z) was not formatted correctly. Data: '{0}'", worldVector.after));
                }

                this.serverTracker.placeNewBlockAt(worldVector.before, user, blockData, parsedLocation);
            }
        }
    }

    @Override
    public boolean savePlayer(File file, User user, boolean forceIfUnchanged) throws IOException, FailedFileCreationException {
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new FailedFileCreationException("Failed to create user data file for player: " + user.getName());
                }
            }
            catch (IOException e) {
                throw new FailedFileCreationException("IOException while creating user data file for player: " + user.getName());
            }
        }

        UserBlockData data = ServerTracker.getInstance().getUserManager().getBlockData(user);
        if (data.hasDataChanged() || forceIfUnchanged) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            for (MultiMapEntry<BlockDataPair, TrackedBlock> limiters : data.getBlockEntries()) {
                BlockDataPair blockData = limiters.getKey();
                Collection<TrackedBlock> placedBlocks = limiters.getValues();
                if (placedBlocks.size() > 0) {
                    writer.append(blockData.toString()).append(BlockSplitter);
                    for (TrackedBlock block : placedBlocks) {
                        writer.append(block.getWorldName()).
                                append(WorldVectorSplit).
                                append(Vector3.serialise(block.getLocation())).
                                append(LocationSplit);
                    }

                    writer.write('\n');
                }
            }

            writer.close();
            data.setDataChanged(false);
            return true;
        }
        else {
            return false;
        }
    }
}
