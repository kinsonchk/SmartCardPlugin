package me.ksmc.smartcardplugin;

import me.ksmc.smartcardplugin.command.SmartcardCommand;
import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public final class Main extends JavaPlugin {

    private FareMediaDatabase fareMediaDatabase;

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Creates the folder for storing plugin files (if not already created)
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Copies the config.yml file (if not already exists in server plugin directory)
        saveDefaultConfig();

        // Copies the sample fare chart CSV files in fare_charts directory to server plugin directory
        saveResource("fare_charts/mtr_sample.csv", false);
        saveResource("fare_charts/mtr_lrt_sample.csv", false);

        // Creates the database .db file (if not already created yet) and connect to it
        try {
            fareMediaDatabase = new FareMediaDatabase(getDataFolder().getAbsolutePath() + "/fare_media.db");
        } catch (SQLException e) {
            e.printStackTrace();
            getLogger().log(Level.SEVERE, "Failed to load fare_media database! " + e.getMessage());

            // Disable the plugin if couldn't connect to the database
            Bukkit.getPluginManager().disablePlugin(this);
        }


        getCommand("smartcard").setExecutor(new SmartcardCommand());

        getServer().getConsoleSender().sendMessage("[Smartcard] Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        // Close database connection
        if (fareMediaDatabase != null) {
            try {
                fareMediaDatabase.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        getServer().getConsoleSender().sendMessage("[Smartcard] Plugin has been disabled!");
    }
}
