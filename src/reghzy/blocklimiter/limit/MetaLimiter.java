package reghzy.blocklimiter.limit;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.utils.IntegerRange;
import reghzy.blocklimiter.track.utils.RangeLimit;
import reghzy.blocklimiter.utils.Translator;
import reghzy.blocklimiter.utils.debug.Debugger;
import reghzy.blocklimiter.utils.logs.ChatLogger;
import reghzy.blocklimiter.utils.permissions.PermissionsHelper;

import java.util.HashMap;

/**
 * Contains information about a limited block (with a specific ID and Metadata)
 * <p>
 *     such as the permissions required to place a certain number of the block,
 *     the message to send if they go over that limit, bypass permissions, etc
 * </p>
 */
public class MetaLimiter {
    public final BlockDataPair dataPair;
    public final String bypassPermission;
    public final String noInitialPermsMsg;

    public final boolean allowOthersToBreakOwnerBlock;
    public final String otherPlayerBrekeOwnerBlockMsg;
    public final String otherPlayerBreakOwnerBlockAttemptMsg;
    public final String youBreakOwnerBlockMsg;
    public final String youBreakOwnerBlockAttemptMsg;

    private final RangeLimits rangeLimits;
    // used for getting a reference to a range by indexing to the numbers in between
    private final HashMap<Integer, IntegerRange> rangeLimitCache;

    public MetaLimiter(int id, int metadata,
                       String bypassPermission,
                       String noInitialPermsMsg,
                       boolean allowOthersToBreakOwnerBlock,
                       String otherPlayerBrekeOwnerBlockMsg,
                       String otherPlayerBreakOwnerBlockAttemptMsg,
                       String youBreakOwnerBlockMsg,
                       String youBreakOwnerBlockAttemptMsg,
                       RangeLimits rangeLimits) {
        this.allowOthersToBreakOwnerBlock = allowOthersToBreakOwnerBlock;
        this.otherPlayerBrekeOwnerBlockMsg = otherPlayerBrekeOwnerBlockMsg;
        this.otherPlayerBreakOwnerBlockAttemptMsg = otherPlayerBreakOwnerBlockAttemptMsg;
        this.youBreakOwnerBlockMsg = youBreakOwnerBlockMsg;
        this.youBreakOwnerBlockAttemptMsg = youBreakOwnerBlockAttemptMsg;
        this.dataPair = new BlockDataPair(id, metadata);
        this.bypassPermission = bypassPermission;
        this.noInitialPermsMsg = noInitialPermsMsg;
        this.rangeLimits = rangeLimits;
        this.rangeLimitCache = this.rangeLimits.generateCache();
    }

