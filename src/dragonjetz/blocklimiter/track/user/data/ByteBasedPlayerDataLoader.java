package dragonjetz.blocklimiter.track.user.data;

import dragonjetz.api.utils.text.DJFormats;
import dragonjetz.blocklimiter.exceptions.FailedFileCreationException;
import dragonjetz.blocklimiter.exceptions.IncorrectDataFormatException;
import dragonjetz.blocklimiter.track.ServerTracker;
import dragonjetz.blocklimiter.track.user.User;
import dragonjetz.blocklimiter.track.user.UserBlockData;
import dragonjetz.blocklimiter.track.utils.BlockDataPair;
import dragonjetz.blocklimiter.track.world.TrackedBlock;
import dragonjetz.blocklimiter.track.world.Vector3;
import dragonjetz.blocklimiter.utils.collections.multimap.MultiMapEntry;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class ByteBasedPlayerDataLoader extends PlayerDataLoader {
    public ByteBasedPlayerDataLoader(ServerTracker serverTracker) {
        super(serverTracker);
    }

    private static String readWorldName(DataInput input) throws IOException {
        try {
            int length = input.readUnsignedShort();
            StringBuilder str = new StringBuilder(length);
            for (int i = 0; i < length; ++i) {
                str.append(input.readChar());
            }

            return str.toString();
        }
        catch (IOException e) {
            throw new IOException("Failed to read world name", e);
        }
    }

    @Override
    public void loadPlayer(File file, String username) throws IOException, IncorrectDataFormatException {
        FileInputStream fileIn = new FileInputStream(file);
        DataInputStream input = new DataInputStream(new BufferedInputStream(fileIn));
        User user = this.serverTracker.getUserManager().getUser(username);
        if (file.length() >= 4) { // ensure there's enough data to read the total number of tracked blocks
            int totalBlocks = input.readInt();                      // read number of entries
            for (int i = 0; i < totalBlocks; i++) {
                try {
                    int id;
                    int data;
                    int blockCount;
                    try {
                        id = input.readUnsignedShort();             // read id (2 bytes)
                        data = input.readUnsignedShort();           // read data (2 bytes)
                        blockCount = input.readUnsignedShort();     // read number of blocks (2 bytes)
                    }
                    catch (IOException e) {
                        throw new IOException("Failed to read block sector entry header", e);
                    }

                    BlockDataPair dataPair = new BlockDataPair(id, data);
                    for (int j = 0; j < blockCount; j++) {
                        String worldName;
                        int x;
                        int y;
                        int z;

                        try {
                            worldName = readWorldName(input);       // read world name (2+ bytes)
                            x = input.readInt();                    // read X coord (4 bytes)
                            y = input.readUnsignedByte();           // read Y coord (1 byte)
                            z = input.readInt();                    // read Z coord (4 bytes)
                        }
                        catch (IOException e) {
                            throw new IOException(DJFormats.format("Failed to read tracked block entry {0}/{1} for block '{2}:{3}'", j + 1, blockCount, id, data), e);
                        }

                        this.serverTracker.placeNewBlockAt(worldName, user, dataPair, new Vector3(x, y, z));
                    }
                }
                catch (IOException e) {
                    throw new IOException(DJFormats.format("Failed to read block sector entry {0}/{1}", i, totalBlocks), e);
                }
            }
        }
    }

    @Override
    public boolean savePlayer(File file, User user, boolean forceIfUnchanged) throws IOException, FailedFileCreationException {
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new FailedFileCreationException("Failed to create user data file for player: " + user.getName());
            }
        }

        UserBlockData data = ServerTracker.getInstance().getUserManager().getBlockData(user);
        if (data.hasDataChanged() || forceIfUnchanged) {
            FileOutputStream fileOut = new FileOutputStream(file, false);
            DataOutputStream output = new DataOutputStream(new BufferedOutputStream(fileOut));
            Collection<MultiMapEntry<BlockDataPair, TrackedBlock>> trackedBlocks = data.getBlockEntries();
            output.writeInt(trackedBlocks.size());              // write number of entries (4 bytes)
            for (MultiMapEntry<BlockDataPair, TrackedBlock> limiters : trackedBlocks) {
                BlockDataPair blockData = limiters.getKey();
                Collection<TrackedBlock> placedBlocks = limiters.getValues();
                if (placedBlocks.size() > 0) {
                    try {
                        try {
                            output.writeShort(blockData.id);            // write id (2 bytes)
                            output.writeShort(blockData.data);          // write data (2 bytes)
                            output.writeShort(placedBlocks.size());     // write the number of blocks (2 bytes)
                        }
                        catch (IOException e) {
                            throw new IOException(DJFormats.format("Failed to write tracked block sector entry header for block '{0}:{1}'", blockData.id, blockData.data), e);
                        }

                        for (TrackedBlock block : placedBlocks) {
                            String worldName = block.getWorldName();
                            Vector3 location = block.getLocation();
                            try {
                                output.writeShort(worldName.length());  // write the length of the world name (2 bytes)
                                output.writeChars(worldName);           // write the world name string (? bytes)
                                output.writeInt(location.x);            // write X coordinate (4 bytes)
                                output.writeByte(location.y);           // write Y coordinate (1 byte)
                                output.writeInt(location.z);            // write Z coordinate (4 bytes)
                            }
                            catch (IOException e) {
                                throw new IOException(DJFormats.format("Failed to write tracked block entry in {0} at {1} {2} {3}", worldName, location.x, location.y, location.z), e);
                            }
                        }
                    }
                    catch (IOException e) {
                        throw new IOException(DJFormats.format("Failed to write block sector entry for block '{0}:{1}'", blockData.id, blockData.data), e);
                    }
                }
            }

            output.flush();
            output.close();
            data.setDataChanged(false);
            return true;
        }
        else {
            return false;
        }
    }
}
