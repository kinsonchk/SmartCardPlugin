package me.ksmc.smartcardplugin;

import me.ksmc.smartcardplugin.command.SmartcardCommand;
import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import me.ksmc.smartcardplugin.dependency.VaultAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    // Allows passing the plugin instance to other classes
    private static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;

        // Plugin startup logic

        // Copies the config.yml file (if not already exists in server plugin directory)
        saveDefaultConfig();

        // Creates the folder for storing plugin files (if not already created)
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Copies the sample fare chart CSV files in fare_charts directory to server plugin directory
        saveResource("fare_charts/mtr_sample.csv", false);
        saveResource("fare_charts/mtr_lrt_sample.csv", false);

        FareMediaDatabase.initializeDatabase();

        VaultAPI.initializeVault();


        getCommand("smartcard").setExecutor(new SmartcardCommand());

        getServer().getConsoleSender().sendMessage("[Smartcard] Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        FareMediaDatabase.disableDatabase();


        getServer().getConsoleSender().sendMessage("[Smartcard] Plugin has been disabled!");
    }

    public static Main getPlugin() {
        return plugin;
    }
}
