package me.ksmc.smartcardplugin.manager.device;

import me.ksmc.smartcardplugin.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

public class PayDeviceManager {

    public static String activateDevice(String deviceType, Sign deviceSign, ItemStack fareMedium, String specifiedAgencyId, String moneyAmount, String stationCode) {

        if (fareMedium.getType() == Material.NAME_TAG) {  // smartcard

        } else if (fareMedium.getType() == Material.PAPER) {  // single-journey ticket

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

        return ChatColor.GREEN + "Pay " + deviceType + " activated.";
    }
}
