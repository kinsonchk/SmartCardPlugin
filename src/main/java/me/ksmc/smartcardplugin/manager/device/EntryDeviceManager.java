package me.ksmc.smartcardplugin.manager.device;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

public class EntryDeviceManager {

    public static String activateEntryDevice(String deviceType, Sign deviceSign, ItemStack fareMedium, String agencyID, String stationCode, String additionalMoney) {
        if (fareMedium.getType() == Material.NAME_TAG) {  // smartcard

            String cardID = fareMedium.getItemMeta().getLore().get(0);

            

        } else if (fareMedium.getType() == Material.PAPER) {  // single-journey ticket

        } else if (fareMedium.getType() == Material.BOOK) {  // pass

        } else if (fareMedium.getType() == Material.IRON_NUGGET) {  // exit-only ticket

        } else if (fareMedium.getType() == Material.TRIAL_KEY) {  // staff pass



        }




        if (deviceType.equalsIgnoreCase("gate")) {
            // activate the gate
        }

        return null;
    }


}
