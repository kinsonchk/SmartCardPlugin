package me.ksmc.smartcardplugin.listener;

import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import me.ksmc.smartcardplugin.manager.ItemManager;
import me.ksmc.smartcardplugin.manager.device.EntryDeviceManager;
import me.ksmc.smartcardplugin.manager.device.ExitDeviceManager;
import me.ksmc.smartcardplugin.manager.device.PayDeviceManager;
import me.ksmc.smartcardplugin.manager.device.StaffDeviceManager;
import me.ksmc.smartcardplugin.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class SignInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) throws SQLException {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (event.getClickedBlock().getState() instanceof Sign sign) {
            String[] lines = sign.getSide(Side.FRONT).getLines();

            String[] line2Args = lines[1].split(" ");
            String agencyID = line2Args[0];

            if (lines[0].equals("[Tickets]")) {
                if (!FareMediaDatabase.getFareMediaDatabase().agencyExists(agencyID)) {
                    player.sendMessage(ChatColor.RED + "Error: Agency not found.");
                    return;
                }
                event.setCancelled(true);

                String currentStation = null;
                if (line2Args.length > 1) {
                    currentStation = line2Args[1];
                }

                // open ticket machine menu
                //openMenu(agencyID, currentStation);

                player.sendMessage(ChatColor.GREEN + "TICKET SUCCESS!");

            } else {
                if (ItemManager.isValidFareMedium(item)) {
                    if (!FareMediaDatabase.getFareMediaDatabase().agencyExists(agencyID)) {
                        player.sendMessage(ChatColor.RED + "Error: Agency not found.");
                        return;
                    }
                    event.setCancelled(true);

                    switch (lines[0]) {
                        case "[Entry Gate]", "[Entry Validator]", "[Exit Gate]", "[Exit Validator]": {
                            String stationCode;
                            String additionalMoney;
                            if (line2Args.length > 1) {
                                stationCode = line2Args[1];
                            } else {
                                player.sendMessage(ChatColor.RED + "Error: Station code not specified.");
                                return;
                            }
                            if (line2Args.length > 2) {
                                additionalMoney = line2Args[2];
                                if (!StringUtils.validMoneyString(additionalMoney)) {
                                    player.sendMessage(ChatColor.RED + "Error: The money amount string is invalid.");
                                    return;
                                }
                            }

                            switch (lines[0]) {
                                case "[Entry Gate]" ->
                                        player.sendMessage(EntryDeviceManager.activateDevice("gate", sign, player, item, agencyID, stationCode));
                                case "[Entry Validator]" ->
                                        player.sendMessage(EntryDeviceManager.activateDevice("validator", sign, player, item, agencyID, stationCode));
                                case "[Exit Gate]" ->
                                        player.sendMessage(ExitDeviceManager.activateDevice("gate", sign, player, item, agencyID, stationCode));
                                case "[Exit Validator]" ->
                                        player.sendMessage(ExitDeviceManager.activateDevice("validator", sign, player, item, agencyID, stationCode));
                            }

                            break;
                        }

                        case "[Pay Gate]", "[Pay Validator]": {
                            String moneyAmount = null;
                            String stationCode = null;
                            if (line2Args.length > 1) {
                                moneyAmount = line2Args[1];
                                if (!StringUtils.validMoneyString(moneyAmount)) {
                                    player.sendMessage(ChatColor.RED + "Error: The money amount string is invalid.");
                                    return;
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Error: Station code not specified.");
                                return;
                            }
                            if (line2Args.length > 2) {
                                stationCode = line2Args[2];
                            }

                            switch (lines[0]) {
                                case "[Pay Gate]" ->
                                        player.sendMessage(PayDeviceManager.activateDevice("gate", sign, player, item, agencyID, moneyAmount, stationCode));
                                case "[Pay Validator]" ->
                                        player.sendMessage(PayDeviceManager.activateDevice("validator", sign, player, item, agencyID, moneyAmount, stationCode));
                            }

                            break;
                        }

                        case "[Staff Gate]", "[Staff Validator]": {
                            switch (lines[0]) {
                                case "[Staff Gate]" ->
                                        player.sendMessage(StaffDeviceManager.activateDevice("gate", sign, item, agencyID));
                                case "[Staff Validator]" ->
                                        player.sendMessage(StaffDeviceManager.activateDevice("validator", sign, item, agencyID));
                            }

                            break;
                        }

                    }

                }

            }

        }


    }

}
