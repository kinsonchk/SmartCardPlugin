package me.ksmc.smartcardplugin.util;

public class FormatUtils {

    public static boolean verifyTimeString(String string) {
        return string.matches("\\d+[smhdw]");
    }
}
