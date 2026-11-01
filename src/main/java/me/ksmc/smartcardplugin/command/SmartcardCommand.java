package me.ksmc.smartcardplugin.command;

import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import me.ksmc.smartcardplugin.manager.EconomyManager;
import me.ksmc.smartcardplugin.manager.ItemManager;
import me.ksmc.smartcardplugin.util.ItemUtils;
import me.ksmc.smartcardplugin.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

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
                String expiryFromLastUse;
                if (args.length > 2) {
                    expiryFromLastUse = args[2];
                } else {
                    // get the default value from config.yml; if failed to get, the value will be null
                    expiryFromLastUse = ItemUtils.getCardExpiryFromLastUseDefaultValue(agencyID);
                }

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION)) {
                    // run the subcommand
                    try {
                        player.sendMessage(ItemManager.issueSmartcard(player, agencyID, expiryFromLastUse));
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
                String moneyAmount = args[2];

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION) || sender instanceof ConsoleCommandSender) {
                    // run the subcommand
                    try {
                        sender.sendMessage(ItemManager.updateSmartcardBalance(cardID, moneyAmount));
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

            case "fixcard": {
                if (args.length < 2) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                String cardID = args[1];

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION) || sender instanceof ConsoleCommandSender) {
                    // run the subcommand
                    try {
                        sender.sendMessage(ItemManager.resetSmartcardEntryExitRecord(cardID));
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

            case "ticket": {
                if (args.length < 4) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }

                String agencyID = args[1];
                String originStation = args[2];
                String destinationStation = args[3];
                String expiryTime;
                if (args.length > 4) {
                    expiryTime = args[4];
                } else {
                    expiryTime = ItemUtils.getTicketExpiryDefaultValue(agencyID);
                }

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION)) {
                    // run the subcommand
                    try {
                        player.sendMessage(ItemManager.issueTicket(player, agencyID, originStation, destinationStation, expiryTime));
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

            case "exitonly": {
                if (args.length < 2) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                String agencyID = args[1];

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION)) {
                    // run the subcommand
                    try {
                        player.sendMessage(ItemManager.issueExitOnlyTicket(player, agencyID));
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

            case "getpass": {
                if (args.length < 3) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                String agencyID = args[1];
                String passType = args[2];

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION)) {
                    // run the subcommand
                    try {
                        player.sendMessage(ItemManager.issuePass(player, agencyID, passType));
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

            case "staffpass": {
                if (args.length < 2) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                String agencyID = args[1];

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION)) {
                    // run the subcommand
                    try {
                        player.sendMessage(ItemManager.issueStaffPass(player, agencyID));
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
                            sender.sendMessage(ItemManager.deleteSmartcard(fareMediumID));
                        } else {
                            sender.sendMessage(ItemManager.deletePass(fareMediumID));
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



            // TODO
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
                    // run the subcommand
                    ItemStack fareMedium = player.getInventory().getItemInMainHand();

                    if (ItemManager.isValidFareMedium(fareMedium)) {
                        if (fareMedium.getType() == Material.NAME_TAG) {
                            String cardID = StringUtils.getCardIdFromLore(fareMedium);

                            sender.sendMessage(ItemUtils.smartcardEnquiry(cardID));

                        } else if (fareMedium.getType() == Material.PAPER) {
                            String agencyID = StringUtils.getAgencyIdFromLore(fareMedium);
                            String originStation = StringUtils.getTicketOriginStationFromLore(fareMedium);
                            String destinationStation =  StringUtils.getTicketDestinationStationFromLore(fareMedium);
                            String ticketExpiry = StringUtils.getTicketExpiryTimestampFromLore(fareMedium);
                            String ticketUsedState = StringUtils.getTicketUsedStateFromLore(fareMedium);

                            sender.sendMessage(ChatColor.GREEN + "Single-Journey Ticket Info:" +
                                    ChatColor.YELLOW + "\nTicket Issuing Agency: " + ChatColor.GOLD + ItemUtils.getCompanyName(agencyID) + ChatColor.YELLOW + "  (Agency ID: " + ChatColor.GOLD + agencyID + ChatColor.YELLOW + ")" +
                                    ChatColor.YELLOW + "\nOrigin Station: " + ChatColor.GOLD + originStation +
                                    ChatColor.YELLOW + "\nDestination Station: " + ChatColor.GOLD + destinationStation +
                                    ChatColor.YELLOW + "\nTicket Expiry: " + ChatColor.GOLD + ticketExpiry +
                                    ChatColor.YELLOW + "\nTicket Already Used: " + ChatColor.GOLD + ticketUsedState
                            );

                        } else if (fareMedium.getType() == Material.BOOK) {
                            String passID = StringUtils.getPassIdFromLore(fareMedium);

                            sender.sendMessage(ItemUtils.passEnquiry(passID));

                        } else if (fareMedium.getType() == Material.IRON_NUGGET) {
                            String agencyID = StringUtils.getAgencyIdFromLore(fareMedium);
                            String ticketExpiry = StringUtils.getExitOnlyTicketExpiryTimestampFromLore(fareMedium);

                            sender.sendMessage(ChatColor.GREEN + "Exit-Only Ticket Info:" +
                                    ChatColor.YELLOW + "\nTicket Issuing Agency: " + ChatColor.GOLD + ItemUtils.getCompanyName(agencyID) + ChatColor.YELLOW + "  (Agency ID: " + ChatColor.GOLD + agencyID + ChatColor.YELLOW + ")" +
                                    ChatColor.YELLOW + "\nTicket Expiry: " + ChatColor.GOLD + ticketExpiry
                            );

                        } else if (fareMedium.getType() == Material.TRIAL_KEY) {
                            String agencyID = StringUtils.getAgencyIdFromLore(fareMedium);

                            sender.sendMessage(ChatColor.GREEN + "Staff Pass Info:" +
                                    ChatColor.YELLOW + "\nTicket Issuing Agency: " + ChatColor.GOLD + ItemUtils.getCompanyName(agencyID) + ChatColor.YELLOW + "  (Agency ID: " + ChatColor.GOLD + agencyID + ChatColor.YELLOW + ")" +
                                    ChatColor.RED + ChatColor.BOLD + "\nIMPORTANT: Do not hand out the staff pass to ordinary players!"
                            );

                        }

                    } else {
                        sender.sendMessage(ChatColor.RED + "Error: The item that you are currently holding is not a valid item for enquiry.");
                    }

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
                    // run the subcommand
                    if (args[1].equals("card")) {
                        try {
                            if (FareMediaDatabase.getFareMediaDatabase().smartcardExists(fareMediumID)) {
                                sender.sendMessage(ItemUtils.smartcardEnquiry(fareMediumID));
                            } else {
                                sender.sendMessage(ChatColor.RED + "Error: cardID not found.");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            sender.sendMessage(DATABASE_ERROR_MESSAGE);
                        }

                    } else if (args[1].equals("pass")) {
                        try {
                            if (FareMediaDatabase.getFareMediaDatabase().passExists(fareMediumID)) {
                                sender.sendMessage(ItemUtils.passEnquiry(fareMediumID));
                            } else {
                                sender.sendMessage(ChatColor.RED + "Error: passID not found.");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            sender.sendMessage(DATABASE_ERROR_MESSAGE);
                        }
                    }

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
                    // run the subcommand
                    try {
                        sender.sendMessage(EconomyManager.lookupAgencyRevenue(agencyID));
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

            case "withdraw": {
                if (args.length < 4) {
                    sender.sendMessage(INCORRECT_USAGE_MESSAGE);
                    return false;
                }
                String agencyID = args[1];
                String moneyAmount = args[2];
                String playerName = args[3];

                if (sender instanceof Player player && player.hasPermission(ADMIN_PERMISSION) || sender instanceof ConsoleCommandSender) {
                    // run the subcommand
                    try {
                        sender.sendMessage(EconomyManager.depositAgencyRevenueToPlayer(agencyID, moneyAmount, playerName));
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

            default:
                sender.sendMessage(ChatColor.RED + "Error: Unknown subcommand.");
                return false;

        }

        return true;
    }
}
