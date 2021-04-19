package reghzy.blocklimiter.track;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import reghzy.blocklimiter.exceptions.FailedFileCreationException;
import reghzy.blocklimiter.exceptions.IncorrectDataFormatException;
import reghzy.blocklimiter.limit.MetaLimiter;
import reghzy.blocklimiter.track.world.TrackedBlock;
import reghzy.blocklimiter.track.user.PlayerDataLoader;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.track.user.UserBlockData;
import reghzy.blocklimiter.track.user.UserDataManager;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.world.Vector3;
import reghzy.blocklimiter.track.world.WorldTracker;
import reghzy.blocklimiter.utils.Translator;
import reghzy.blocklimiter.utils.debug.Debugger;
import reghzy.blocklimiter.utils.logs.ChatFormat;
import reghzy.blocklimiter.utils.logs.ChatLogger;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

public final class ServerTracker {
    private static ServerTracker instance;

    private final PlayerDataLoader dataLoader;
    private final UserDataManager userDataManager;
    private final HashMap<String, WorldTracker> worlds;

    public ServerTracker() throws OperationNotSupportedException {
        if (ServerTracker.instance == null) {
            ServerTracker.instance = this;
        }
        else {
            throw new OperationNotSupportedException("BlockTrackManager instance already exists!");
        }

        this.userDataManager = new UserDataManager(this);
        this.worlds = new HashMap<String, WorldTracker>(8);
        this.dataLoader = new PlayerDataLoader(this);

        try {
            this.loadPlayerData(PlayerDataLoader.PlayerDataFolder);
        }
        catch (FailedFileCreationException e) {
            ChatLogger.logConsole("Failed to create user data folder");
            e.printStackTrace();
        }
    }

    public void loadPlayerData(File directory) throws FailedFileCreationException {
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            if (!directory.exists()) {
                if (!directory.mkdirs())
                    throw new FailedFileCreationException("Failed to create player data directory");
            }

            return;
        }

