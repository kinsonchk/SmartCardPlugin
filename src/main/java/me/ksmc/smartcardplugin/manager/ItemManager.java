package me.ksmc.smartcardplugin.manager;

import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import me.ksmc.smartcardplugin.util.ItemUtils;
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

    public static boolean isValidFareMedium(ItemStack item) {
        if (!item.hasItemMeta()) {
            return false;
        }

        if (!item.getItemMeta().hasLore()) {
            return false;
        }

        boolean hasAgencyIdAttribute = false;
        for (String line : item.getItemMeta().getLore()) {
            if (line.contains("Agency ID")) {
                hasAgencyIdAttribute = true;
                break;
            }
        }

        return hasAgencyIdAttribute && item.getItemMeta().isUnbreakable();
    }


    public static String issueSmartcard(Player player, String agencyID, String expiryFromLastUse) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().agencyExists(agencyID)) {
            return ChatColor.RED + "Error: Agency not found.";
        }

        if (!ItemUtils.smartcardEnabled(agencyID)) {
            return ChatColor.RED + "Error: Smartcards are not enabled for this agency.";
        }

        if (expiryFromLastUse != null) {
            expiryFromLastUse = expiryFromLastUse.toLowerCase(Locale.ROOT);
            if (!StringUtils.validTimeString(expiryFromLastUse)) {
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

        String smartcardName = ItemUtils.getSmartcardBrandName(agencyID);
        smartcardMeta.setDisplayName(ChatColor.YELLOW + smartcardName + ChatColor.GREEN + " | Smartcard");
        smartcardMeta.setUnbreakable(true);

        List<String> smartcardLore = new ArrayList<>();
        smartcardLore.add("Card ID: " + ChatColor.GOLD + cardID);
        smartcardLore.add("Agency ID: " + ChatColor.GOLD + agencyID);
        smartcardLore.add("Expiry: " + ChatColor.GOLD + expiryFromLastUse + " from last use");

        smartcardMeta.setLore(smartcardLore);
        smartcard.setItemMeta(smartcardMeta);

        player.getInventory().addItem(smartcard);

        return ChatColor.GREEN + "The smartcard has been issued.";
    }

    public static String getSmartcardBalance(String cardID) throws SQLException {
        Object balance = FareMediaDatabase.getFareMediaDatabase().getCardBalance(cardID);
        if (balance == null) {
            return null;
        }
        return balance.toString();
    }

    public static String updateSmartcardBalance(String cardID, String moneyAmount) throws SQLException {
        if (!StringUtils.validMoneyString(moneyAmount)) {
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

    public static String resetSmartcardEntryExitRecord(String cardID) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().clearEntryExitRecord(cardID)) {
            return ChatColor.RED + "Error: cardID not found.";
        }

        return ChatColor.GREEN + "The smartcard's current entry and exit records have been cleared.";
    }

    public static String deleteSmartcard(String cardID) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().deleteSmartCard(cardID)) {
            return ChatColor.RED + "Error: cardID not found.";
        }

        return ChatColor.GREEN + "The smartcard has been deleted.";
    }


    public static String issueTicket(Player player, String agencyID, String originStation, String destinationStation, String expiryDuration) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().agencyExists(agencyID)) {
            return ChatColor.RED + "Error: Agency not found.";
        }

        if (!ItemUtils.ticketsEnabled(agencyID)) {
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

        String companyName = ItemUtils.getCompanyName(agencyID);
        ticketMeta.setDisplayName(ChatColor.GREEN + "Single-Journey Ticket - " + companyName);
        ticketMeta.setUnbreakable(true);

        List<String> ticketLore = new ArrayList<>();
        ticketLore.add("Agency ID: " + ChatColor.GOLD + agencyID);
        ticketLore.add("Origin Station: " + ChatColor.GOLD + originStation);
        ticketLore.add("Destination Station: " + ChatColor.GOLD + destinationStation);
        ticketLore.add("Expiry: " + ChatColor.GOLD + expiryTime);
        ticketLore.add("Used: " + ChatColor.GOLD + false);

        ticketMeta.setLore(ticketLore);
        ticket.setItemMeta(ticketMeta);

        player.getInventory().addItem(ticket);

        return ChatColor.GREEN + "The ticket has been issued.";
    }

    public static boolean ticketUsed(ItemStack ticket) {
        if (StringUtils.getTicketUsedStateFromLore(ticket).equals("true")) {
            return true;
        }
        return false;
    }

    public static void updateTicketToUsedState(ItemStack ticket) {
        ItemMeta ticketMeta = ticket.getItemMeta();

        List<String> ticketLore = ticketMeta.getLore();
        ticketLore.set(4, "Used: " + ChatColor.GOLD + true);

        ticketMeta.setLore(ticketLore);
        ticket.setItemMeta(ticketMeta);
    }


    public static String issueExitOnlyTicket(Player player, String agencyID) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().agencyExists(agencyID)) {
            return ChatColor.RED + "Error: Agency not found.";
        }

        if (!ItemUtils.exitOnlyTicketsEnabled(agencyID)) {
            return ChatColor.RED + "Error: Exit-only tickets are not enabled for this agency.";
        }

        String expiryDuration = ItemUtils.getExitOnlyTicketExpiryDefaultValue(agencyID);
        Object expiryTimestamp = StringUtils.convertExpiryToTimestamp(expiryDuration);
        if (expiryTimestamp == null) {
            return ChatColor.RED + "Error: The expiryTime input is invalid.";
        }
        String expiryTime = expiryTimestamp.toString();

        // issue the ticket
        ItemStack exitOnlyTicket = new ItemStack(Material.IRON_NUGGET);

        ItemMeta exitOnlyTicketMeta = exitOnlyTicket.getItemMeta();

        String companyName = ItemUtils.getCompanyName(agencyID);
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

    public static void confiscateTicket(Player player, ItemStack ticket) {
        player.getInventory().removeItem(ticket);
    }


    public static String issuePass(Player player, String agencyID, String passType) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().agencyExists(agencyID)) {
            return ChatColor.RED + "Error: Agency not found.";
        }

        if (!ItemUtils.passesEnabled(agencyID)) {
            return ChatColor.RED + "Error: Passes are not enabled for this agency.";
        }

        if (!ItemUtils.passTypeExists(agencyID, passType)) {
            return ChatColor.RED + "Error: passType not found.";
        }
        int tripsRemaining = ItemUtils.getTripsAllowedForPass(agencyID, passType);

        String expiryDuration = ItemUtils.getPassExpiryValue(agencyID, passType);
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

        String passName = ItemUtils.getPassName(agencyID, passType);
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

    public static boolean passEnteredGate(String passID) {


        return false;
    }

    public static String deletePass(String passID) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().deletePass(passID)) {
            return ChatColor.RED + "Error: passID not found.";
        }

        return ChatColor.GREEN + "The pass has been deleted.";
    }


    public static String issueStaffPass(Player player, String agencyID) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().agencyExists(agencyID)) {
            return ChatColor.RED + "Error: Agency not found.";
        }

        ItemStack staffPass = new ItemStack(Material.TRIAL_KEY);

        ItemMeta staffPassMeta = staffPass.getItemMeta();

        String companyName = ItemUtils.getCompanyName(agencyID);
        staffPassMeta.setDisplayName(ChatColor.GREEN + "Staff Pass - " + companyName);
        staffPassMeta.setUnbreakable(true);

        List<String> staffPassLore = new ArrayList<>();
        staffPassLore.add("Agency ID: " + ChatColor.GOLD + agencyID);

        staffPassMeta.setLore(staffPassLore);
        staffPass.setItemMeta(staffPassMeta);

        player.getInventory().addItem(staffPass);

        return ChatColor.GREEN + "The staff pass has been issued.";
    }

}
