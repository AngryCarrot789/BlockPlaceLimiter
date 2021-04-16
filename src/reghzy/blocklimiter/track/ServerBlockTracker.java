package reghzy.blocklimiter.track;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import reghzy.blocklimiter.exceptions.BlockAlreadyBrokenException;
import reghzy.blocklimiter.exceptions.BlockAlreadyPlacedException;
import reghzy.blocklimiter.exceptions.FailedFileCreationException;
import reghzy.blocklimiter.exceptions.IncorrectDataFormatException;
import reghzy.blocklimiter.limit.MetaLimiter;
import reghzy.blocklimiter.track.block.TrackedBlock;
import reghzy.blocklimiter.track.user.PlayerDataLoader;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.track.user.UserBlockData;
import reghzy.blocklimiter.track.user.UserDataManager;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.world.Vector3;
import reghzy.blocklimiter.track.world.WorldBlockTracker;
import reghzy.blocklimiter.utils.Translator;
import reghzy.blocklimiter.utils.debug.Debugger;
import reghzy.blocklimiter.utils.logs.ChatFormat;
import reghzy.blocklimiter.utils.logs.ChatLogger;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.util.Collection;
import java.util.HashMap;

public final class ServerBlockTracker {
    private static ServerBlockTracker instance;

    private final PlayerDataLoader dataLoader;
    private final UserDataManager userDataManager;
    private final HashMap<String, WorldBlockTracker> worlds;

    public ServerBlockTracker() throws OperationNotSupportedException {
        if (ServerBlockTracker.instance == null) {
            ServerBlockTracker.instance = this;
        }
        else {
            throw new OperationNotSupportedException("BlockTrackManager instance already exists!");
        }

        this.userDataManager = new UserDataManager(this);
        this.worlds = new HashMap<String, WorldBlockTracker>(8);
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
            catch (IOException exception) {
                ChatLogger.logPlugin(ChatColor.RED + "IO Exception while loading player data for " + ChatFormat.apostrophise(username));
                exception.printStackTrace();
            }
            catch (IncorrectDataFormatException exception) {
                ChatLogger.logPlugin(ChatColor.RED + "Detected corrupted data while loading player data for " + ChatFormat.apostrophise(username));
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

    public boolean shouldCancelBlockBreak(Player breakerPlayer, Block block, MetaLimiter limiter, boolean force) throws BlockAlreadyBrokenException {
        User breaker = getUserManager().getUser(breakerPlayer);
        TrackedBlock trackedBlock = getBlockAt(block);
        if (trackedBlock == null) {
            if (force) {
                ChatLogger.logConsole("A limited block at " + new Vector3(block).toString() + " didn't exist, but was still placed. Breaker: " + breaker.getName());
                return false;
            }
            else {
                throw new BlockAlreadyBrokenException(new Vector3(block), breaker);
            }
        }

        User owner = trackedBlock.getOwner();
        Player ownerPlayer = owner.getBukkitPlayer();
        UserBlockData ownerData = getUserManager().getBlockData(owner);
        UserBlockData breakData = getUserManager().getBlockData(breaker);

        // same player
        if (breaker.equals(owner)) {
            if (breakData.removeBlock(trackedBlock)) {
                WorldBlockTracker tracker = getWorldTracker(trackedBlock.getWorldName());
                if (force) {
                    tracker.forceBreakBlock(trackedBlock);
                }
                else {
                    tracker.breakBlock(trackedBlock);
                }
                Debugger.log("Broke limited block at " + trackedBlock.getLocation().toString());
                return false;
            }
            Debugger.log("1 Failed to break block at " + trackedBlock.getLocation().toString());
        }
        else {
            if (limiter.allowOthersToBreakOwnerBlock) {
                if (ownerData.removeBlock(trackedBlock)) {
                    breakBlockAt(getWorldTracker(trackedBlock.getWorldName()), trackedBlock.getLocation());
                    if (ownerPlayer != null) {
                        ownerPlayer.sendMessage(Translator.translateWildcards(limiter.otherPlayerBrokeOwnerBlockMsg, breakerPlayer));
                    }
                    Debugger.log("Broke limited block at " + trackedBlock.getLocation().toString());
                    return false;
                }
                Debugger.log("2 Failed to break block at " + trackedBlock.getLocation().toString());
            }
            else {
                ownerPlayer.sendMessage(Translator.translateWildcards(limiter.otherPlayerBreakOwnerBlockAttemptMsg, breakerPlayer));
                return true;
            }
        }

        return false;
    }

    public boolean shouldCancelBlockPlace(Player player, Block block, MetaLimiter limiter, boolean force) throws BlockAlreadyPlacedException {
        User placer = getUserManager().getUser(player);
        TrackedBlock trackedBlock = getBlockAt(block);
        if (trackedBlock != null) {
            if (force) {
                ChatLogger.logConsole("Block at " + trackedBlock.getLocation().toString() + " was already placed, but someone tried to place a block there. Placer: " + placer.getName());
            }
            else {
                throw new BlockAlreadyPlacedException(new Vector3(block), placer);
            }
        }

        UserBlockData placerData = getUserManager().getBlockData(placer);
        int placed = placerData.getPlacedBlocks(limiter.dataPair);
        // same player
        if (limiter.canPlayerPlace(player, placed)) {
            WorldBlockTracker tracker = getWorldTracker(player.getWorld());
            if (force) {
                tracker.forcePlaceBlock(new TrackedBlock(placer, tracker.getWorldName(), limiter.dataPair, new Vector3(block)));
            }
            else {
                tracker.placeBlock(new TrackedBlock(placer, tracker.getWorldName(), limiter.dataPair, new Vector3(block)));
            }
            return false;
        }
        else {
            if (placed == 0) {
                player.sendMessage(Translator.translateWildcards(limiter.noInitialPermsMsg, player));
            }
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

    public TrackedBlock getBlockAt(String worldName, Vector3 location) {
        return getWorldTracker(worldName).getBlock(location);
    }

    public TrackedBlock getBlockAt(World worldName, Vector3 location) {
        return getWorldTracker(worldName).getBlock(location);
    }

    public TrackedBlock getBlockAt(Block block) {
        return getWorldTracker(block.getWorld()).getBlock(block.getX(), block.getY(), block.getZ());
    }

    public TrackedBlock placeBlockAt(WorldBlockTracker worldBlockTracker, User user, BlockDataPair blockData, Vector3 location) throws BlockAlreadyPlacedException {
        return worldBlockTracker.placeBlock(new TrackedBlock(user, worldBlockTracker.getWorldName(), blockData, location));
    }

    public TrackedBlock breakBlockAt(WorldBlockTracker worldBlockTracker, Vector3 location) throws BlockAlreadyBrokenException {
        return worldBlockTracker.breakBlock(location);
    }

    public WorldBlockTracker getWorldTracker(String name) {
        return getOrCreateWorldTracker(name);
    }

    public WorldBlockTracker getWorldTracker(World world) {
        return getWorldTracker(world.getName());
    }

    public Collection<WorldBlockTracker> getWorldTrackers() {
        return this.worlds.values();
    }

    private WorldBlockTracker getOrCreateWorldTracker(String name) {
        WorldBlockTracker worldBlockTracker = worlds.get(name);
        if (worldBlockTracker == null) {
            worldBlockTracker = new WorldBlockTracker(this, name);
            worlds.put(name, worldBlockTracker);
        }
        return worldBlockTracker;
    }

    public UserDataManager getUserManager() {
        return this.userDataManager;
    }

    public static ServerBlockTracker getInstance() {
        return ServerBlockTracker.instance;
    }
}
