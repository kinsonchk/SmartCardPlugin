package me.ksmc.smartcardplugin.manager;

import me.ksmc.smartcardplugin.Main;
import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import me.ksmc.smartcardplugin.util.StringFormattingUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;

public class ItemManager {

    public static String issueSmartcard(Player player, String agencyID, String expiryFromLastUse) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().doesAgencyExist(agencyID)) {
            return ChatColor.RED + "Error: Agency not found.";
        }

        if (!Main.getPlugin().getConfig().getBoolean(agencyID + ".smart-card-config.enabled", false)) {
            return ChatColor.RED + "Error: Smartcards are not enabled for this agency.";
        }

        if (expiryFromLastUse != null) {
            expiryFromLastUse = expiryFromLastUse.toLowerCase(Locale.ROOT);
            if (!StringFormattingUtils.verifyTimeString(expiryFromLastUse)) {
                return ChatColor.RED + "Error: The expiryFromLastUse time input is invalid.";
            }
        }

        if (FareMediaDatabase.getFareMediaDatabase().createSmartCard(agencyID, expiryFromLastUse)) {
            ItemStack smartcard = new ItemStack(Material.NAME_TAG);
            player.getInventory().addItem(smartcard);
        } else {
            return ChatColor.RED + "Error: Database error.";
        }

        return ChatColor.GREEN + "The smartcard has been issued.";
    }



    public static String issueTicket(Player player, String agencyID, String originStation, String destinationStation, String expiryTime) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().doesAgencyExist(agencyID)) {
            return ChatColor.RED + "Error: Agency not found.";
        }

        if (!Main.getPlugin().getConfig().getBoolean(agencyID + ".single-journey-ticket-config.enabled", false)) {
            return ChatColor.RED + "Error: Tickets are not enabled for this agency.";
        }

        if (expiryTime != null) {
            expiryTime = expiryTime.toLowerCase(Locale.ROOT);
            if (!StringFormattingUtils.verifyTimeString(expiryTime)) {
                return ChatColor.RED + "Error: The expiryTime input is invalid.";
            }
        }

        ItemStack ticket = new ItemStack(Material.PAPER);
        player.getInventory().addItem(ticket);

        return ChatColor.GREEN + "The ticket has been issued.";
    }

    public static String issueExitOnlyTicket(Player player, String agencyID) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().doesAgencyExist(agencyID)) {
            return ChatColor.RED + "Error: Agency not found.";
        }

        if (!Main.getPlugin().getConfig().getBoolean(agencyID + ".exit-only-ticket-config.enabled", false)) {
            return ChatColor.RED + "Error: Exit-only tickets are not enabled for this agency.";
        }

        String expiryTime = Main.getPlugin().getConfig().getString(agencyID + ".exit-only-ticket-config.expiry", null);

        ItemStack ticket = new ItemStack(Material.PAPER);
        player.getInventory().addItem(ticket);

        return ChatColor.GREEN + "The exit-only ticket has been issued.";
    }

    public static String issuePass() throws SQLException {

        return ChatColor.GREEN + "The pass has been issued.";
    }

    public boolean confiscateTicket() {
        return false;
    }

}
