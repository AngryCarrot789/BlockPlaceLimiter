package dragonjetz.blocklimiter.limit;

import org.bukkit.configuration.ConfigurationSection;

/**
 * <h2>
 *     A class for making config getting/setting easier
 * </h2>
 * <h3>
 *     Without having to worry about the correct config section names and stuff
 * </h3>
 */
public class LimitConfigHelper {
    // --------------------------------------------------------------------------------------------------
    // ------------------------------------------ Constants ---------------------------------------------
    // --------------------------------------------------------------------------------------------------
    public static final String BypassPermissionName            = "BypassPermission";
    public static final String NoInitialPermsMsgName           = "NoInitialPermissionMessage";
    public static final String BypassBreakPermissionName       = "BypassBreakPermission";
    public static final String RangeLimitsName                 = "RangeLimits";
    public static final String RangePermissionName             = "Permission";
    public static final String LimitHitMessageName             = "LimitHitMessage";
    public static final String AllowOtherToBreakOwnerBlockName = "AllowOthersToBreakOwnerBlock";
    public static final String OtherPlayerBreakBlockMsgName    = "OtherPlayerBreakBlockMessage";
    public static final String OtherPlayerBreakBlockAttemptMsg = "OtherPlayerBreakBlockAttemptMessage";
    public static final String YouBreakOwnerBlockMsgName       = "YouBreakOwnerBlockMessage";
    public static final String YouBreakOwnerBlockAttemptMsg    = "YouBreakOwnerBlockAttemptMessage";

    // ##############################################################################################

    // ----------------------------------------------------------------------------------------------
    // ##################################### Configuration sections #################################
    // ----------------------------------------------------------------------------------------------

    public static ConfigurationSection getRangeLimitSection(ConfigurationSection section) {
        return section.getConfigurationSection(RangeLimitsName);
    }

    // ##############################################################################################

    // ----------------------------------------------------------------------------------------------
    // ############################### MetaLimits (aka real limits stuff) ###########################
    // ----------------------------------------------------------------------------------------------

    public static String getBypassPermission(ConfigurationSection section, String defaultValue) {
        return section.getString(BypassPermissionName, defaultValue);
    }

    public static String getNoInitialPermsMsg(ConfigurationSection section, String defaultValue) {
        return section.getString(NoInitialPermsMsgName, defaultValue);
    }

    public static String getBypassBreakPermission(ConfigurationSection section, String defaultValue) {
        return section.getString(BypassBreakPermissionName, defaultValue);
    }

    public static String getRangePlacePermission(ConfigurationSection section, String defaultValue) {
        return section.getString(RangePermissionName, defaultValue);
    }

    public static String getLimitHitMessage(ConfigurationSection section, String defaultValue) {
        return section.getString(LimitHitMessageName, defaultValue);
    }

    public static boolean getAllowOthersToBreakOwnerBlock(ConfigurationSection section, boolean defaultValue) {
        return section.getBoolean(AllowOtherToBreakOwnerBlockName, defaultValue);
    }

    public static String getOtherPlayerBrokenOwnerBlockMsg(ConfigurationSection section, String defaultValue) {
        return section.getString(OtherPlayerBreakBlockMsgName, defaultValue);
    }

    public static String getOtherPlayerBreakOwnerBlockAttemptMsg(ConfigurationSection section, String defaultValue) {
        return section.getString(OtherPlayerBreakBlockAttemptMsg, defaultValue);
    }

    public static String getYouBreakOwnerBlockMsg(ConfigurationSection section, String defaultValue) {
        return section.getString(YouBreakOwnerBlockMsgName, defaultValue);
    }

    public static String getYouBreakOwnerBlockAttemptMsg(ConfigurationSection section, String defaultValue) {
        return section.getString(YouBreakOwnerBlockAttemptMsg, defaultValue);
    }
}