        for (File file : files) {
            String name = file.getName();
            String username = name.substring(0, name.lastIndexOf('.'));
            try {
                this.dataLoader.loadPlayer(file, username);
            }
            catch (IncorrectDataFormatException exception) {
                ChatLogger.logPlugin(ChatColor.RED + "Detected corrupted data while loading the player data for user " + ChatFormat.apostrophise(username));
                ChatLogger.logPlugin(exception.getMessage());
            }
            catch (IOException exception) {
                ChatLogger.logPlugin(ChatColor.RED + "IO Exception while loading player data for " + ChatFormat.apostrophise(username));
                exception.printStackTrace();
            }
        }
    }

    public int savePlayerData(File directory) throws FailedFileCreationException, IOException {
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            if (!directory.exists()) {
                if (!directory.mkdirs())
                    throw new FailedFileCreationException("Failed to create player data directory");
            }

            return 0;
        }

        int count = 0;
        for(UserBlockData data : getUserManager().getUsersData()) {
            User user = data.getUser();
            if (this.dataLoader.savePlayer(new File(directory, user.getName() + ".dat"), user)) {
                count++;
            }
        }
        return count;
    }

    public boolean shouldCancelBlockBreak(Player breakerPlayer, Block block, MetaLimiter limiter) {
        User breaker = getUserManager().getUser(breakerPlayer);
        TrackedBlock trackedBlock = getBlockAt(block);
        if (trackedBlock == null) {
            ChatLogger.logConsole("A limited block in " + ChatFormat.apostrophise(breakerPlayer.getWorld().getName()) + " at " + ChatFormat.apostrophise(new Vector3(block).toString()) + " didn't exist, but was still placed. Breaker: " + breaker.getName());
            return false;
        }

        User owner = trackedBlock.getOwner();
        Player ownerPlayer = owner.getBukkitPlayer();

        // same player
        if (breaker.equals(owner)) {
            TrackedBlock brokenBlock = breakBlockAt(block);
            Debugger.log("Broke limited block at " + brokenBlock.getLocation().toString());
            return false;
        }
        else {
            if (limiter.allowOthersToBreakOwnerBlock) {
                TrackedBlock brokenBlock = breakBlockAt(block);
                if (ownerPlayer != null) {
                    if (limiter.otherPlayerBrekeOwnerBlockMsg != null) {
                        ownerPlayer.sendMessage(Translator.translateWildcards(
                                limiter.otherPlayerBrekeOwnerBlockMsg, breakerPlayer, brokenBlock.getLocation()));
                    }
                }
                if (limiter.youBreakOwnerBlockMsg != null) {
                    breakerPlayer.sendMessage(Translator.translateWildcards(
                            limiter.youBreakOwnerBlockMsg, owner.getName(), breakerPlayer.getWorld().getName(), brokenBlock.getLocation()));
                }
                Debugger.log("Broke limited block at " + trackedBlock.getLocation().toString());
                return false;
            }
            else {
                if (ownerPlayer != null) {
                    if (limiter.otherPlayerBreakOwnerBlockAttemptMsg != null) {
                        ownerPlayer.sendMessage(Translator.translateWildcards(
                                limiter.otherPlayerBreakOwnerBlockAttemptMsg, breakerPlayer, trackedBlock.getLocation()));
                    }
                }
                if (limiter.youBreakOwnerBlockAttemptMsg != null) {
                    breakerPlayer.sendMessage(Translator.translateWildcards(
                            limiter.youBreakOwnerBlockAttemptMsg, owner.getName(), breakerPlayer.getWorld().getName(), trackedBlock.getLocation()));
                }
                return true;
            }
        }
    }

    public boolean shouldCancelBlockPlace(Player player, Block block, MetaLimiter limiter) {
        User placer = getUserManager().getUser(player);
        TrackedBlock trackedBlock = getBlockAt(block);
        if (trackedBlock != null) {
            ChatLogger.logConsole("Block in " + ChatFormat.apostrophise(block.getWorld().getName()) +
                                  " at " + ChatFormat.apostrophise(trackedBlock.getLocation().toString()) +
                                  " was already placed, but " + ChatFormat.apostrophise(placer.getName()) + " tried to place a block there");
            ChatLogger.logConsole("Previous Block space owner: " + trackedBlock.getOwner().getName() + ", New placer: " + placer.getName());
            ChatLogger.logConsole("Breaking the block...");
            breakBlockAt(trackedBlock);
        }

        UserBlockData placerData = getUserManager().getBlockData(placer);
        int placed = placerData.getPlacedBlocks(limiter.dataPair);
        if (limiter.canPlayerPlace(player, placed)) {
            placeNewBlockAt(player.getWorld(), placer, limiter.dataPair, new Vector3(block));
            return false;
        }
        else {
            return true;
        }
    }

    public void onPlayerJoin(Player player) {
        getUserManager().loadPlayer(player.getName());
    }

    public void onPlayerLeave(Player player) {
        UserDataManager userManager = getUserManager();
        User left = userManager.getUser(player);
        UserBlockData userData = userManager.getBlockData(left);
        if (userData.shouldUnload()) {
            userManager.unloadUser(left);
        }
    }

    public void onWorldLoad(World world) {
        getOrCreateWorldTracker(world.getName());
    }

    public void onWorldUnload(World world) {

    }

    // --------------------------------- Getting ----------------------------------------- //

    public TrackedBlock getBlockAt(Block block) {
        return getBlockAt(getWorldTracker(block.getWorld()), block.getX(), block.getY(), block.getZ());
    }

    public TrackedBlock getBlockAt(WorldTracker world, Vector3 location) {
        return world.getBlock(location);
    }

    public TrackedBlock getBlockAt(String worldName, Vector3 location) {
        return getBlockAt(worldName, location.x, location.y, location.z);
    }

    public TrackedBlock getBlockAt(String worldName, int x, int y, int z) {
        return getBlockAt(getWorldTracker(worldName), x, y, z);
    }

    public TrackedBlock getBlockAt(World world, Vector3 location) {
        return getBlockAt(getWorldTracker(world), location.x, location.y, location.z);
    }

    public TrackedBlock getBlockAt(World world, int x, int y, int z) {
        return getBlockAt(getWorldTracker(world), x, y, z);
    }

    public TrackedBlock getBlockAt(WorldTracker world, int x, int y, int z) {
        return world.getBlock(x, y, z);
    }

    // ----------------------------------------------------------------------------------- //

    // --------------------------------- Placing ----------------------------------------- //

    // New blocks
    public TrackedBlock placeNewBlockAt(WorldTracker world, User owner, String worldName, BlockDataPair blockData, Vector3 location) {
        return placeBlockAt(world, TrackedBlock.createBlock(owner, worldName, blockData, location));
    }

    public TrackedBlock placeNewBlockAt(WorldTracker world, User owner, BlockDataPair blockData, Vector3 location) {
        return placeBlockAt(world, TrackedBlock.createBlock(owner, world.getWorldName(), blockData, location));
    }

    public TrackedBlock placeNewBlockAt(String worldName, User owner, BlockDataPair blockData, Vector3 location) {
        return placeBlockAt(worldName, TrackedBlock.createBlock(owner, worldName, blockData, location));
    }

    public TrackedBlock placeNewBlockAt(World world, User owner, BlockDataPair blockData, Vector3 location) {
        return placeBlockAt(world, TrackedBlock.createBlock(owner, world.getName(), blockData, location));
    }

    // Existing blocks
    public TrackedBlock placeBlockAt(World world, TrackedBlock block, Vector3 location) {
        return placeBlockAt(getWorldTracker(world), block, location);
    }

    public TrackedBlock placeBlockAt(World world, TrackedBlock block, int x, int y, int z) {
        return placeBlockAt(getWorldTracker(world), block, x, y, z);
    }

    public TrackedBlock placeBlockAt(String worldName, TrackedBlock block, int x, int y, int z) {
        return placeBlockAt(getWorldTracker(worldName), block, x, y, z);
    }

    public TrackedBlock placeBlockAt(String worldName, TrackedBlock block, Vector3 location) {
        return placeBlockAt(getWorldTracker(worldName), block, location);
    }

    public TrackedBlock placeBlockAt(WorldTracker world, TrackedBlock block, Vector3 location) {
        return placeBlockAt(world, block, location.x, location.y, location.z);
    }

    public TrackedBlock placeBlockAt(WorldTracker world, TrackedBlock block, int x, int y, int z) {
        return world.placeBlock(block, x, y, z);
    }

    public TrackedBlock placeBlockAt(World world, TrackedBlock block) {
        return placeBlockAt(getWorldTracker(world), block);
    }

    public TrackedBlock placeBlockAt(String worldName, TrackedBlock block) {
        return placeBlockAt(getWorldTracker(worldName), block);
    }

    public TrackedBlock placeBlockAt(WorldTracker world, TrackedBlock block) {
        return world.placeBlock(block);
    }

    // ----------------------------------------------------------------------------------- //

    // --------------------------------- Breaking ---------------------------------------- //

    public TrackedBlock breakBlockAt(TrackedBlock block) {
        return breakBlockAt(block.getWorldName(), block.getLocation());
    }

    public TrackedBlock breakBlockAt(Block block) {
        return breakBlockAt(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public TrackedBlock breakBlockAt(World world, Vector3 location) {
        return breakBlockAt(getWorldTracker(world), location);
    }

    public TrackedBlock breakBlockAt(World world, int x, int y, int z) {
        return breakBlockAt(getWorldTracker(world), x, y, z);
    }

    public TrackedBlock breakBlockAt(String worldName, int x, int y, int z) {
        return breakBlockAt(getWorldTracker(worldName), x, y, z);
    }

    public TrackedBlock breakBlockAt(String worldName, Vector3 location) {
        return breakBlockAt(getWorldTracker(worldName), location);
    }

    public TrackedBlock breakBlockAt(WorldTracker world, Vector3 location) {
        return breakBlockAt(world, location.x, location.y, location.z);
    }

    public TrackedBlock breakBlockAt(WorldTracker world, int x, int y, int z) {
        return world.breakBlock(x, y, z);
    }

    // ----------------------------------------------------------------------------------- //

    public WorldTracker getWorldTracker(String name) {
        return getOrCreateWorldTracker(name);
    }

    public WorldTracker getWorldTracker(World world) {
        return getWorldTracker(world.getName());
    }

    public Collection<WorldTracker> getWorldTrackers() {
        return this.worlds.values();
    }

    public UserDataManager getUserManager() {
        return this.userDataManager;
    }

    public static ServerTracker getInstance() {
        return ServerTracker.instance;
    }

    private WorldTracker getOrCreateWorldTracker(String name) {
        WorldTracker worldTracker = worlds.get(name);
        if (worldTracker == null) {
            worldTracker = new WorldTracker(this, name);
            worlds.put(name, worldTracker);
        }
        return worldTracker;
    }
}
