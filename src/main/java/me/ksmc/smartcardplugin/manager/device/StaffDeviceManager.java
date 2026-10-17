package me.ksmc.smartcardplugin.manager.device;

import me.ksmc.smartcardplugin.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

public class StaffDeviceManager {

    public static String activateDevice(String deviceType, Sign deviceSign, ItemStack fareMedium, String specifiedAgencyId) {

        if (fareMedium.getType() != Material.TRIAL_KEY) {
            return ChatColor.RED + "Error: Staff devices must be activated with a staff pass.";
        }

        String agencyIdOnStaffPass = StringUtils.getStaffPassAgencyIdFromLore(fareMedium);
        if (!agencyIdOnStaffPass.equals(specifiedAgencyId)) {
            return ChatColor.RED + "Error: The agencyID on the staff pass does not match with the one specified on the device.";
        }

        if (deviceType.equalsIgnoreCase("gate")) {
            // activate the gate
        }

        return ChatColor.GREEN + "Staff " + deviceType + " activated.";
    }

}
