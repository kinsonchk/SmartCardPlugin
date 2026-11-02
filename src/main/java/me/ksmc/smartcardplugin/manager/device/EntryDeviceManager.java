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
import java.time.Duration;
import java.time.Instant;

public class EntryDeviceManager {

    public static String activateDevice(String deviceType, Sign deviceSign, Player activeUser, ItemStack fareMedium, String specifiedAgencyId, String specifiedStationCode) throws SQLException {

        if (fareMedium.getType() == Material.NAME_TAG) {  // smartcard
            String cardID = StringUtils.getCardIdFromLore(fareMedium);
            String agencyIdOnCard = FareMediaDatabase.getFareMediaDatabase().getAgencyIdOnCard(cardID);

            if (ItemUtils.cardExpired(cardID)) {
                return ChatColor.RED + "Error: This smart card has already expired. Please use another card.";
            }

            if (!ItemUtils.cardCanBeUsedWithSpecifiedAgency(agencyIdOnCard, specifiedAgencyId)) {
                return ChatColor.RED + "Error: This card cannot be used with the specified agency.";
            }

            String cardBalanceRaw = ItemManager.getSmartcardBalance(cardID);
            if (cardBalanceRaw == null) {
                return ChatColor.RED + "Error: Database error.";
            }
            double cardCurrentBalance = Double.parseDouble(cardBalanceRaw);
            if (cardCurrentBalance <= 0) {
                return ChatColor.RED + "Error: The smartcard's balance is too low. Please add value to use it.";
            }

            double penaltyFare = 0;

            if (FareMediaDatabase.getFareMediaDatabase().hasEntryRecord(cardID)) {
                String agencyIdOnCardEntryRecord = StringUtils.getAgencyIdOnCardEntryRecord(cardID);
                if (!agencyIdOnCardEntryRecord.equals(specifiedAgencyId)) {
                    return ChatColor.RED + "Error: This card has been used to activate an entry device of another agency. You must use it to activate a device of that same agency to use the card again, or have an admin reset the card's entry record.";
                }

                double accumulatedPenaltyFare = Double.parseDouble(StringUtils.getPenaltyFareOnCardEntryRecord(cardID));

                // Record the surcharge/penalty fee from card for already having an entry record, and add it to the total fare upon exiting
                // the penalty fare will accumulate, per activation, until the card is interacted with an exit device, in which the total fare will be deducted, and the entry record will be cleared
                // NOTE: it uses the agencyID ON THE CARD'S ENTRY RECORD, NOT the agencyID that issued the card, NOR the agencyID specified on the entry device sign
                double cardPenaltyFee = ItemUtils.getCardPenaltyFee(agencyIdOnCardEntryRecord);
                if (!StringUtils.validMoneyString(String.valueOf(cardPenaltyFee))) {
                    return ChatColor.RED + "Error: The penalty fee amount input is invalid.";
                }

                double cardNewBalance = cardCurrentBalance - cardPenaltyFee - accumulatedPenaltyFare;
                if (cardNewBalance <= 0) {
                    return ChatColor.RED + "Error: The smartcard's balance is too low. Please add value to use it.";
                }

                accumulatedPenaltyFare += cardPenaltyFee;
                penaltyFare = accumulatedPenaltyFare;

                activeUser.sendMessage(ChatColor.GOLD + "$" + cardPenaltyFee + ChatColor.GREEN + " penalty fee will be added to the fare.");
            }

            FareMediaDatabase.getFareMediaDatabase().createEntryRecord(cardID, specifiedAgencyId, specifiedStationCode, String.valueOf(penaltyFare));

            FareMediaDatabase.getFareMediaDatabase().updateCardLastUsedTimestamp(cardID);
            activeUser.sendMessage(ChatColor.GREEN + "Smart card interaction successful.");


        } else if (fareMedium.getType() == Material.PAPER) {  // single-journey ticket
            if (ItemUtils.ticketExpired(fareMedium)) {
                return ChatColor.RED + "Error: This single-journey ticket has already expired. Please use another ticket.";
            }

            String agencyIdOnTicket = StringUtils.getAgencyIdFromLore(fareMedium);
            if (!agencyIdOnTicket.equals(specifiedAgencyId)) {
                return ChatColor.RED + "Error: The agencyID on the ticket does not match with the one specified on the device.";
            }

            if (ItemManager.ticketUsed(fareMedium)) {
                return ChatColor.RED + "Error: This ticket has already been used.";
            }

            String originStationCode = StringUtils.getTicketOriginStationFromLore(fareMedium);
            if (!originStationCode.equals(specifiedStationCode)) {
                return ChatColor.RED + "Error: The origin station on the ticket does not match with the one specified on the device.";
            }

            // update the ticket used state to true
            ItemManager.updateTicketToUsedState(fareMedium);
            activeUser.sendMessage(ChatColor.GREEN + "The ticket is now activated.");


        } else if (fareMedium.getType() == Material.BOOK) {  // pass
            String passID = StringUtils.getCardIdFromLore(fareMedium);

            // CHECK IF THE PASS HAS ALREADY EXPIRED


//            if (ItemManager.passEnteredGate(passID)) {
//                return ChatColor.RED + "Error: This pass has been used to activate an entry device but not an exit device. You must use it to activate an exit device before using it again.";
//            }




        } else if (fareMedium.getType() == Material.IRON_NUGGET) {  // exit-only ticket
            return ChatColor.RED + "Error: Exit-only tickets can only be used with exit devices.";

        } else if (fareMedium.getType() == Material.TRIAL_KEY) {  // staff pass
            String agencyIdOnStaffPass = StringUtils.getAgencyIdFromLore(fareMedium);
            if (!agencyIdOnStaffPass.equals(specifiedAgencyId)) {
                return ChatColor.RED + "Error: The agencyID on the staff pass does not match with the one specified on the device.";
            }
        }

        if (deviceType.equalsIgnoreCase("gate")) {
            // activate the gate
        }

        return ChatColor.GREEN + "Entry " + deviceType + " activated.";
    }


}
