package reghzy.blocklimiter.limit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import reghzy.api.commands.utils.RZLogger;
import reghzy.blocklimiter.BlockPlaceLimiterPlugin;
import reghzy.blocklimiter.track.utils.BlockDataPair;
import reghzy.blocklimiter.track.utils.RangeLimit;
import reghzy.blocklimiter.utils.Translator;
import ru.tehkode.permissions.bukkit.PermissionsEx;

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
    public final String bypassBreakPermission;
    public final boolean allowOthersToBreakOwnerBlock;
    public final String otherPlayerBreakOwnerBlockMsg;
    public final String otherPlayerBreakOwnerBlockAttemptMsg;
    public final String youBreakOwnerBlockMsg;
    public final String youBreakOwnerBlockAttemptMsg;
    private final RangeLimits rangeLimits;
    // used for getting a reference to a range by indexing to the numbers in between
    // private final HashMap<Integer, IntegerRange> rangeLimitCache;

    public static final RZLogger LOGGER = BlockPlaceLimiterPlugin.LOGGER;

    public MetaLimiter(int id, int metadata,
                       String bypassPermission,
                       String noInitialPermsMsg,
                       String bypassBreakPermission,
                       boolean allowOthersToBreakOwnerBlock,
                       String otherPlayerBreakOwnerBlockMsg,
                       String otherPlayerBreakOwnerBlockAttemptMsg,
                       String youBreakOwnerBlockMsg,
                       String youBreakOwnerBlockAttemptMsg,
                       RangeLimits rangeLimits) {
        this.bypassBreakPermission = bypassBreakPermission;
        this.allowOthersToBreakOwnerBlock = allowOthersToBreakOwnerBlock;
        this.otherPlayerBreakOwnerBlockMsg = otherPlayerBreakOwnerBlockMsg;
        this.otherPlayerBreakOwnerBlockAttemptMsg = otherPlayerBreakOwnerBlockAttemptMsg;
        this.youBreakOwnerBlockMsg = youBreakOwnerBlockMsg;
        this.youBreakOwnerBlockAttemptMsg = youBreakOwnerBlockAttemptMsg;
        this.dataPair = new BlockDataPair(id, metadata);
        this.bypassPermission = bypassPermission;
        this.noInitialPermsMsg = noInitialPermsMsg;
        this.rangeLimits = rangeLimits;
        // this.rangeLimitCache = this.rangeLimits.generateCache();
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
        // get the current range they've placed which should NOT be null (5)
        RangeLimit currentLimit = this.rangeLimits.getRangeLimit(currentlyPlaced);

        // if it is null... something horribly wrong has happened. someone's probably
        // badly edited the limits config and broke it during runtime of the plugin o_o
        if (currentLimit == null) { // currentRange == null) {
            LOGGER.logFormatConsole("&6Severe error: Player (&3{0}&6) had placed an amount of the block '{1}' (they have {2} placed) " +
                                    "that has not been correctly processed (cached ranges didn't contain the player's placed amount, when they should have). " +
                                    "BPL:Range_1CURRANNULL",
                                    player.getName(), this.dataPair.toString(), currentlyPlaced);
            LOGGER.logFormatConsole("This is likely due to the limits config being edited (aka by reducing them amount limited blocks players can place)");
            player.sendMessage(ChatColor.GOLD + "Internal server error: contact the server operators about 'BPL:Range_1CURRANNULL'");
            return false;
        }

        // because this is emulating if they can place the extra block... add 1 extra, making them have placed 6
        RangeLimit nextLimit = this.rangeLimits.getRangeLimit(currentlyPlaced + 1);

        // if there's only 1 limit (e.g. 0 to 5 for this limiter), this will be null
        if (nextLimit == null) { //if (nextRange == null) {
            // they've already placed 5 stone, but they cant place 6, they must obviously have permission for 5
            // therefore... they cant place anymore than 5 so return false to "canPlayerPlace" (and send them a message)
            // if they've placed none at this point... it means there is no limit for this block.... probably... very dodgy
            if (currentlyPlaced == 0) {
                if (noInitialPermsMsg != null) {
                    player.sendMessage(Translator.translateWildcards(noInitialPermsMsg, player, null));
                }
            }
            else if (currentLimit.limitHitMessage != null) {
                player.sendMessage(Translator.translateWildcards(currentLimit.limitHitMessage, player, null));
            }

            return false;
        }
        // however... if theres more than 1 limit (e.g. 0 to 5 stone, and 6 to 10)...
        // do they have permission to place an extra block (6 of them)?
        else if (hasPermission(player, nextLimit.permission)) {
            // they can :) could send them a message saying "yes! you can place this block" but nah
            // Debugger.log(player.getName() + " Placed a block! Total placed: " + currentlyPlaced + 1);
            // This will usually be the case for most limited block placements (because the limit is usually bigger than 1 or 2)
            return true;
        }
        else {
            // there's no error, and assuming they've already placed 5 stone
            // but they cant place 6, they must obviously have permission for 5
            // therefore... they cant place anymore than 5 so return false to "canPlayerPlace" (and send them a message)
            // if they've placed none at this point... it means they dont have perms to place this block yet, they might need to rank up!
            if (currentlyPlaced == 0) {
                if (noInitialPermsMsg != null) {
                    player.sendMessage(Translator.translateWildcards(noInitialPermsMsg, player, null));
                }
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
     */
    public boolean playerBypassesChecks(Player player) {
        return PermissionsEx.getPermissionManager().has(player, bypassPermission);
    }

    /**
     * Checks if the player has the given permission. takes into account if the permissions are inverted
     */
    public static boolean hasPermission(Player player, String permission) {
        if (permission == null)
            return false;

        return PermissionsEx.getPermissionManager().has(player, permission);
    }

    // @Nullable
    // private IntegerRange getCachedRangeLimit(int value) {
    //     return this.rangeLimits.getCachedRange(value);
    // }

    @Override
    public int hashCode() {
        // i mean theres 11 bits left (1 << 20) on the end...
        // thats a value around 2000... could possibly stuff
        // the maximum amount of blocks allowed to be placed in....
        // eh
        return this.dataPair.hashCode();
    }
}
