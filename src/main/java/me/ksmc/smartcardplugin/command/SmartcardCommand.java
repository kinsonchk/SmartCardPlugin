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
            case "getcard": {
                if (args.length < 2) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                String agencyID = args[1];
                String expiryFromLastUse = "";
                if (args.length > 2) {
                    expiryFromLastUse = args[2];
                } else {
                    //expiryFromLastUse = get value from config.yml
                }

                if (sender instanceof Player player && player.hasPermission(adminPermission)) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else if (sender instanceof Player) {
                    sender.sendMessage(noPermissionMessage);
                } else {
                    sender.sendMessage(onlyInGameUsageMessage);
                }
                break;
            }

            case "setcardbalance": {
                if (args.length < 3) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                String cardID = args[1];
                double moneyAmount = 0.0;
                try {
                    moneyAmount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Error: Please enter a valid money amount.");
                    break;
                }

                if (sender instanceof Player player && player.hasPermission(adminPermission) || sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else {
                    sender.sendMessage(noPermissionMessage);
                }
                break;
            }

            case "fixcard": {
                if (args.length < 2) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                String cardID = args[1];

                if (sender instanceof Player player && player.hasPermission(adminPermission) || sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else {
                    sender.sendMessage(noPermissionMessage);
                }
                break;
            }

            case "ticket": {
                if (args.length < 4) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                String agencyID = args[1];
                String originStation = args[2];
                String destinationStation = args[3];
                String expiryTime = "";
                if (args.length > 4) {
                    expiryTime = args[4];
                } else {
                    //expiryTime = get value from config.yml
                }

                if (sender instanceof Player player && player.hasPermission(adminPermission)) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else if (sender instanceof Player) {
                    sender.sendMessage(noPermissionMessage);
                } else {
                    sender.sendMessage(onlyInGameUsageMessage);
                }
                break;
            }

            case "exitonly": {
                if (args.length < 2) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                String agencyID = args[1];

                if (sender instanceof Player player && player.hasPermission(adminPermission)) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else if (sender instanceof Player) {
                    sender.sendMessage(noPermissionMessage);
                } else {
                    sender.sendMessage(onlyInGameUsageMessage);
                }
                break;
            }

            case "getpass": {
                if (args.length < 3) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                String agencyID = args[1];
                String passType = args[2];

                if (sender instanceof Player player && player.hasPermission(adminPermission)) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else if (sender instanceof Player) {
                    sender.sendMessage(noPermissionMessage);
                } else {
                    sender.sendMessage(onlyInGameUsageMessage);
                }
                break;
            }

            case "staffpass": {
                if (args.length < 2) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                String agencyID = args[1];

                if (sender instanceof Player player && player.hasPermission(adminPermission)) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else if (sender instanceof Player) {
                    sender.sendMessage(noPermissionMessage);
                } else {
                    sender.sendMessage(onlyInGameUsageMessage);
                }
                break;
            }

            case "delete": {
                if (args.length < 3) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                if (!args[1].equals("card") && !args[1].equals("pass")) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                String fareMediumID = args[2];

                if (sender instanceof Player player && player.hasPermission(adminPermission) || sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else {
                    sender.sendMessage(noPermissionMessage);
                }
                break;
            }

            case "machine": {
                if (args.length < 2) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                String agencyID = args[1];
                String currentStation = "";
                if (args.length > 2) {
                    currentStation = args[2];
                }

                if (sender instanceof Player player) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else {
                    sender.sendMessage(onlyInGameUsageMessage);
                }
                break;
            }

            case "enquiry": {
                if (sender instanceof Player player) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else {
                    sender.sendMessage(onlyInGameUsageMessage);
                }
                break;
            }

            case "enquirystaff": {
                if (args.length < 3) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                if (!args[1].equals("card") && !args[1].equals("pass")) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                String fareMediumID = args[2];

                if (sender instanceof Player player && player.hasPermission(adminPermission) || sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else {
                    sender.sendMessage(noPermissionMessage);
                }
                break;
            }

            case "revenue": {
                if (args.length < 2) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                String agencyID = args[1];

                if (sender instanceof Player player && player.hasPermission(adminPermission) || sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else {
                    sender.sendMessage(noPermissionMessage);
                }
                break;
            }

            case "withdraw":
                if (args.length < 4) {
                    sender.sendMessage(incorrectUsageMessage);
                    return false;
                }
                String agencyID = args[1];
                String moneyAmount = args[2];
                String adminName = args[3];

                if (sender instanceof Player player && player.hasPermission(adminPermission) || sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("Success!");
                    // run the subcommand

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
