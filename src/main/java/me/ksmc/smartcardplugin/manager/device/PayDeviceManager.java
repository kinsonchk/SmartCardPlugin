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
import java.time.Instant;

public class PayDeviceManager {

    public static String activateDevice(String deviceType, Sign deviceSign, Player activeUser, ItemStack fareMedium, String specifiedAgencyId, String moneyAmount, String specifiedStationCode) throws SQLException {

        if (fareMedium.getType() == Material.NAME_TAG) {  // smartcard
            String cardID = StringUtils.getCardIdFromLore(fareMedium);
            String agencyIdOnCard = FareMediaDatabase.getFareMediaDatabase().getAgencyIdOnCard(cardID);

            if (ItemUtils.cardExpired(cardID)) {
                return ChatColor.RED + "Error: This smart card has already expired. Please use another card.";
            }

            if (!ItemUtils.cardCanBeUsedWithSpecifiedAgency(agencyIdOnCard, specifiedAgencyId)) {
                return ChatColor.RED + "Error: This card cannot be used with the specified agency.";
            }

            if (!StringUtils.validMoneyString(moneyAmount)) {
                return ChatColor.RED + "Error: The money amount specified on the sign is invalid.";
            }
            double fare = Double.parseDouble(moneyAmount);

            String cardBalanceRaw = ItemManager.getSmartcardBalance(cardID);
            if (cardBalanceRaw == null) {
                return ChatColor.RED + "Error: Database error.";
            }
            double cardCurrentBalance = Double.parseDouble(cardBalanceRaw);

            double cardNewBalance = cardCurrentBalance - fare;
            if (cardCurrentBalance <= 0 || cardNewBalance <= 0) {
                return ChatColor.RED + "Error: The smartcard's balance is too low. Please add value to use it.";
            }

            if (!FareMediaDatabase.getFareMediaDatabase().setCardBalance(cardID, cardNewBalance)) {
                return ChatColor.RED + "Error: Database error.";
            }

            activeUser.sendMessage(ChatColor.GOLD + "$" + fare + ChatColor.GREEN + " fare has been deducted from the card.");
            EconomyManager.addAgencyRevenue(specifiedAgencyId, fare);

            FareMediaDatabase.getFareMediaDatabase().createCardTransactionRecord(cardID, specifiedAgencyId, String.valueOf(fare));
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

            String originStationCode = StringUtils.getTicketOriginStationFromLore(fareMedium);
            String destinationStationCode = StringUtils.getTicketDestinationStationFromLore(fareMedium);
            if (!originStationCode.equals(specifiedStationCode) && !destinationStationCode.equals(specifiedStationCode)) {
                return ChatColor.RED + "Error: The origin or destination station on the ticket does not match with the one specified on the device.";
            }

            if (!ItemManager.ticketUsed(fareMedium)) {
                // update the ticket used state to true
                ItemManager.updateTicketToUsedState(fareMedium);
                activeUser.sendMessage(ChatColor.GREEN + "The ticket is now activated.");

            } else {
                // confiscate the ticket
                ItemManager.confiscateTicket(activeUser, fareMedium);
                activeUser.sendMessage(ChatColor.GREEN + "The ticket has been confiscated.");
            }


        } else if (fareMedium.getType() == Material.BOOK) {  // pass
            String passID = StringUtils.getPassIdFromLore(fareMedium);
            String agencyIdOnPass = FareMediaDatabase.getFareMediaDatabase().getAgencyIdOnPass(passID);
            String passType = FareMediaDatabase.getFareMediaDatabase().getPassTypeOnPass(passID);
            int tripsRemaining = FareMediaDatabase.getFareMediaDatabase().getTripsRemainingOnPass(passID);

            if (ItemUtils.passExpired(passID)) {
                return ChatColor.RED + "Error: The pass has already expired. Please use another pass.";
            }

            if (tripsRemaining != -1 && tripsRemaining <= 0) {  // -1 means unlimited trips
                return ChatColor.RED + "Error: There are no more valid trips remaining on this pass. Please use another pass.";
            }

            if (!ItemUtils.passCanBeUsedWithSpecifiedAgency(agencyIdOnPass, specifiedAgencyId, passType)) {
                return ChatColor.RED + "Error: This pass cannot be used with the specified agency.";
            }

            if (specifiedStationCode.isBlank()) {
                return ChatColor.RED + "Error: You cannot use this pay device since no station is specified on it.";
            }

            if (!ItemUtils.passValidForSpecifiedStation(agencyIdOnPass, specifiedStationCode, passType)) {
                return ChatColor.RED + "Error: This pass is not valid for the specified station.";
            }

            // Deduct one trip from the pass (if applicable) upon successful device activation
            if (tripsRemaining != -1) {
                int newTripsRemaining = tripsRemaining - 1;

                if (!FareMediaDatabase.getFareMediaDatabase().updateTripsRemainingOnPass(passID, newTripsRemaining)) {
                    return ChatColor.RED + "Error: Database error.";
                }
            }

            activeUser.sendMessage(ChatColor.GREEN + "The pass is accepted.");


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

        return ChatColor.GREEN + "Pay " + deviceType + " activated.";
    }
}
