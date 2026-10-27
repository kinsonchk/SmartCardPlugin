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

public class EntryDeviceManager {

    public static String activateDevice(String deviceType, Sign deviceSign, Player activeUser, ItemStack fareMedium, String specifiedAgencyId, String specifiedStationCode) throws SQLException {

        if (fareMedium.getType() == Material.NAME_TAG) {  // smartcard
            String cardID = StringUtils.getCardOrPassIdFromLore(fareMedium);

            // CHECK IF THE CARD HAS EXPIRED ALREADY



            String cardBalanceRaw = ItemManager.getSmartcardBalance(cardID);
            if (cardBalanceRaw == null) {
                return ChatColor.RED + "Error: Database error.";
            }
            double cardCurrentBalance = Double.parseDouble(cardBalanceRaw);

            if (cardCurrentBalance <= 0) {
                return ChatColor.RED + "Error: The smartcard's balance is too low. Please add value to use it.";
            }

            if (FareMediaDatabase.getFareMediaDatabase().hasEntryRecord(cardID)) {
                String agencyIdOnCardEntryRecord = StringUtils.getAgencyIdOnCardEntryRecord(cardID);
                if (!agencyIdOnCardEntryRecord.equals(specifiedAgencyId)) {
                    return ChatColor.RED + "Error: This card has been used to activate an entry device of another agency. You must use it to activate a device of that same agency to use the card again, or have an admin reset the card's entry record.";
                }

                // Deduct surcharge/penalty fee from card for already having an entry record
                // NOTE: it uses the agencyID ON THE CARD'S ENTRY RECORD, NOT the agencyID that issued the card
                double cardPenaltyFee = ItemUtils.getCardPenaltyFee(agencyIdOnCardEntryRecord);
                if (!StringUtils.validMoneyString(String.valueOf(cardPenaltyFee))) {
                    return ChatColor.RED + "Error: The penalty fee amount input is invalid.";
                }

                double cardNewBalance = cardCurrentBalance - cardPenaltyFee;
                if (cardNewBalance <= 0) {
                    return ChatColor.RED + "Error: The smartcard's balance is too low. Please add value to use it.";
                }

                if (!FareMediaDatabase.getFareMediaDatabase().setCardBalance(cardID, cardNewBalance)) {
                    return ChatColor.RED + "Error: Database error.";
                }

                activeUser.sendMessage(ChatColor.GOLD + "$" + cardPenaltyFee + ChatColor.GREEN + " penalty fee has been deducted from the card.");
                EconomyManager.addAgencyRevenue(agencyIdOnCardEntryRecord, cardPenaltyFee);
            }

            FareMediaDatabase.getFareMediaDatabase().createEntryRecord(cardID, specifiedAgencyId, specifiedStationCode);
            activeUser.sendMessage(ChatColor.GREEN + "Smart card interaction successful.");


        } else if (fareMedium.getType() == Material.PAPER) {  // single-journey ticket
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
            activeUser.sendMessage(ChatColor.GREEN + "The ticket has been activated.");

        } else if (fareMedium.getType() == Material.BOOK) {  // pass



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
