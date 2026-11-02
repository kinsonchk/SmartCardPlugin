package me.ksmc.smartcardplugin.util;

import me.ksmc.smartcardplugin.Main;
import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    public static double getCardPenaltyFee(String agencyID) {
        return Main.getPlugin().getConfig().getDouble(agencyID + ".smart-card-config.penalty-fee", 0);
    }

    public static String getCardExpiryFromLastUseDefaultValue(String agencyID) {
        return Main.getPlugin().getConfig().getString(agencyID + ".smart-card-config.card-expiry-from-last-use", null);
    }

    public static String smartcardEnquiry(String cardID) {
        String agencyID = "";
        try {
            agencyID = FareMediaDatabase.getFareMediaDatabase().getAgencyIdOnCard(cardID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String cardBalance = "";
        try {
            cardBalance = FareMediaDatabase.getFareMediaDatabase().getCardBalance(cardID).toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String cardExpiryDuration = "";
        try {
            cardExpiryDuration = FareMediaDatabase.getFareMediaDatabase().getCardExpiryFromLastUseDuration(cardID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String cardLastUsedTimestamp = "";
        try {
            cardLastUsedTimestamp = FareMediaDatabase.getFareMediaDatabase().getCardLastUsedTimestamp(cardID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String currentEntryRecord = "";
        try {
            currentEntryRecord = FareMediaDatabase.getFareMediaDatabase().getEntryRecord(cardID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        StringBuilder entryRecordToDisplay = new StringBuilder();
        if (currentEntryRecord != null) {
            String[] entryRecord = currentEntryRecord.split(",");
            entryRecordToDisplay.append("\n ");
            for (String info : entryRecord) {
                entryRecordToDisplay.append(info + "  ");
            }
        } else {
            entryRecordToDisplay = new StringBuilder("None");
        }

        String cardTransactions = "";
        try {
            cardTransactions = FareMediaDatabase.getFareMediaDatabase().getCardTransactionRecords(cardID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String[] transactionsList = cardTransactions.split(";");
        StringBuilder transactionsStringToDisplay = new StringBuilder();
        if (!cardTransactions.isEmpty()) {
            int index = 0;
            for (String transaction : transactionsList) {
                index++;
                transactionsStringToDisplay.append("\n" + index + ".  ");
                String[] transactionRecord = transaction.split(",");
                for (String info : transactionRecord) {
                    transactionsStringToDisplay.append(info + "  ");
                }
            }
        } else {
            transactionsStringToDisplay = new StringBuilder("\nNo records found on this smart card.");
        }

        return (ChatColor.GREEN + "Smart Card Info:" +
                ChatColor.YELLOW + "\nCard ID: " + ChatColor.GOLD + cardID +
                ChatColor.YELLOW + "\nCard Brand Name: " + ChatColor.GOLD + ItemUtils.getSmartcardBrandName(agencyID) +
                ChatColor.YELLOW + "\nCard Issuing Agency: " + ChatColor.GOLD + ItemUtils.getCompanyName(agencyID) + ChatColor.YELLOW + "  (Agency ID: " + ChatColor.GOLD + agencyID + ChatColor.YELLOW + ")" +
                ChatColor.YELLOW + "\nCard Balance: " + ChatColor.GOLD + "$" + cardBalance +
                ChatColor.YELLOW + "\nCard Expiry: " + ChatColor.GOLD + cardExpiryDuration + ChatColor.YELLOW + " from last use.  (Last Used Time: " + ChatColor.GOLD + cardLastUsedTimestamp + ChatColor.YELLOW + ")" +
                ChatColor.YELLOW + "\nCurrent Entry Record on card: " + ChatColor.AQUA + entryRecordToDisplay +
                ChatColor.YELLOW + "\nThe 5 most recent transactions (from most recent to least recent):" + ChatColor.AQUA + transactionsStringToDisplay
        );
    }

    public static boolean cardCanBeUsedWithSpecifiedAgency(String agencyIdOnCard, String specifiedAgencyId) {
        if (agencyIdOnCard.equalsIgnoreCase(specifiedAgencyId)) {
            return true;
        }

        List<String> useInOtherCompanies = Main.getPlugin().getConfig().getStringList(agencyIdOnCard + ".smart-card-config.use-in-other-companies");

        if (useInOtherCompanies.isEmpty() || useInOtherCompanies.contains(specifiedAgencyId)) {
            return true;
        }

        return false;
    }

    public static boolean cardExpired(String cardID) throws SQLException {
        Instant cardLastUsedTimestamp = Instant.parse(FareMediaDatabase.getFareMediaDatabase().getCardLastUsedTimestamp(cardID));
        Instant currentTimestamp = Instant.now();
        Duration cardExpiryDuration = StringUtils.convertDurationToTimeUnits(FareMediaDatabase.getFareMediaDatabase().getCardExpiryFromLastUseDuration(cardID));
        if (Duration.between(cardLastUsedTimestamp, currentTimestamp).compareTo(cardExpiryDuration) >= 0) {
            return true;
        }
        return false;
    }


    public static boolean ticketsEnabled(String agencyID) {
        return Main.getPlugin().getConfig().getBoolean(agencyID + ".single-journey-ticket-config.enabled", false);
    }

    public static String getTicketExpiryDefaultValue(String agencyID) {
        return Main.getPlugin().getConfig().getString(agencyID + ".single-journey-ticket-config.expiry", null);
    }

    public static boolean ticketExpired(ItemStack ticket) {
        Instant ticketExpiryTimestamp = Instant.parse(StringUtils.getTicketExpiryTimestampFromLore(ticket));
        Instant currentTimestamp = Instant.now();
        if (ticketExpiryTimestamp.isBefore(currentTimestamp)) {
            return true;
        }
        return false;
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

//    public static boolean passValidForStation(String agencyID, ) {
//
//    }

    public static int getTripsAllowedForPass(String agencyID, String passType) {
        return Main.getPlugin().getConfig().getInt(agencyID + ".pass-config.pass-types." + passType + ".trips-allowed", -1);
    }

    public static String getPassExpiryValue(String agencyID, String passType) {
        return Main.getPlugin().getConfig().getString(agencyID + ".pass-config.pass-types." + passType + ".expiry", null);
    }

    public static String passEnquiry(String passID) {
        String agencyID = "";
        try {
            agencyID = FareMediaDatabase.getFareMediaDatabase().getAgencyIdOnPass(passID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String passType = "";
        try {
            passType = FareMediaDatabase.getFareMediaDatabase().getPassTypeOnPass(passID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int tripsRemaining = 0;
        try {
            tripsRemaining = FareMediaDatabase.getFareMediaDatabase().getTripsRemainingOnPass(passID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String tripsRemainingToDisplay;
        if (tripsRemaining == -1) {
            tripsRemainingToDisplay = "unlimited";
        } else {
            tripsRemainingToDisplay = Integer.toString(tripsRemaining);
        }

        String passEnteredGateState = "";
        try {
            passEnteredGateState = String.valueOf(FareMediaDatabase.getFareMediaDatabase().getPassEnteredGateState(passID));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String passExpiry = "";
        try {
            passExpiry = FareMediaDatabase.getFareMediaDatabase().getPassExpiryTimestamp(passID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return(ChatColor.GREEN + "Pass Info:" +
                ChatColor.YELLOW + "\nPass ID: " + ChatColor.GOLD + passID +
                ChatColor.YELLOW + "\nPass Type Name: " + ChatColor.GOLD + ItemUtils.getPassName(agencyID, passType) + ChatColor.YELLOW + "  (Pass Type: " + ChatColor.GOLD + passType + ChatColor.YELLOW + ")" +
                ChatColor.YELLOW + "\nPass Issuing Agency: " + ChatColor.GOLD + ItemUtils.getCompanyName(agencyID) + ChatColor.YELLOW + "  (Agency ID: " + ChatColor.GOLD + agencyID + ChatColor.YELLOW + ")" +
                ChatColor.YELLOW + "\nTrips Remaining on Pass: " + ChatColor.GOLD + tripsRemainingToDisplay +
                ChatColor.YELLOW + "\nPass Expiry: " + ChatColor.GOLD + passExpiry +
                ChatColor.YELLOW + "\nEntered Gate State: " + ChatColor.GOLD + passEnteredGateState
        );
    }


    public static boolean exitOnlyTicketsEnabled(String agencyID) {
        return Main.getPlugin().getConfig().getBoolean(agencyID + ".exit-only-ticket-config.enabled", false);
    }

    public static String getExitOnlyTicketExpiryDefaultValue(String agencyID) {
        return Main.getPlugin().getConfig().getString(agencyID + ".exit-only-ticket-config.expiry", null);
    }

    public static boolean exitOnlyTicketExpired(ItemStack ticket) {
        Instant ticketExpiryTimestamp = Instant.parse(StringUtils.getExitOnlyTicketExpiryTimestampFromLore(ticket));
        Instant currentTimestamp = Instant.now();
        if (ticketExpiryTimestamp.isBefore(currentTimestamp)) {
            return true;
        }
        return false;
    }

}
