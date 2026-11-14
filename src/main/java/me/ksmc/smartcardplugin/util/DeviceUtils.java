package me.ksmc.smartcardplugin.util;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;

public class DeviceUtils {

    public static void playSuccessSound(Player player) {

        player.playNote(player.getLocation(), Instrument.PLING, Note.sharp(2, Note.Tone.F));
    }

    public static void playErrorSound(Player player) {

        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
    }

}
