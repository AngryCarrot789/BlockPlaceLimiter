package reghzy.blocklimiter.track;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import reghzy.api.commands.utils.RZLogger;
import reghzy.api.utils.ExceptionHelper;
import reghzy.api.utils.ItemHelper;
import reghzy.blocklimiter.BlockPlaceLimiterPlugin;
import reghzy.blocklimiter.exceptions.FailedFileCreationException;
import reghzy.blocklimiter.exceptions.IncorrectDataFormatException;
import reghzy.blocklimiter.limit.MetaLimiter;
import reghzy.blocklimiter.track.user.User;
import reghzy.blocklimiter.track.user.UserBlockData;
import reghzy.blocklimiter.track.user.UserDataManager;
import reghzy.blocklimiter.track.user.data.PlayerDataLoader;
import reghzy.blocklimiter.track.user.data.StringBasedPlayerDataLoader;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.world.TrackedBlock;
import reghzy.blocklimiter.track.world.Vector3;
import reghzy.blocklimiter.track.world.WorldTracker;
import reghzy.blocklimiter.utils.Translator;
import reghzy.carrottools.playerdata.PlayerRegister;
import ru.tehkode.permissions.bukkit.PermissionsEx;

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

    public static final RZLogger LOGGER = BlockPlaceLimiterPlugin.LOGGER;
    
    public ServerTracker() throws OperationNotSupportedException {
        if (ServerTracker.instance == null) {
            ServerTracker.instance = this;
        }
        else {
            throw new OperationNotSupportedException("ServerTracker instance already exists!");
        }

        this.userDataManager = new UserDataManager(this);
        this.worlds = new HashMap<String, WorldTracker>(8);
        this.dataLoader = new StringBasedPlayerDataLoader(this);

        try {
            this.loadPlayerData(PlayerDataLoader.PLAYER_DATA_FOLDER);
        }
        catch (FailedFileCreationException e) {
            LOGGER.logFormat("Failed to create user data folder");
            ExceptionHelper.printException(e);
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
            String fileName = file.getName();
            String username = fileName.substring(0, fileName.lastIndexOf('.'));
            try {
                this.dataLoader.loadPlayer(file, username);
            }
            catch (IncorrectDataFormatException exception) {
                LOGGER.logFormatConsole("&4Detected corrupted data while loading the player data for user '&c{0}&4'", username);
                LOGGER.logTranslateConsole(exception.getMessage());
            }
            catch (IOException exception) {
                LOGGER.logFormatConsole("&4IOException while loading the player data for user '&c{0}&4'", username);
                ExceptionHelper.printException(exception, LOGGER, true);
            }
        }
    }

    public int savePlayerData(File directory, boolean forceIfUnchanged) throws FailedFileCreationException, IOException {
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
            if (this.dataLoader.savePlayer(new File(directory, user.getName() + ".dat"), user, forceIfUnchanged)) {
                count++;
            }
        }
        return count;
    }

    public boolean shouldCancelBlockBreak(Player breakerPlayer, Block block, MetaLimiter limiter) {
        User breaker = getUserManager().getUser(breakerPlayer);
        TrackedBlock trackedBlock = getBlockAt(block);
        if (trackedBlock == null) {
            LOGGER.logFormatConsole("A limited block (&a{0}&6:&2{1}&6, aka &e{5}) in '{2}' at '{3}' didn't exist, but was still placed. Breaker: {4}",
                                    block.getTypeId(), block.getData(),
                                    breakerPlayer.getWorld().getName(),
                                    new Vector3(block).toString(),
                                    breaker.getName(),
                                    ItemHelper.getItemName(block.getTypeId(), 0));
            for(Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp()) {
                    if (PlayerRegister.getData(player).meta.getOrCreateBoolRef("msg-limitedbroken-nonexistant", true).getValue()) {

                    }
                }
            }

            return false;
        }

        User owner = trackedBlock.getOwner();
        // same player
        if (breaker.equals(owner)) {
            breakBlockAt(block);
            return false;
        }
        else {
            Player ownerPlayer = owner.getBukkitPlayer();
            boolean canPlayerBreak = limiter.allowOthersToBreakOwnerBlock;
            if (!canPlayerBreak) {
                if (PermissionsEx.getPermissionManager().has(breakerPlayer, limiter.bypassBreakPermission)) {
                    canPlayerBreak = true;
                }
            }

            if (canPlayerBreak) {
                TrackedBlock brokenBlock = breakBlockAt(block);
                if (limiter.otherPlayerBreakOwnerBlockMsg != null && ownerPlayer != null && ownerPlayer.isOnline()) {
                    ownerPlayer.sendMessage(Translator.translateWildcards(limiter.otherPlayerBreakOwnerBlockMsg, breakerPlayer, brokenBlock.getLocation()));
                }
                if (limiter.youBreakOwnerBlockMsg != null) {
                    breakerPlayer.sendMessage(Translator.translateWildcards(limiter.youBreakOwnerBlockMsg, owner.getName(), breakerPlayer.getWorld().getName(), brokenBlock.getLocation()));
                }

                return false;
            }
            else {
                if (limiter.otherPlayerBreakOwnerBlockAttemptMsg != null && ownerPlayer != null && ownerPlayer.isOnline()) {
                    ownerPlayer.sendMessage(Translator.translateWildcards(limiter.otherPlayerBreakOwnerBlockAttemptMsg, breakerPlayer, trackedBlock.getLocation()));
                }
                if (limiter.youBreakOwnerBlockAttemptMsg != null) {
                    breakerPlayer.sendMessage(Translator.translateWildcards(limiter.youBreakOwnerBlockAttemptMsg, owner.getName(), breakerPlayer.getWorld().getName(), trackedBlock.getLocation()));
                }

                return true;
            }
        }
    }

    public boolean shouldCancelBlockPlace(Player placerPlayer, Block block, MetaLimiter limiter) {
        User placer = getUserManager().getUser(placerPlayer);
        TrackedBlock trackedBlock = getBlockAt(block);
        if (trackedBlock != null) {
            LOGGER.logFormatConsole("&6Block (&e{0}&6, aka &3{4}&6) in '{1}' at '{2}' was already placed, but {3} tried to place a block there",
                                    trackedBlock.getBlockData().toString(), block.getWorld().getName(),
                                    trackedBlock.getLocation().toString(), placer.getName(),
                                    ItemHelper.getItemName(trackedBlock.getBlockData().id, trackedBlock.getBlockData().data));
            LOGGER.logFormatConsole("Possible Cause: The block can destroy itself (e.g TNT), or block was broken (WorldEdited, Block Breaker, etc) and they quickly replaced the block (too fast for the synchroniser to detect)");
            LOGGER.logFormatConsole("Previous Block space owner was '{0}', New placer is '{1}'", trackedBlock.getOwner().getName(), placer.getName());
            LOGGER.logFormatConsole("Removing the block that was previously there (owned by {0})", trackedBlock.getOwner().getName());
            breakBlockAt(trackedBlock);
        }

        UserBlockData placerData = getUserManager().getBlockData(placer);
        if (limiter.canPlayerPlace(placerPlayer, placerData.getPlacedBlocks(limiter.dataPair))) {
            placeNewBlockAt(placerPlayer.getWorld(), placer, limiter.dataPair, new Vector3(block));
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
        User user = userManager.getUser(player);
        if (userManager.getBlockData(user).shouldUnload()) {
            userManager.unloadUser(user);
        }
    }

    public void onWorldLoad(World world) {
        getOrCreateWorldTracker(world.getName());
    }

    public void onWorldUnload(World world) {

    }

    // useful API stuff below
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
