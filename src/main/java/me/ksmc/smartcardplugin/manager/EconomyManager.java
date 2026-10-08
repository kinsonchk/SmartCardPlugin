package me.ksmc.smartcardplugin.manager;

import me.ksmc.smartcardplugin.dependency.VaultAPI;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public class EconomyManager {
    private static EconomyManager instance;
    private static final Economy economy = VaultAPI.getEconomy();

    public static EconomyManager getEconomy() {
        return instance;
    }

    public String lookupBalance(Player player) {
        return economy.format(economy.getBalance(player));
    }

    public boolean deductPlayerBalance(Player player, double amount) {
        EconomyResponse response = economy.withdrawPlayer(player, amount);
        if (response.transactionSuccess()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean addPlayerBalance(Player player, double amount) {
        EconomyResponse response = economy.depositPlayer(player, amount);
        if (response.transactionSuccess()) {
            return true;
        } else {
            return false;
        }
    }

}
