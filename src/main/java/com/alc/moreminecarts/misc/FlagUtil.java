package com.alc.moreminecarts.misc;

import com.alc.moreminecarts.registry.MMItems;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class FlagUtil {
    public static byte getFlagColorValue (Item flag) {
        // Not doing this statically since it is not recommended to keep static references to Items.
        Map<Item, DyeColor> railSignalMap = new HashMap<>();

        for (Map.Entry<RegistryObject<Item>, DyeColor> entry : MMItems.RAIL_SIGNALS.inverse().entrySet()) {
            railSignalMap.put(entry.getKey().get(), entry.getValue());
        }

        if(railSignalMap.containsKey(flag)) {
            return (byte) (railSignalMap.get(flag).getId() + 1);
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
