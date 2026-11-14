package me.ksmc.smartcardplugin.manager.device.type;

import me.ksmc.smartcardplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.HashSet;
import java.util.Set;

public class GateManager {
    private static final Set<Location> activeGates = new HashSet<>();

    public boolean gateCurrentlyInUse(Sign deviceSign) {
        return activeGates.contains(deviceSign.getLocation());
    }

    public void activateGate(Sign deviceSign) {
        activeGates.add(deviceSign.getLocation());

        Block blockToChange = deviceSign.getLocation().subtract(0, 1 ,0).getBlock();
        blockToChange.setType(Material.EMERALD_BLOCK);

        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
            @Override
            public void run() {
                deactivateGate(deviceSign);
            }
        }, 40);  // open the gate for 40 ticks = 2 seconds
    }

    public void deactivateGate(Sign deviceSign) {
        activeGates.remove(deviceSign.getLocation());

        Block blockToChange = deviceSign.getLocation().subtract(0, 1 ,0).getBlock();
        blockToChange.setType(Material.REDSTONE_BLOCK);
    }


    // This will open the other iron trapdoor adjacent to the "main" iron trapdoor of a wide gate
    public static void triggerAdjacentIronTrapdoor(Block sourceIronTrapdoor) {

    }

}
