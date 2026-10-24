package me.ksmc.smartcardplugin.manager.device;

import me.ksmc.smartcardplugin.manager.ItemManager;
import me.ksmc.smartcardplugin.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExitDeviceManager {

    public static String activateDevice(String deviceType, Sign deviceSign, Player activeUser, ItemStack fareMedium, String specifiedAgencyId, String specifiedStationCode) {

        if (fareMedium.getType() == Material.NAME_TAG) {  // smartcard



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
