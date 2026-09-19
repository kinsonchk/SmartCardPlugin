package me.ksmc.smartCardPlugin;

import me.ksmc.smartCardPlugin.Commands.SmartcardCommand;
import me.ksmc.smartCardPlugin.Database.FareMediaDatabase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Main extends JavaPlugin {

    private FareMediaDatabase fareMediaDatabase;
    // Database getter
    public FareMediaDatabase getFareMediaDatabase() {
        return this.fareMediaDatabase;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Creates the database .db file (if not already created yet)
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            getServer().getConsoleSender().sendMessage("Database not found, creating a new one...");
            fareMediaDatabase = new FareMediaDatabase(getDataFolder().getAbsolutePath() + "/fare_media.db");

        } catch (SQLException e) {
            e.printStackTrace();
            getServer().getConsoleSender().sendMessage("Failed to connect to the database! " + e.getMessage());

            // Disable the plugin if couldn't connect to the database
            Bukkit.getPluginManager().disablePlugin(this);
        }

        getCommand("smartcard").setExecutor(new SmartcardCommand());

        getServer().getConsoleSender().sendMessage("Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        // Close database connection
        try {
            fareMediaDatabase.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getServer().getConsoleSender().sendMessage("Plugin has been disabled!");
    }
}
