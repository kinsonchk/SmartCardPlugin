package me.ksmc.smartcardplugin.manager.device.type;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class GateManager {


    public static void activateGate(Sign deviceSign) {

        Block blockToChange = deviceSign.getLocation().subtract(0, 1 ,0).getBlock();
        blockToChange.setType(Material.EMERALD_BLOCK);

    }

}
