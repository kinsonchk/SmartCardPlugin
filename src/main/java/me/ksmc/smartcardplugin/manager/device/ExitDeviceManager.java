package me.ksmc.smartcardplugin.manager.device;

import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import me.ksmc.smartcardplugin.manager.EconomyManager;
import me.ksmc.smartcardplugin.manager.ItemManager;
import me.ksmc.smartcardplugin.manager.device.type.GateManager;
import me.ksmc.smartcardplugin.util.DeviceUtils;
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
            String cardID = StringUtils.getCardIdFromLore(fareMedium);
            String agencyIdOnCard = FareMediaDatabase.getFareMediaDatabase().getAgencyIdOnCard(cardID);

            if (ItemUtils.cardExpired(cardID)) {
                DeviceUtils.playErrorSound(activeUser);
                return ChatColor.RED + "Error: This smart card has already expired. Please use another card.";
            }

            if (!ItemUtils.cardCanBeUsedWithSpecifiedAgency(agencyIdOnCard, specifiedAgencyId)) {
                DeviceUtils.playErrorSound(activeUser);
                return ChatColor.RED + "Error: This card cannot be used with the specified agency.";
            }

            String cardBalanceRaw = ItemManager.getSmartcardBalance(cardID);
            if (cardBalanceRaw == null) {
                DeviceUtils.playErrorSound(activeUser);
                return ChatColor.RED + "Error: Database error.";
            }
            double cardCurrentBalance = Double.parseDouble(cardBalanceRaw);

            if (FareMediaDatabase.getFareMediaDatabase().hasEntryRecord(cardID)) {
                String agencyIdOnCardEntryRecord = StringUtils.getAgencyIdOnCardEntryRecord(cardID);
                if (!agencyIdOnCardEntryRecord.equals(specifiedAgencyId)) {
                    DeviceUtils.playErrorSound(activeUser);
                    return ChatColor.RED + "Error: This card has been used to activate an entry device of another agency. You must use it to activate a device of that same agency to use the card again, or have an admin reset the card's entry record.";
                }
            }

            double totalFare = 0;

            // NOTE: the penalty fee here is that of the agencyID specified on the device sign, NOT the agencyID of the card
            if (!FareMediaDatabase.getFareMediaDatabase().hasEntryRecord(cardID)) {
                double cardPenaltyFee = ItemUtils.getCardPenaltyFee(specifiedAgencyId);
                if (!StringUtils.validMoneyString(String.valueOf(cardPenaltyFee))) {
                    DeviceUtils.playErrorSound(activeUser);
                    return ChatColor.RED + "Error: The penalty fee amount input is invalid.";
                }

                double cardNewBalance = cardCurrentBalance - cardPenaltyFee;
                if (!FareMediaDatabase.getFareMediaDatabase().setCardBalance(cardID, cardNewBalance)) {
                    DeviceUtils.playErrorSound(activeUser);
                    return ChatColor.RED + "Error: Database error.";
                }

                totalFare = cardPenaltyFee;

                activeUser.sendMessage(ChatColor.GOLD + "$" + cardPenaltyFee + ChatColor.GREEN + " penalty fee has been deducted from the card.");
                EconomyManager.addAgencyRevenue(specifiedAgencyId, cardPenaltyFee);

            } else {
                double penaltyFareFromEntryRecord = Double.parseDouble(StringUtils.getPenaltyFareOnCardEntryRecord(cardID));

                // TODO: GET TRIP FARE FROM FARE CHART
                String originStation = StringUtils.getOriginStationOnCardEntryRecord(cardID);

                String fareRaw = "15.3";  // TEST FOR NOW -- GET FARE FROM EXCEL FILE
                if (!StringUtils.validMoneyString(fareRaw)) {
                    DeviceUtils.playErrorSound(activeUser);
                    return ChatColor.RED + "Error: The fare amount input for this trip is invalid.";
                }
                double tripFare = Double.parseDouble(fareRaw);

                double cardNewBalance = cardCurrentBalance - tripFare - penaltyFareFromEntryRecord;

                if (!FareMediaDatabase.getFareMediaDatabase().setCardBalance(cardID, cardNewBalance)) {
                    DeviceUtils.playErrorSound(activeUser);
                    return ChatColor.RED + "Error: Database error.";
                }

                totalFare = tripFare + penaltyFareFromEntryRecord;

                activeUser.sendMessage(ChatColor.GOLD + "$" + totalFare + ChatColor.GREEN + " fare has been deducted from the card.");
                EconomyManager.addAgencyRevenue(specifiedAgencyId, totalFare);
            }

            // CREATE TRANSACTION RECORD
            // note: the penalty fare from entry record (if involved) is passed onto the transaction record
            FareMediaDatabase.getFareMediaDatabase().createCardTransactionRecord(cardID, specifiedAgencyId, String.valueOf(totalFare));
            ItemManager.resetSmartcardEntryExitRecord(cardID);

            FareMediaDatabase.getFareMediaDatabase().updateCardLastUsedTimestamp(cardID);
            activeUser.sendMessage(ChatColor.GREEN + "Smart card interaction successful.");


        } else if (fareMedium.getType() == Material.PAPER) {  // single-journey ticket
            // no need to check if the ticket has already expired since it can only be used once anyways

            String agencyIdOnTicket = StringUtils.getAgencyIdFromLore(fareMedium);
            if (!agencyIdOnTicket.equals(specifiedAgencyId)) {
                DeviceUtils.playErrorSound(activeUser);
                return ChatColor.RED + "Error: The agencyID on the ticket does not match with the one specified on the device.";
            }

            if (!ItemManager.ticketUsed(fareMedium)) {
                DeviceUtils.playErrorSound(activeUser);
                return ChatColor.RED + "Error: This ticket has not yet been used.";
            }

            String destinationStationCode = StringUtils.getTicketDestinationStationFromLore(fareMedium);
            if (!destinationStationCode.equals(specifiedStationCode)) {
                DeviceUtils.playErrorSound(activeUser);
                return ChatColor.RED + "Error: The destination station on the ticket does not match with the one specified on the device.";
            }

            // confiscate the ticket
            ItemManager.confiscateTicket(activeUser, fareMedium);
            activeUser.sendMessage(ChatColor.GREEN + "The ticket has been confiscated.");

        } else if (fareMedium.getType() == Material.BOOK) {  // pass
            String passID = StringUtils.getPassIdFromLore(fareMedium);
            String agencyIdOnPass = FareMediaDatabase.getFareMediaDatabase().getAgencyIdOnPass(passID);
            String passType = FareMediaDatabase.getFareMediaDatabase().getPassTypeOnPass(passID);

            if (ItemUtils.passExpired(passID)) {
                DeviceUtils.playErrorSound(activeUser);
                return ChatColor.RED + "Error: The pass has already expired. Please use another pass.";
            }

            if (!ItemManager.passEnteredGate(passID)) {
                DeviceUtils.playErrorSound(activeUser);
                return ChatColor.RED + "Error: This pass has not been used to activate an entry device. You must use it to activate an entry device before using it again.";
            }

            if (!ItemUtils.passCanBeUsedWithSpecifiedAgency(agencyIdOnPass, specifiedAgencyId, passType)) {
                DeviceUtils.playErrorSound(activeUser);
                return ChatColor.RED + "Error: This pass cannot be used with the specified agency.";
            }

            if (!ItemUtils.passValidForSpecifiedStation(agencyIdOnPass, specifiedStationCode, passType)) {
                DeviceUtils.playErrorSound(activeUser);
                return ChatColor.RED + "Error: This pass is not valid for the specified station.";
            }

            ItemManager.changePassEnteredGateState(passID);
            activeUser.sendMessage(ChatColor.GREEN + "The pass is accepted.");


        } else if (fareMedium.getType() == Material.IRON_NUGGET) {  // exit-only ticket
            if (ItemUtils.exitOnlyTicketExpired(fareMedium)) {
                DeviceUtils.playErrorSound(activeUser);
                return ChatColor.RED + "Error: This exit-only ticket has already expired. Please use another one.";
            }

            String agencyIdOnTicket = StringUtils.getAgencyIdFromLore(fareMedium);
            if (!agencyIdOnTicket.equals(specifiedAgencyId)) {
                DeviceUtils.playErrorSound(activeUser);
                return ChatColor.RED + "Error: The agencyID on the exit-only ticket does not match with the one specified on the device.";
            }

            // confiscate the ticket
            ItemManager.confiscateTicket(activeUser, fareMedium);
            activeUser.sendMessage(ChatColor.GREEN + "The exit-only ticket has been confiscated.");

        } else if (fareMedium.getType() == Material.TRIAL_KEY) {  // staff pass
            String agencyIdOnStaffPass = StringUtils.getAgencyIdFromLore(fareMedium);
            if (!agencyIdOnStaffPass.equals(specifiedAgencyId)) {
                DeviceUtils.playErrorSound(activeUser);
                return ChatColor.RED + "Error: The agencyID on the staff pass does not match with the one specified on the device.";
            }
        }

        if (deviceType.equalsIgnoreCase("gate")) {
            // activate the gate
            GateManager.activateGate(deviceSign);
        }

        DeviceUtils.playSuccessSound(activeUser);
        return ChatColor.GREEN + "Exit " + deviceType + " activated.";
    }

}
