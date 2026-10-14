package me.ksmc.smartcardplugin.util;

import me.ksmc.smartcardplugin.Main;
import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class ItemUtils {

    public static String getCompanyName(String agencyID) {
        return Main.getPlugin().getConfig().getString(agencyID + ".company-name");
    }


    public static boolean smartcardEnabled(String agencyID) {
        return Main.getPlugin().getConfig().getBoolean(agencyID + ".smart-card-config.enabled", false);
    }

    public static String getSmartcardBrandName(String agencyID) {
        return Main.getPlugin().getConfig().getString(agencyID + ".smart-card-config.card-brand-name");
    }

    public static String getCardExpiryFromLastUseDefaultValue(String agencyID) {
        return Main.getPlugin().getConfig().getString(agencyID + ".smart-card-config.card-expiry-from-last-use", null);
    }


    public static boolean ticketsEnabled(String agencyID) {
        return Main.getPlugin().getConfig().getBoolean(agencyID + ".single-journey-ticket-config.enabled", false);
    }

    public static String getTicketExpiryDefaultValue(String agencyID) {
        return Main.getPlugin().getConfig().getString(agencyID + ".single-journey-ticket-config.expiry", null);
    }


    public static boolean passesEnabled(String agencyID) {
        return Main.getPlugin().getConfig().getBoolean(agencyID + ".pass-config.enabled", false);
    }

    public static boolean passTypeExists(String agencyID, String passType) {
        return Main.getPlugin().getConfig().isConfigurationSection(agencyID + ".pass-config.pass-types." + passType);
    }

    public static String getPassName(String agencyID, String passType) {
        return Main.getPlugin().getConfig().getString(agencyID + ".pass-config.pass-types." + passType + ".pass-type-name");
    }

    public static int getTripsAllowedForPass(String agencyID, String passType) {
        return Main.getPlugin().getConfig().getInt(agencyID + ".pass-config.pass-types." + passType + ".trips-allowed", -1);
    }

    public static String getPassExpiryValue(String agencyID, String passType) {
        return Main.getPlugin().getConfig().getString(agencyID + ".pass-config.pass-types." + passType + ".expiry", null);
    }


    public static boolean exitOnlyTicketsEnabled(String agencyID) {
        return Main.getPlugin().getConfig().getBoolean(agencyID + ".exit-only-ticket-config.enabled", false);
    }

    public static String getExitOnlyTicketExpiryDefaultValue(String agencyID) {
        return Main.getPlugin().getConfig().getString(agencyID + ".exit-only-ticket-config.expiry", null);
    }

}
