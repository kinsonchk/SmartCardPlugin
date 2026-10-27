package me.ksmc.smartcardplugin.manager.device;

import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import me.ksmc.smartcardplugin.manager.EconomyManager;
import me.ksmc.smartcardplugin.manager.ItemManager;
import me.ksmc.smartcardplugin.util.ItemUtils;
import me.ksmc.smartcardplugin.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class ExitDeviceManager {

    public static String activateDevice(String deviceType, Sign deviceSign, Player activeUser, ItemStack fareMedium, String specifiedAgencyId, String specifiedStationCode) throws SQLException {

        if (fareMedium.getType() == Material.NAME_TAG) {  // smartcard
            String cardID = StringUtils.getCardOrPassIdFromLore(fareMedium);

            // CHECK IF THE CARD HAS EXPIRED ALREADY



            String cardBalanceRaw = ItemManager.getSmartcardBalance(cardID);
            if (cardBalanceRaw == null) {
                return ChatColor.RED + "Error: Database error.";
            }
            double cardCurrentBalance = Double.parseDouble(cardBalanceRaw);



            if (FareMediaDatabase.getFareMediaDatabase().hasEntryRecord(cardID)) {
                String agencyIdOnCardEntryRecord = StringUtils.getAgencyIdOnCardEntryRecord(cardID);
                if (!agencyIdOnCardEntryRecord.equals(specifiedAgencyId)) {
                    return ChatColor.RED + "Error: This card has been used to activate an entry device of another agency. You must use it to activate a device of that same agency to use the card again, or have an admin reset the card's entry record.";
                }
            }

            // NOTE: the penalty fee here is that of the agencyID specified on the device sign, NOT the agencyID of the card
            if (!FareMediaDatabase.getFareMediaDatabase().hasEntryRecord(cardID)) {
                double cardPenaltyFee = ItemUtils.getCardPenaltyFee(specifiedAgencyId);
                if (!StringUtils.validMoneyString(String.valueOf(cardPenaltyFee))) {
                    return ChatColor.RED + "Error: The penalty fee amount input is invalid.";
                }

                double cardNewBalance = cardCurrentBalance - cardPenaltyFee;
                if (!FareMediaDatabase.getFareMediaDatabase().setCardBalance(cardID, cardNewBalance)) {
                    return ChatColor.RED + "Error: Database error.";
                }

                activeUser.sendMessage(ChatColor.GOLD + "$" + cardPenaltyFee + ChatColor.GREEN + " penalty fee has been deducted from the card.");
                EconomyManager.addAgencyRevenue(specifiedAgencyId, cardPenaltyFee);
            }

            // CALCULATE FARE FROM FARE CHART


            FareMediaDatabase.getFareMediaDatabase().createExitRecord(cardID, specifiedAgencyId, specifiedStationCode);
            // CREATE TRANSACTION RECORD
            ItemManager.resetSmartcardEntryExitRecord(cardID);
            activeUser.sendMessage(ChatColor.GREEN + "Smart card interaction successful.");


        } else if (fareMedium.getType() == Material.PAPER) {  // single-journey ticket
            String agencyIdOnTicket = StringUtils.getAgencyIdFromLore(fareMedium);
            if (!agencyIdOnTicket.equals(specifiedAgencyId)) {
                return ChatColor.RED + "Error: The agencyID on the ticket does not match with the one specified on the device.";
            }

            if (!ItemManager.ticketUsed(fareMedium)) {
                return ChatColor.RED + "Error: This ticket has not yet been used.";
            }

            String destinationStationCode = StringUtils.getTicketDestinationStationFromLore(fareMedium);
            if (!destinationStationCode.equals(specifiedStationCode)) {
                return ChatColor.RED + "Error: The destination station on the ticket does not match with the one specified on the device.";
            }

            // confiscate the ticket
            ItemManager.confiscateTicket(activeUser, fareMedium);
            activeUser.sendMessage(ChatColor.GREEN + "The ticket has been confiscated.");

        } else if (fareMedium.getType() == Material.BOOK) {  // pass



        } else if (fareMedium.getType() == Material.IRON_NUGGET) {  // exit-only ticket
            String agencyIdOnTicket = StringUtils.getAgencyIdFromLore(fareMedium);
            if (!agencyIdOnTicket.equals(specifiedAgencyId)) {
                return ChatColor.RED + "Error: The agencyID on the exit-only ticket does not match with the one specified on the device.";
            }

            // confiscate the ticket
            ItemManager.confiscateTicket(activeUser, fareMedium);
            activeUser.sendMessage(ChatColor.GREEN + "The exit-only ticket has been confiscated.");

        } else if (fareMedium.getType() == Material.TRIAL_KEY) {  // staff pass
            String agencyIdOnStaffPass = StringUtils.getAgencyIdFromLore(fareMedium);
            if (!agencyIdOnStaffPass.equals(specifiedAgencyId)) {
                return ChatColor.RED + "Error: The agencyID on the staff pass does not match with the one specified on the device.";
            }
        }

        if (deviceType.equalsIgnoreCase("gate")) {
            // activate the gate
        }

        return ChatColor.GREEN + "Exit " + deviceType + " activated.";
    }

}
