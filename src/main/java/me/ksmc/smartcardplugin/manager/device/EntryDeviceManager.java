package me.ksmc.smartcardplugin.manager.device;

import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import me.ksmc.smartcardplugin.manager.ItemManager;
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

//            String cardBalanceRaw = ItemManager.getSmartcardBalance(cardID);
//            if (cardBalanceRaw == null) {
//                return ChatColor.RED + "Error: Database error.";
//            }
//            double cardBalance = Double.parseDouble(cardBalanceRaw);
//            if (cardBalance <= 0) {
//                return ChatColor.RED + "Error: Smartcard's balance is too low.";
//            }

            FareMediaDatabase.getFareMediaDatabase().createEntryRecord(cardID, specifiedAgencyId, specifiedStationCode);

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
