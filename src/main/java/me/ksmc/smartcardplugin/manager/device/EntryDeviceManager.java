package me.ksmc.smartcardplugin.manager.device;

import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import me.ksmc.smartcardplugin.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class EntryDeviceManager {

    public static String activateDevice(String deviceType, Sign deviceSign, ItemStack fareMedium, String specifiedAgencyId, String stationCode, String additionalMoney) throws SQLException {

        if (fareMedium.getType() == Material.NAME_TAG) {  // smartcard
            String cardID = StringUtils.getCardIdFromLore(fareMedium);

//            String cardBalanceRaw = ItemManager.getSmartcardBalance(cardID);
//            if (cardBalanceRaw == null) {
//                return ChatColor.RED + "Error: Database error.";
//            }
//            double cardBalance = Double.parseDouble(cardBalanceRaw);
//            if (cardBalance <= 0) {
//                return ChatColor.RED + "Error: Smartcard's balance is too low.";
//            }

            FareMediaDatabase.getFareMediaDatabase().createEntryRecord(cardID, specifiedAgencyId, stationCode);

        } else if (fareMedium.getType() == Material.PAPER) {  // single-journey ticket

        } else if (fareMedium.getType() == Material.BOOK) {  // pass

        } else if (fareMedium.getType() == Material.IRON_NUGGET) {  // exit-only ticket

        } else if (fareMedium.getType() == Material.TRIAL_KEY) {  // staff pass
            String agencyIdOnStaffPass = StringUtils.getStaffPassAgencyIdFromLore(fareMedium);
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
