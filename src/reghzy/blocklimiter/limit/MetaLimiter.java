package reghzy.blocklimiter.limit;

import gnu.trove.set.hash.TIntHashSet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.utils.IntegerRange;
import reghzy.blocklimiter.track.utils.RangeLimit;
import reghzy.blocklimiter.utils.Translator;
import reghzy.blocklimiter.utils.debug.Debugger;
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
    private final HashMap<Integer, IntegerRange> limitRangeCache;

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
        this.limitRangeCache = this.rangeLimits.generateCache();
    }

    /**
     * Checks if the player has permission to place this limit block, by checking if
     * their currentlyPlaced value falls within a range limit they have permission for
     * @param currentlyPlaced The number of this limited block the player has placed (the exact number)
     */
    public boolean canPlayerPlace(Player player, int currentlyPlaced) {
        IntegerRange currentRange = getLimitRange(currentlyPlaced);
        if (currentRange == null) {
            Debugger.log("Severe error: player had placed an amount of ID " + this.dataPair.id + " that has not been correctly processed (cached ranges didnt contain the player's placed amount, when they should have)");
            player.sendMessage(ChatColor.GOLD + "Internal server error: contact the server admins about 'BlockPlacementLimiter:Range'");
            return false;
        }

        RangeLimit pair = this.rangeLimits.getPermission(currentRange);
        if (pair == null) {
            Debugger.log("Fatal error: The range for ID " + this.dataPair.id + " didn't contain link to its permission (range " + currentRange.toString() + ")");
            player.sendMessage(ChatColor.GOLD + "Internal server error: contact the server admins about 'BlockPlacementLimiter:Perms'");
            return false;
        }

        if (hasPermission(player, pair.permission)) {
            if (currentRange.between(currentlyPlaced + 1)) {
                Debugger.log(player.getName() + " placed a limited block! Total placed: " + currentlyPlaced);
                return true;
            }

            Debugger.log(player.getName() + " Failed to place a block. Total placed: " + currentlyPlaced);
            player.sendMessage(Translator.translateWildcards(pair.limitHitMessage, player));
            return false;
        }

        return false;
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
    public boolean hasPermission(Player player, String permission) {
        if (permission == null)
            return false;

        return PermissionsHelper.hasPermission(player, permission);
    }

    private IntegerRange getLimitRange(int value) {
        return limitRangeCache.get(value);
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
