package me.ksmc.smartcardplugin.manager;

import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import me.ksmc.smartcardplugin.util.FormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Locale;

public class ItemManager {

    public static String issueSmartcard(Player player, String agencyID, String expiryFromLastUse) throws SQLException {
        if (expiryFromLastUse != null) {
            expiryFromLastUse = expiryFromLastUse.toLowerCase(Locale.ROOT);
            if (!FormatUtils.verifyTimeString(expiryFromLastUse)) {
                return ChatColor.RED + "Error: The card-expiry-from-last-use time input is invalid.";
            }
        }

        if (FareMediaDatabase.getFareMediaDatabase().createSmartCard(agencyID, expiryFromLastUse)) {
            ItemStack smartcard = new ItemStack(Material.NAME_TAG);
            player.getInventory().addItem(smartcard);
        } else {
            return ChatColor.RED + "Error: Database error.";
        }

        return ChatColor.GREEN + "The smartcard has been issued.";
    }

    public boolean confiscateTicket() {
        return false;
    }

}
