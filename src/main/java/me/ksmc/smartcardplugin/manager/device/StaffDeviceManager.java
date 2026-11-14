package me.ksmc.smartcardplugin.manager.device;

import me.ksmc.smartcardplugin.manager.device.type.GateManager;
import me.ksmc.smartcardplugin.util.DeviceUtils;
import me.ksmc.smartcardplugin.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StaffDeviceManager {

    public static String activateDevice(String deviceType, Sign deviceSign, Player activeUser, ItemStack fareMedium, String specifiedAgencyId) {

        if (fareMedium.getType() != Material.TRIAL_KEY) {
            DeviceUtils.playErrorSound(activeUser);
            return ChatColor.RED + "Error: Staff devices must be activated with a staff pass.";
        }

        String agencyIdOnStaffPass = StringUtils.getAgencyIdFromLore(fareMedium);
        if (!agencyIdOnStaffPass.equals(specifiedAgencyId)) {
            DeviceUtils.playErrorSound(activeUser);
            return ChatColor.RED + "Error: The agencyID on the staff pass does not match with the one specified on the device.";
        }

        if (deviceType.equalsIgnoreCase("gate")) {
            // activate the gate
            GateManager.activateGate(deviceSign);
        }

        DeviceUtils.playSuccessSound(activeUser);
        return ChatColor.GREEN + "Staff " + deviceType + " activated.";
    }

}
