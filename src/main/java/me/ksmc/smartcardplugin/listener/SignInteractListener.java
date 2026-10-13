package me.ksmc.smartcardplugin.listener;

import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import me.ksmc.smartcardplugin.manager.ItemManager;
import me.ksmc.smartcardplugin.manager.device.EntryDeviceManager;
import me.ksmc.smartcardplugin.manager.device.ExitDeviceManager;
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
            if (line2Args.length < 1) {
                return;
            }

            String agencyID = line2Args[0];
            if (!FareMediaDatabase.getFareMediaDatabase().agencyExists(agencyID)) {
                player.sendMessage(ChatColor.RED + "Error: Agency not found.");
                return;
            }

            if (lines[0].equals("[Tickets]")) {
                event.setCancelled(true);

                String currentStation = null;
                if (line2Args.length > 1) {
                    currentStation = line2Args[1];
                }

                // open ticket machine menu
                //openMenu(agencyID, currentStation);

                player.sendMessage(ChatColor.GREEN + "TICKET SUCCESS!");

            } else {
                if (ItemManager.isValidItem(item)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.GREEN + "DEVICE SUCCESS!");

                    switch (lines[0]) {
                        case "[Entry Gate]", "[Entry Validator]", "[Exit Gate]", "[Exit Validator]": {
                            String stationCode = null;
                            String additionalMoney = null;
                            if (line2Args.length > 1) {
                                stationCode = line2Args[1];
                            }
                            if (line2Args.length > 2) {
                                additionalMoney = line2Args[2];
                            }

                            switch (lines[0]) {
                                case "[Entry Gate]" ->
                                        player.sendMessage(EntryDeviceManager.activateEntryDevice("gate", sign, item, agencyID, stationCode, additionalMoney));
                                case "[Entry Validator]" ->
                                        player.sendMessage(EntryDeviceManager.activateEntryDevice("validator", sign, item, agencyID, stationCode, additionalMoney));
                                case "[Exit Gate]" ->
                                        player.sendMessage(ExitDeviceManager.activateExitDevice("gate", sign, item, agencyID, stationCode, additionalMoney));
                                case "[Exit Validator]" ->
                                        player.sendMessage(ExitDeviceManager.activateExitDevice("validator", sign, item, agencyID, stationCode, additionalMoney));
                            }

                            break;
                        }

                        case "[Pay Gate]", "[Pay Validator]": {
                            String moneyAmount = null;
                            String stationCode = null;
                            if (line2Args.length > 1) {
                                moneyAmount = line2Args[1];
                            }
                            if (line2Args.length > 2) {
                                stationCode = line2Args[2];
                            }

//                            switch (lines[0]) {
//                                case "[Pay Gate]" ->
//                                        player.sendMessage(EntryDeviceManager.activateEntryDevice("gate", sign, item, agencyID, stationCode, additionalMoney));
//                                case "[Pay Validator]" ->
//                                        player.sendMessage(EntryDeviceManager.activateEntryDevice("validator", sign, item, agencyID, stationCode, additionalMoney));
//                            }

                            break;
                        }

                        case "[Staff Gate]", "[Staff Validator]": {
//                            switch (lines[0]) {
//                                case "[Staff Gate]" ->
//                                        player.sendMessage(EntryDeviceManager.activateEntryDevice("gate", sign, item, agencyID, stationCode, additionalMoney));
//                                case "[Staff Validator]" ->
//                                        player.sendMessage(EntryDeviceManager.activateEntryDevice("validator", sign, item, agencyID, stationCode, additionalMoney));
//                            }

                            break;
                        }

                    }

                }

            }

        }


    }

}
