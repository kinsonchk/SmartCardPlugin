package me.ksmc.smartcardplugin.util;

public class FormatUtils {

    public static boolean verifyTimeString(String string) {
        return string != null && string.matches("\\d+[smhdw]");
    }

    public static boolean verifyMoneyString(String string) {
        return string != null && string.matches("^-?(0|[1-9]\\d*)(\\.\\d{1,2})?$");  // only allow double with at most 2 decimal places
    }
}
