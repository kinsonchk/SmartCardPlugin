package me.ksmc.smartcardplugin.command;

import me.ksmc.smartcardplugin.Main;
import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import me.ksmc.smartcardplugin.manager.ItemManager;
import me.ksmc.smartcardplugin.util.FormatUtils;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Locale;

public class SmartcardCommand implements CommandExecutor {
    public static final String ADMIN_PERMISSION = "smartcard.admin";

    public static final String NO_PERMISSION_MESSAGE = ChatColor.RED + "You do not have permission to use this command.";
    public static final String ONLY_IN_GAME_USAGE_MESSAGE = ChatColor.RED + "This command can only be used in game.";
    public static final String INCORRECT_USAGE_MESSAGE = ChatColor.RED + "Error: Incorrect subcommand usage.";
    public static final String DATABASE_ERROR_MESSAGE = ChatColor.RED + "Error: Database error.";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String subcommand = args[0];
        switch (subcommand) {
            case "getcard": {
                if (args.length < 2) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }

                String agencyID = args[1];
                try {
                    if (!FareMediaDatabase.getFareMediaDatabase().doesAgencyExist(agencyID)) {
                        sender.sendMessage(ChatColor.RED + "Error: Agency not found.");
                        return true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage(DATABASE_ERROR_MESSAGE);
                }

                String expiryFromLastUse;
                if (args.length > 2) {
                    expiryFromLastUse = args[2];
                } else {
                    // get the default value from config.yml; if failed to get, the value will be null
                    expiryFromLastUse = Main.getPlugin().getConfig().getString(agencyID + ".smart-card-config.card-expiry-from-last-use", null);
                }

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION)) {
                    // run the subcommand
                    try {
                        String outcome = ItemManager.issueSmartcard(player, agencyID, expiryFromLastUse);
                        player.sendMessage(outcome);
                        return true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        player.sendMessage(DATABASE_ERROR_MESSAGE);
                    }

                } else if (sender instanceof Player) {
                    sender.sendMessage(NO_PERMISSION_MESSAGE);
                } else {
                    sender.sendMessage(ONLY_IN_GAME_USAGE_MESSAGE);
                }
                break;
            }

            case "setcardbalance": {
                if (args.length < 3) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                String cardID = args[1];
                BigDecimal moneyAmount;  // use BigDecimal for more numerical accuracy
                if (!FormatUtils.verifyMoneyString(args[2])) {
                    sender.sendMessage(ChatColor.RED + "Error: Please enter a valid money amount.");
                    return true;
                }
                try {
                    moneyAmount = new BigDecimal(args[2]).setScale(2, RoundingMode.HALF_UP);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Error: Please enter a valid numeric amount.");
                    return true;
                }

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION) || sender instanceof ConsoleCommandSender) {
                    // run the subcommand
                    try {
                        if (!FareMediaDatabase.getFareMediaDatabase().setCardBalance(cardID, Double.parseDouble(args[2]))) {
                            sender.sendMessage(ChatColor.RED + "Error: cardID not found.");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "The smartcard's balance has been updated to " + ChatColor.GOLD + "$" + moneyAmount + ChatColor.GREEN + ".");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        sender.sendMessage(DATABASE_ERROR_MESSAGE);
                    }

                } else {
                    sender.sendMessage(NO_PERMISSION_MESSAGE);
                }
                break;
            }

            case "fixcard": {
                if (args.length < 2) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                String cardID = args[1];

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION) || sender instanceof ConsoleCommandSender) {
                    // run the subcommand
                    try {
                        if (!FareMediaDatabase.getFareMediaDatabase().clearEntryExitRecords(cardID)) {
                            sender.sendMessage(ChatColor.RED + "Error: cardID not found.");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "The smartcard's current entry and exit records have been cleared.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        sender.sendMessage(DATABASE_ERROR_MESSAGE);
                    }
                } else {
                    sender.sendMessage(NO_PERMISSION_MESSAGE);
                }
                break;
            }

            case "ticket": {
                if (args.length < 4) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
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

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION)) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else if (sender instanceof Player) {
                    sender.sendMessage(NO_PERMISSION_MESSAGE);
                } else {
                    sender.sendMessage(ONLY_IN_GAME_USAGE_MESSAGE);
                }
                break;
            }

            case "exitonly": {
                if (args.length < 2) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                String agencyID = args[1];

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION)) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else if (sender instanceof Player) {
                    sender.sendMessage(NO_PERMISSION_MESSAGE);
                } else {
                    sender.sendMessage(ONLY_IN_GAME_USAGE_MESSAGE);
                }
                break;
            }

            case "getpass": {
                if (args.length < 3) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                String agencyID = args[1];
                String passType = args[2];

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION)) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else if (sender instanceof Player) {
                    sender.sendMessage(NO_PERMISSION_MESSAGE);
                } else {
                    sender.sendMessage(ONLY_IN_GAME_USAGE_MESSAGE);
                }
                break;
            }

            case "staffpass": {
                if (args.length < 2) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                String agencyID = args[1];

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION)) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else if (sender instanceof Player) {
                    sender.sendMessage(NO_PERMISSION_MESSAGE);
                } else {
                    sender.sendMessage(ONLY_IN_GAME_USAGE_MESSAGE);
                }
                break;
            }

            case "delete": {
                if (args.length < 3) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                if (!args[1].equals("card") && !args[1].equals("pass")) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                String fareMediumType = args[1];
                String fareMediumID = args[2];

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION) || sender instanceof ConsoleCommandSender) {
                    // run the subcommand
                    try {
                        if (fareMediumType.equals("card")) {
                            if (!FareMediaDatabase.getFareMediaDatabase().deleteSmartCard(fareMediumID)) {
                                sender.sendMessage(ChatColor.RED + "Error: cardID not found.");
                            } else {
                                sender.sendMessage(ChatColor.GREEN + "The smartcard has been deleted.");
                            }
                        } else {
                            if (!FareMediaDatabase.getFareMediaDatabase().deletePass(fareMediumID)) {
                                sender.sendMessage(ChatColor.RED + "Error: passID not found.");
                            } else {
                                sender.sendMessage(ChatColor.GREEN + "The pass has been deleted.");
                            }
                        }
                        return true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        sender.sendMessage(DATABASE_ERROR_MESSAGE);
                    }

                } else {
                    sender.sendMessage(NO_PERMISSION_MESSAGE);
                }
                break;
            }

            case "machine": {
                if (args.length < 2) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
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
                    sender.sendMessage(ONLY_IN_GAME_USAGE_MESSAGE);
                }
                break;
            }

            case "enquiry": {
                if (sender instanceof Player player) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else {
                    sender.sendMessage(ONLY_IN_GAME_USAGE_MESSAGE);
                }
                break;
            }

            case "enquirystaff": {
                if (args.length < 3) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                if (!args[1].equals("card") && !args[1].equals("pass")) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                String fareMediumID = args[2];

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION) || sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else {
                    sender.sendMessage(NO_PERMISSION_MESSAGE);
                }
                break;
            }

            case "revenue": {
                if (args.length < 2) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                String agencyID = args[1];

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION) || sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else {
                    sender.sendMessage(NO_PERMISSION_MESSAGE);
                }
                break;
            }

            case "withdraw":
                if (args.length < 4) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                String agencyID = args[1];
                String moneyAmount = args[2];
                String adminName = args[3];

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION) || sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("Success!");
                    // run the subcommand

                } else {
                    sender.sendMessage(NO_PERMISSION_MESSAGE);
                }
                break;

            default:
                sender.sendMessage(ChatColor.RED + "Error: Unknown subcommand.");
                return false;

        }

        return true;
    }
}
