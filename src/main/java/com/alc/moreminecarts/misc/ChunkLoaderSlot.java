package com.alc.moreminecarts.misc;

import com.alc.moreminecarts.tile_entities.ChunkLoaderTile;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ChunkLoaderSlot extends Slot {


    public ChunkLoaderSlot(IInventory inventory, int index, int x_pos, int y_pos) {
        super(inventory, index, x_pos, y_pos);
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        return ChunkLoaderTile.getBurnDuration(itemstack.getItem()) > 0;
    }
}
