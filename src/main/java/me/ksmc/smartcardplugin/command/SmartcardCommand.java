package me.ksmc.smartcardplugin.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SmartcardCommand implements CommandExecutor {

    public final String adminPermission = "smartcard.admin";

    public final String noPermissionMessage = ChatColor.RED + "You do not have permission to use this command.";
    public final String onlyInGameUsageMessage = ChatColor.RED + "This command can only be used in game.";
    public final String incorrectUsageMessage = ChatColor.RED + "Error: Incorrect subcommand usage.";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            return false;
        }

        String subcommand = args[0];
        switch (subcommand) {
            case "getcard":
                if (args.length < 2) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                if (sender instanceof Player player && player.hasPermission(adminPermission)) {

                    sender.sendMessage("Success!");

                } else if (sender instanceof Player) {
                    sender.sendMessage(noPermissionMessage);
                } else {
                    sender.sendMessage(onlyInGameUsageMessage);
                }
                break;

            case "setcardbalance":
                if (args.length < 3) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                if (sender instanceof Player player && player.hasPermission(adminPermission) || sender instanceof ConsoleCommandSender) {

                    sender.sendMessage("Success!");

                } else {
                    sender.sendMessage(noPermissionMessage);
                }
                break;

            case "fixcard":
                if (args.length < 2) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                if (sender instanceof Player player && player.hasPermission(adminPermission) || sender instanceof ConsoleCommandSender) {

                    sender.sendMessage("Success!");

                } else {
                    sender.sendMessage(noPermissionMessage);
                }
                break;

            case "ticket":
                if (args.length < 4) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                if (sender instanceof Player player && player.hasPermission(adminPermission)) {

                    sender.sendMessage("Success!");

                } else if (sender instanceof Player) {
                    sender.sendMessage(noPermissionMessage);
                } else {
                    sender.sendMessage(onlyInGameUsageMessage);
                }
                break;

            case "exitonly":
                if (args.length < 2) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                if (sender instanceof Player player && player.hasPermission(adminPermission)) {

                    sender.sendMessage("Success!");

                } else if (sender instanceof Player) {
                    sender.sendMessage(noPermissionMessage);
                } else {
                    sender.sendMessage(onlyInGameUsageMessage);
                }
                break;

            case "getpass":
                if (args.length < 3) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                if (sender instanceof Player player && player.hasPermission(adminPermission)) {

                    sender.sendMessage("Success!");

                } else if (sender instanceof Player) {
                    sender.sendMessage(noPermissionMessage);
                } else {
                    sender.sendMessage(onlyInGameUsageMessage);
                }
                break;

            case "staffpass":
                if (args.length < 2) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                if (sender instanceof Player player && player.hasPermission(adminPermission)) {

                    sender.sendMessage("Success!");

                } else if (sender instanceof Player) {
                    sender.sendMessage(noPermissionMessage);
                } else {
                    sender.sendMessage(onlyInGameUsageMessage);
                }
                break;

            case "delete":
                if (args.length < 3) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                if (sender instanceof Player player && player.hasPermission(adminPermission) || sender instanceof ConsoleCommandSender) {

                    sender.sendMessage("Success!");

                } else {
                    sender.sendMessage(noPermissionMessage);
                }
                break;

            case "machine":
                if (args.length < 2) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                if (sender instanceof Player player) {

                    sender.sendMessage("Success!");

                } else {
                    sender.sendMessage(onlyInGameUsageMessage);
                }
                break;

            case "enquiry":
                if (sender instanceof Player player) {

                    sender.sendMessage("Success!");

                } else {
                    sender.sendMessage(onlyInGameUsageMessage);
                }
                break;

            case "enquirystaff":
                if (args.length < 3) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                if (sender instanceof Player player && player.hasPermission(adminPermission) || sender instanceof ConsoleCommandSender) {

                    sender.sendMessage("Success!");

                } else {
                    sender.sendMessage(noPermissionMessage);
                }
                break;

            case "revenue":
                if (args.length < 2) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                if (sender instanceof Player player && player.hasPermission(adminPermission) || sender instanceof ConsoleCommandSender) {

                    sender.sendMessage("Success!");

                } else {
                    sender.sendMessage(noPermissionMessage);
                }
                break;

            case "withdraw":
                if (args.length < 4) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                if (sender instanceof Player player && player.hasPermission(adminPermission) || sender instanceof ConsoleCommandSender) {

                    sender.sendMessage("Success!");

                } else {
                    sender.sendMessage(noPermissionMessage);
                }
                break;

            default:
                sender.sendMessage(ChatColor.RED + "Error: Unknown subcommand.");
                return false;

        }

        return true;
    }
}
