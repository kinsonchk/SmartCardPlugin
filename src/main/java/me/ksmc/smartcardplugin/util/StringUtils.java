package me.ksmc.smartcardplugin.util;

import me.ksmc.smartcardplugin.database.FareMediaDatabase;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static final String TIME_STRING_FORMAT = "^(\\d+)([smhdw])$";
    public static final String MONEY_STRING_FORMAT = "^-?(0|[1-9]\\d*)(\\.\\d{1,2})?$";  // only allows a double with at most 2 decimal places

    public static boolean validTimeString(String string) {
        return string != null && string.matches(TIME_STRING_FORMAT);
    }

    public static boolean validMoneyString(String string) {
        return string != null && string.matches(MONEY_STRING_FORMAT);
    }

    public static Object convertExpiryToTimestamp(String expiryDuration) {
        if (expiryDuration == null) {
            return null;
        }
        expiryDuration = expiryDuration.toLowerCase(Locale.ROOT);
        if (!validTimeString(expiryDuration)) {
            return null;
        }

        Pattern pattern = Pattern.compile(TIME_STRING_FORMAT);
        Matcher matcher = pattern.matcher(expiryDuration.trim().toLowerCase(Locale.ROOT));
        if (!matcher.matches()) {
            return null;
        }

        long amount = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        Instant now = Instant.now();
        Instant expiryTimestamp = switch (unit) {
            case "s" -> now.plus(amount, ChronoUnit.SECONDS);
            case "m" -> now.plus(amount, ChronoUnit.MINUTES);
            case "h" -> now.plus(amount, ChronoUnit.HOURS);
            case "d" -> now.plus(amount, ChronoUnit.DAYS);
            case "w" -> now.plus(amount * 7, ChronoUnit.DAYS);
            default -> null;
        };
        return expiryTimestamp;
    }

    public static Duration convertDurationToTimeUnits(String duration) {
        if (duration == null) {
            return null;
        }
        duration = duration.toLowerCase(Locale.ROOT);
        if (!validTimeString(duration)) {
            return null;
        }

        Pattern pattern = Pattern.compile(TIME_STRING_FORMAT);
        Matcher matcher = pattern.matcher(duration.trim().toLowerCase(Locale.ROOT));
        if (!matcher.matches()) {
            return null;
        }

        long amount = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        Duration durationInTimeUnits = switch (unit) {
            case "s" -> Duration.ofSeconds(amount);
            case "m" -> Duration.ofMinutes(amount);
            case "h" -> Duration.ofHours(amount);
            case "d" -> Duration.ofDays(amount);
            case "w" -> Duration.ofDays(amount * 7);
            default -> null;
        };
        return durationInTimeUnits;
    }


    public static String getCardOrPassIdFromLore(ItemStack smartcard) {
        String rawLore = ChatColor.stripColor(smartcard.getItemMeta().getLore().get(0));
        return rawLore.replace("Card ID: ", "").trim();

    }

    public static String getCardOrPassAgencyIdFromLore(ItemStack staffPass) {
        String rawLore = ChatColor.stripColor(staffPass.getItemMeta().getLore().get(1));  // STORED ON THE 2ND LINE!!!
        return rawLore.replace("Agency ID: ", "").trim();
    }

    public static String getAgencyIdOnCardEntryRecord(String cardID) throws SQLException {
        String rawEntryRecord = FareMediaDatabase.getFareMediaDatabase().getEntryRecord(cardID);
        return rawEntryRecord.split(":")[0];
    }

    public static String getAgencyIdFromLore(ItemStack staffPass) {
        String rawLore = ChatColor.stripColor(staffPass.getItemMeta().getLore().get(0));
        return rawLore.replace("Agency ID: ", "").trim();
    }

    public static String getTicketOriginStationFromLore(ItemStack ticket) {
        String rawLore = ChatColor.stripColor(ticket.getItemMeta().getLore().get(1));
        return rawLore.replace("Origin Station: ", "").trim();
    }

    public static String getTicketDestinationStationFromLore(ItemStack ticket) {
        String rawLore = ChatColor.stripColor(ticket.getItemMeta().getLore().get(2));
        return rawLore.replace("Destination Station: ", "").trim();
    }

    public static String getTicketUsedStateFromLore(ItemStack ticket) {
        String rawLore = ChatColor.stripColor(ticket.getItemMeta().getLore().get(4));
        return rawLore.replace("Used: ", "").trim();
    }

    public static String getTicketExpiryTimestampFromLore(ItemStack ticket) {
        String rawLore = ChatColor.stripColor(ticket.getItemMeta().getLore().get(3));
        return rawLore.replace("Expiry: ", "").trim();
    }

}
