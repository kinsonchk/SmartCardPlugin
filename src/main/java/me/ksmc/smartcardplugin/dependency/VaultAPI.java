package me.ksmc.smartcardplugin.dependency;

import me.ksmc.smartcardplugin.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultAPI {
    private static Economy econ = null;

    public static void initializeVault() {
        if (!setupEconomy() ) {
            Main.getPlugin().getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", Main.getPlugin().getDescription().getName()));
            Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
        }
    }

    private static boolean setupEconomy() {
        if (Main.getPlugin().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Main.getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

}
