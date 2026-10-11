package me.ksmc.smartcardplugin.manager;

import me.ksmc.smartcardplugin.Main;
import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import me.ksmc.smartcardplugin.dependency.VaultAPI;
import me.ksmc.smartcardplugin.util.StringUtils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class EconomyManager {
    private static EconomyManager instance;
    private static final Economy economy = VaultAPI.getEconomy();

    public static EconomyManager getEconomy() {
        return instance;
    }

    public String lookupPlayerBalance(Player player) {
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

    public static boolean addPlayerBalance(Player player, double amount) {
        EconomyResponse response = economy.depositPlayer(player, amount);
        if (response.transactionSuccess()) {
            return true;
        } else {
            return false;
        }
    }


    public static String lookupAgencyRevenue(String agencyID) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().agencyExists(agencyID)) {
            return ChatColor.RED + "Error: Agency not found.";
        }

        Object revenue = FareMediaDatabase.getFareMediaDatabase().getAgencyRevenue(agencyID);
        if (revenue == null) {
            return ChatColor.RED + "Error: Database error.";
        }

        String companyName = Main.getPlugin().getConfig().getString(agencyID + ".company-name");

        return ChatColor.GREEN + companyName + "'s current revenue: " + ChatColor.GOLD + "$" + revenue;
    }

    public static String depositAgencyRevenueToPlayer(String agencyID, String moneyAmount, String playerName) throws SQLException {
        if (!FareMediaDatabase.getFareMediaDatabase().agencyExists(agencyID)) {
            return ChatColor.RED + "Error: Agency not found.";
        }

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            return ChatColor.RED + "Error: Player not found/online.";
        }

        if (!StringUtils.verifyMoneyString(moneyAmount)) {
            return ChatColor.RED + "Error: The money amount input is invalid.";
        }

        double moneyAmountToDeposit;
        try {
            moneyAmountToDeposit = Double.parseDouble(moneyAmount);
            if (moneyAmountToDeposit <= 0) {
                return ChatColor.RED + "Error: The money amount must be greater than 0.";
            }
        } catch (NumberFormatException e) {
            return ChatColor.RED + "Error: The money amount input is invalid.";
        }

        Object agencyCurrentRevenueString = FareMediaDatabase.getFareMediaDatabase().getAgencyRevenue(agencyID);
        if (agencyCurrentRevenueString == null) {
            return ChatColor.RED + "Error: Database error.";
        }
        double agencyCurrentRevenue = Double.parseDouble(agencyCurrentRevenueString.toString());
        if (agencyCurrentRevenue < moneyAmountToDeposit) {
            return ChatColor.RED + "Error: The agency has insufficient funds for withdrawal.";
        }

        // MUST MAINTAIN ATOMICITY DURING MONEY TRANSACTION!!!
        double agencyNewRevenue = agencyCurrentRevenue - moneyAmountToDeposit;

        if (!FareMediaDatabase.getFareMediaDatabase().setAgencyRevenue(agencyID, agencyNewRevenue)) {
            return ChatColor.RED + "Error: Database error.";
        }

        if (!addPlayerBalance(target, moneyAmountToDeposit)) {
            FareMediaDatabase.getFareMediaDatabase().setAgencyRevenue(agencyID, agencyCurrentRevenue);  // REVERT IF TRANSACTION FAILED
            return ChatColor.RED + "Error: Transaction error. No money transferred.";
        }

        String companyName = Main.getPlugin().getConfig().getString(agencyID + ".company-name");

        return ChatColor.GOLD + "$" + moneyAmountToDeposit + ChatColor.GREEN + " has been deposited to " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + " from " + companyName + ".";
    }

}
