package com.alc.moreminecarts.misc;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FlagSlot extends Slot {


    public FlagSlot(Container inventory, int index, int x_pos, int y_pos) {
        super(inventory, index, x_pos, y_pos);
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        return FlagUtil.getFlagColorValue(itemstack.getItem()) != 0;
    }
}
