package com.alc.moreminecarts.misc;

import com.alc.moreminecarts.MMItemReferences;
import net.minecraft.world.item.Item;

public class FlagUtil {

    public static byte getFlagColorValue (Item flag) {
        if (MMItemReferences.rail_signal_white.equals(flag)) {
            return 1;
        } else if (MMItemReferences.rail_signal_orange.equals(flag)) {
            return 2;
        } else if (MMItemReferences.rail_signal_magenta.equals(flag)) {
            return 3;
        } else if (MMItemReferences.rail_signal_light_blue.equals(flag)) {
            return 4;
        } else if (MMItemReferences.rail_signal_yellow.equals(flag)) {
            return 5;
        } else if (MMItemReferences.rail_signal_lime.equals(flag)) {
            return 6;
        } else if (MMItemReferences.rail_signal_pink.equals(flag)) {
            return 7;
        } else if (MMItemReferences.rail_signal_gray.equals(flag)) {
            return 8;
        } else if (MMItemReferences.rail_signal_light_gray.equals(flag)) {
            return 9;
        } else if (MMItemReferences.rail_signal_cyan.equals(flag)) {
            return 10;
        } else if (MMItemReferences.rail_signal_purple.equals(flag)) {
            return 11;
        } else if (MMItemReferences.rail_signal_blue.equals(flag)) {
            return 12;
        } else if (MMItemReferences.rail_signal_brown.equals(flag)) {
            return 13;
        } else if (MMItemReferences.rail_signal_green.equals(flag)) {
            return 14;
        } else if (MMItemReferences.rail_signal_red.equals(flag)) {
            return 15;
        } else if (MMItemReferences.rail_signal_black.equals(flag)) {
            return 16;
        }
        return 0;
    }

    public static int getNextSelectedSlot(int selected_slot, int discluded_slots, boolean is_decrement) {
        if (!is_decrement && selected_slot == 8-discluded_slots) selected_slot = 0;
        else if (is_decrement && selected_slot == 0) selected_slot = (byte)(8-discluded_slots);
        else {
            selected_slot += is_decrement? -1 : 1;
        }
        return selected_slot;
    }

}