    /**
     * Emulates trying to place this limiter (aka trying to place currentlyPlaced + 1)
     * <p>
     *     Checks if the player has permission to place this limit block, by checking if
     *     their currentlyPlaced value falls within a range limit they have permission for
     * </p>
     * @param currentlyPlaced The number of this limited block the player has placed (the exact number)
     */
    public boolean canPlayerPlace(Player player, int currentlyPlaced) {
        // assume this metalimiter is for stone from 0 to 5 for example
        // because this is emulating if they can place the extra block... add 1 extra, making them have placed 6
        IntegerRange nextRange = getRangeLimit(currentlyPlaced + 1);
        // if there's only 1 limit (e.g. 0 to 5 for this limiter), this will be null
        if (nextRange == null) {
            // get the current range they've placed which should NOT be null (5)
            IntegerRange currentRange = getRangeLimit(currentlyPlaced);
            RangeLimit currentLimit = this.rangeLimits.getRangeLimit(currentRange);
            // if it is null... something horribly wrong has happened. someone's probably
            // badly edited the limits config and broke it during runtime of the plugin o_o
            if (currentRange == null || currentLimit == null) {
                ChatLogger.logConsole("Severe error: player had placed an amount of ID " + this.dataPair.id + " that has not been correctly processed (cached ranges didnt contain the player's placed amount, when they should have)");
                player.sendMessage(ChatColor.GOLD + "Internal server error: contact the server operators about 'BlockPlaceLimiter:Range'");
                return false;
            }

            // there's no error, and assuming they've already placed 5 stone
            // but they cant place 6, they must obviously have permission for 5
            // therefore... they cant place anymore than 5 so return false to "canPlayerPlace" (and send them a message)
            if (currentlyPlaced == 0 && noInitialPermsMsg != null) {
                player.sendMessage(Translator.translateWildcards(noInitialPermsMsg, player, null));
            }
            else if (currentLimit.limitHitMessage != null) {
                player.sendMessage(Translator.translateWildcards(currentLimit.limitHitMessage, player, null));
            }
            return false;
        }

        // however... if theres more than 1 limit (e.g. 0 to 5 stone, and 6 to 10)...
        // get the range limit from the 'possible next range' the user is trying to place (so 6 to 10)
        RangeLimit nextLimit = this.rangeLimits.getRangeLimit(nextRange);
        // this should not be null at all; another fatal operator error badly configuring the limits config :)
        if (nextLimit == null) {
            ChatLogger.logConsole("Fatal error: The range for ID " + this.dataPair.id + " didn't contain link to its permission (range " + nextRange.toString() + ")");
            player.sendMessage(ChatColor.GOLD + "Internal server error: contact the server operators about 'BlockPlaceLimiter:Perms'");
            return false;
        }

        // do they have permission to place an extra block (6 of them)?
        if (hasPermission(player, nextLimit.permission)) {
            // they can :) could send them a message saying "yes! you can place this block" but nah
            Debugger.log(player.getName() + " Placed a block! Total placed: " + currentlyPlaced + 1);
            return true;
        }
        else {
            // get the current range they've placed which should NOT be null (5)
            IntegerRange currentRange = getRangeLimit(currentlyPlaced);
            RangeLimit currentLimit = this.rangeLimits.getRangeLimit(currentRange);
            // if it is null... something horribly wrong has happened. someone's probably
            // badly edited the limits config and broke it during runtime of the plugin o_o
            if (currentRange == null || currentLimit == null) {
                ChatLogger.logConsole("Severe error: player had placed an amount of ID " + this.dataPair.id + " that has not been correctly processed (cached ranges didnt contain the player's placed amount, when they should have)");
                player.sendMessage(ChatColor.GOLD + "Internal server error: contact the server operators about 'BlockPlaceLimiter:Range'");
                return false;
            }

            // there's no error, and assuming they've already placed 5 stone
            // but they cant place 6, they must obviously have permission for 5
            // therefore... they cant place anymore than 5 so return false to "canPlayerPlace" (and send them a message)
            if (currentlyPlaced == 0 && noInitialPermsMsg != null) {
                player.sendMessage(Translator.translateWildcards(noInitialPermsMsg, player, null));
            }
            else if (currentLimit.limitHitMessage != null) {
                player.sendMessage(Translator.translateWildcards(currentLimit.limitHitMessage, player, null));
            }
            return false;
        }
    }

    public RangeLimits getRangeLimits() {
        return this.rangeLimits;
    }

    /**
     * Checks if the player has this limited block's bypass permission (normally meaning they can place it without consequence)
     * @param player
     * @return
     */
    public boolean playerBypassesChecks(Player player) {
        return PermissionsHelper.hasPermission(player, bypassPermission);
    }

    /**
     * Checks if the player has the given permission. takes into account if the permissions are inverted
     */
    public static boolean hasPermission(Player player, String permission) {
        if (permission == null)
            return false;

        return PermissionsHelper.hasPermission(player, permission);
    }

    private IntegerRange getRangeLimit(int value) {
        return rangeLimitCache.get(value);
    }

    @Override
    public int hashCode() {
        // i mean theres 11 bits left (1 << 20) on the end...
        // thats a value around 2000... could possibly stuff
        // the maximum amount of blocks allowed to be placed in....
        // eh
        return this.dataPair.hashCode();
    }
}
