package me.ksmc.smartCardPlugin;

import me.ksmc.smartCardPlugin.Commands.SmartcardCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("Plugin has been enabled!");

        getCommand("smartcard").setExecutor(new SmartcardCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Plugin has been disabled!");
    }
}
