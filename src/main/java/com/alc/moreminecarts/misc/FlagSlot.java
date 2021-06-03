package com.alc.moreminecarts.misc;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class FlagSlot extends Slot {


    public FlagSlot(IInventory inventory, int index, int x_pos, int y_pos) {
        super(inventory, index, x_pos, y_pos);
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        return FlagUtil.getFlagColorValue(itemstack.getItem()) != 0;
    }
}
