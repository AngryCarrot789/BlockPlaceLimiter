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

    public void savePlayerData(File directory) throws FailedFileCreationException, IOException {
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            if (!directory.exists()) {
                if (!directory.mkdirs())
                    throw new FailedFileCreationException("Failed to create player data directory");
            }

            return;
        }

        for(UserBlockData data : getUserManager().getUsersData()) {
            User user = data.getUser();
            this.dataLoader.savePlayer(new File(directory, user.getName() + ".dat"), user);
        }
    }

    public boolean shouldCancelBlockBreak(Player breakerPlayer, Block block, MetaLimiter limiter) throws BlockAlreadyBrokenException {
        User breaker = getUserManager().getUser(breakerPlayer);
        TrackedBlock trackedBlock = getBlockAt(block);
        if (trackedBlock == null) {
            throw new BlockAlreadyBrokenException(new Vector3(block), breaker);
        }

        User owner = trackedBlock.getOwner();
        Player ownerPlayer = owner.getBukkitPlayer();
        UserBlockData ownerData = getUserManager().getBlockData(owner);
        UserBlockData breakData = getUserManager().getBlockData(breaker);

        // same player
        if (breaker.equals(owner)) {
            if (breakData.removeBlock(trackedBlock)) {
                breakBlockAt(getWorldTracker(trackedBlock.getWorldName()), trackedBlock.getLocation());
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

    public boolean shouldCancelBlockPlace(Player player, Block block, MetaLimiter limiter) throws BlockAlreadyPlacedException {
        User placer = getUserManager().getUser(player);
        TrackedBlock trackedBlock = getBlockAt(block);
        if (trackedBlock != null) {
            throw new BlockAlreadyPlacedException(new Vector3(block), placer);
        }

        UserBlockData placerData = getUserManager().getBlockData(placer);
        int placed = placerData.getPlacedBlocks(limiter.dataPair);
        // same player
        if (limiter.canPlayerPlace(player, placed)) {
            placerData.addBlock(placeBlockAt(getWorldTracker(player.getWorld()), placer, limiter.dataPair, new Vector3(block)));
            return false;
        }
        else {
            if (placed == 0) {
                player.sendMessage(Translator.translateWildcards(limiter.noInitialPermsMsg, player));
            }
            else {
                player.sendMessage(Translator.translateWildcards("You cant place this", player));
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

    public TrackedBlock placeBlockAt(WorldBlockTracker worldBlockTracker, TrackedBlock block) {
        worldBlockTracker.placeBlock(block);
        return block;
    }

    public TrackedBlock placeBlockAt(WorldBlockTracker worldBlockTracker, User user, BlockDataPair blockData, Vector3 location) {
        return placeBlockAt(worldBlockTracker, new TrackedBlock(user, worldBlockTracker.getWorldName(), blockData, location));
    }

    public TrackedBlock breakBlockAt(WorldBlockTracker worldBlockTracker, Vector3 location) {
        return worldBlockTracker.breakBlock(location);
    }

    public WorldBlockTracker getWorldTracker(String name) {
        return getOrCreateWorldTracker(name);
    }

    protected WorldBlockTracker getWorldTracker(World world) {
        return getWorldTracker(world.getName());
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
