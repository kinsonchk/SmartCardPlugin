package me.ksmc.smartcardplugin.manager;

import me.ksmc.smartcardplugin.Main;
import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import me.ksmc.smartcardplugin.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemManager {

    public static String issueSmartcard(Player player, String agencyID, String expiryFromLastUse) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().agencyExists(agencyID)) {
            return ChatColor.RED + "Error: Agency not found.";
        }

        if (!Main.getPlugin().getConfig().getBoolean(agencyID + ".smart-card-config.enabled", false)) {
            return ChatColor.RED + "Error: Smartcards are not enabled for this agency.";
        }

        if (expiryFromLastUse != null) {
            expiryFromLastUse = expiryFromLastUse.toLowerCase(Locale.ROOT);
            if (!StringUtils.verifyTimeString(expiryFromLastUse)) {
                return ChatColor.RED + "Error: The expiryFromLastUse time input is invalid.";
            }
        }

        Object cardIdRaw = FareMediaDatabase.getFareMediaDatabase().createSmartCard(agencyID, expiryFromLastUse);
        if (cardIdRaw == null) {
            return ChatColor.RED + "Error: Database error.";
        }
        String cardID = cardIdRaw.toString();

        // issue the smartcard
        ItemStack smartcard = new ItemStack(Material.NAME_TAG);

        ItemMeta smartcardMeta = smartcard.getItemMeta();

        String smartcardName = Main.getPlugin().getConfig().getString(agencyID + ".smart-card-config.card-brand-name");
        smartcardMeta.setDisplayName(ChatColor.YELLOW + smartcardName + ChatColor.GREEN + " | Smartcard");
        smartcardMeta.setUnbreakable(true);

        List<String> smartcardLore = new ArrayList<>();
        smartcardLore.add("Card ID: " + ChatColor.GOLD + cardID);
        smartcardLore.add("Agency ID: " + ChatColor.GOLD + agencyID);

        smartcardMeta.setLore(smartcardLore);
        smartcard.setItemMeta(smartcardMeta);

        player.getInventory().addItem(smartcard);

        return ChatColor.GREEN + "The smartcard has been issued.";
    }

    public static String updateSmartcardBalance(String cardID, String moneyAmount) throws SQLException {
        if (!StringUtils.verifyMoneyString(moneyAmount)) {
            return ChatColor.RED + "Error: The money amount input is invalid.";
        }

        try {
            if (!FareMediaDatabase.getFareMediaDatabase().setCardBalance(cardID, Double.parseDouble(moneyAmount))) {
                return ChatColor.RED + "Error: cardID not found.";
            }
        } catch (NumberFormatException e) {
            return ChatColor.RED + "Error: The money amount input is invalid.";
        }

        return ChatColor.GREEN + "The smartcard's balance has been updated to " + ChatColor.GOLD + "$" + moneyAmount + ChatColor.GREEN + ".";
    }

    public static String resetSmartcardEntryExitRecords(String cardID) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().clearEntryExitRecords(cardID)) {
            return ChatColor.RED + "Error: cardID not found.";
        }

        return ChatColor.GREEN + "The smartcard's current entry and exit records have been cleared.";
    }

    public static String issueTicket(Player player, String agencyID, String originStation, String destinationStation, String expiryDuration) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().agencyExists(agencyID)) {
            return ChatColor.RED + "Error: Agency not found.";
        }

        if (!Main.getPlugin().getConfig().getBoolean(agencyID + ".single-journey-ticket-config.enabled", false)) {
            return ChatColor.RED + "Error: Tickets are not enabled for this agency.";
        }

        Object expiryTimestamp = StringUtils.convertExpiryToTimestamp(expiryDuration);
        if (expiryTimestamp == null) {
            return ChatColor.RED + "Error: The expiryTime input is invalid.";
        }
        String expiryTime = expiryTimestamp.toString();

        // issue the ticket
        ItemStack ticket = new ItemStack(Material.PAPER);

        ItemMeta ticketMeta = ticket.getItemMeta();

        String companyName = Main.getPlugin().getConfig().getString(agencyID + ".company-name");
        ticketMeta.setDisplayName(ChatColor.GREEN + "Single-Journey Ticket - " + companyName);
        ticketMeta.setUnbreakable(true);

        List<String> ticketLore = new ArrayList<>();
        ticketLore.add("Agency ID: " + ChatColor.GOLD + agencyID);
        ticketLore.add("Origin Station: " + ChatColor.GOLD + originStation);
        ticketLore.add("Destination Station: " + ChatColor.GOLD + destinationStation);
        ticketLore.add("Expiry: " + ChatColor.GOLD + expiryTime);

        ticketMeta.setLore(ticketLore);
        ticket.setItemMeta(ticketMeta);

        player.getInventory().addItem(ticket);

        return ChatColor.GREEN + "The ticket has been issued.";
    }

    public static String issueExitOnlyTicket(Player player, String agencyID) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().agencyExists(agencyID)) {
            return ChatColor.RED + "Error: Agency not found.";
        }

        if (!Main.getPlugin().getConfig().getBoolean(agencyID + ".exit-only-ticket-config.enabled", false)) {
            return ChatColor.RED + "Error: Exit-only tickets are not enabled for this agency.";
        }

        String expiryDuration = Main.getPlugin().getConfig().getString(agencyID + ".exit-only-ticket-config.expiry", null);
        Object expiryTimestamp = StringUtils.convertExpiryToTimestamp(expiryDuration);
        if (expiryTimestamp == null) {
            return ChatColor.RED + "Error: The expiryTime input is invalid.";
        }
        String expiryTime = expiryTimestamp.toString();

        // issue the ticket
        ItemStack exitOnlyTicket = new ItemStack(Material.IRON_NUGGET);

        ItemMeta exitOnlyTicketMeta = exitOnlyTicket.getItemMeta();

        String companyName = Main.getPlugin().getConfig().getString(agencyID + ".company-name");
        exitOnlyTicketMeta.setDisplayName(ChatColor.GREEN + "Exit-Only Ticket - " + companyName);
        exitOnlyTicketMeta.setUnbreakable(true);

        List<String> ticketLore = new ArrayList<>();
        ticketLore.add("Agency ID: " + ChatColor.GOLD + agencyID);
        ticketLore.add("Expiry: " + ChatColor.GOLD + expiryTime);

        exitOnlyTicketMeta.setLore(ticketLore);
        exitOnlyTicket.setItemMeta(exitOnlyTicketMeta);

        player.getInventory().addItem(exitOnlyTicket);

        return ChatColor.GREEN + "The exit-only ticket has been issued.";
    }

    public static String issuePass(Player player, String agencyID, String passType) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().agencyExists(agencyID)) {
            return ChatColor.RED + "Error: Agency not found.";
        }

        if (!Main.getPlugin().getConfig().getBoolean(agencyID + ".pass-config.enabled", false)) {
            return ChatColor.RED + "Error: Passes are not enabled for this agency.";
        }

        if (!Main.getPlugin().getConfig().isConfigurationSection(agencyID + ".pass-config.pass-types." + passType)) {
            return ChatColor.RED + "Error: passType not found.";
        }
        int tripsRemaining = Main.getPlugin().getConfig().getInt(agencyID + ".pass-config.pass-types." + passType + ".trips-allowed", -1);

        String expiryDuration = Main.getPlugin().getConfig().getString(agencyID + ".pass-config.pass-types." + passType + ".expiry", null);
        Object expiryTimestamp = StringUtils.convertExpiryToTimestamp(expiryDuration);
        if (expiryTimestamp == null) {
            return ChatColor.RED + "Error: The expiryTime input is invalid.";
        }
        String expiryTime = expiryTimestamp.toString();

        Object passIdRaw = FareMediaDatabase.getFareMediaDatabase().createPass(agencyID, passType, tripsRemaining, expiryTime);
        if (passIdRaw == null) {
            return ChatColor.RED + "Error: Database error.";
        }
        String passID = passIdRaw.toString();

        // issue the pass
        ItemStack pass = new ItemStack(Material.BOOK);

        ItemMeta passMeta = pass.getItemMeta();

        String passName = Main.getPlugin().getConfig().getString(agencyID + ".pass-config.pass-types." + passType + ".pass-type-name");
        passMeta.setDisplayName(ChatColor.YELLOW + passName + ChatColor.GREEN + " | Pass");
        passMeta.setUnbreakable(true);

        List<String> passLore = new ArrayList<>();
        passLore.add("Pass ID: " + ChatColor.GOLD + passID);
        passLore.add("Agency ID: " + ChatColor.GOLD + agencyID);
        passLore.add("Expiry: " + ChatColor.GOLD + expiryTime);

        passMeta.setLore(passLore);
        pass.setItemMeta(passMeta);

        player.getInventory().addItem(pass);

        return ChatColor.GREEN + "The pass has been issued.";
    }

    public static String issueStaffPass(Player player, String agencyID) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().agencyExists(agencyID)) {
            return ChatColor.RED + "Error: Agency not found.";
        }

        ItemStack staffPass = new ItemStack(Material.TRIAL_KEY);

        ItemMeta staffPassMeta = staffPass.getItemMeta();

        String companyName = Main.getPlugin().getConfig().getString(agencyID + ".company-name");
        staffPassMeta.setDisplayName(ChatColor.GREEN + "Staff Pass - " + companyName);
        staffPassMeta.setUnbreakable(true);

        List<String> staffPassLore = new ArrayList<>();
        staffPassLore.add("Agency ID: " + ChatColor.GOLD + agencyID);

        staffPassMeta.setLore(staffPassLore);
        staffPass.setItemMeta(staffPassMeta);

        player.getInventory().addItem(staffPass);

        return ChatColor.GREEN + "The staff pass has been issued.";
    }

    public static String deleteSmartcard(String cardID) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().deleteSmartCard(cardID)) {
            return ChatColor.RED + "Error: cardID not found.";
        }

        return ChatColor.GREEN + "The smartcard has been deleted.";
    }

    public static String deletePass(String passID) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().deletePass(passID)) {
            return ChatColor.RED + "Error: passID not found.";
        }

        return ChatColor.GREEN + "The pass has been deleted.";
    }


    public boolean confiscateTicket() {
        return false;
    }

}
