package com.alc.moreminecarts.misc;

import com.alc.moreminecarts.tile_entities.ChunkLoaderTile;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ChunkLoaderSlot extends Slot {


    public ChunkLoaderSlot(Container inventory, int index, int x_pos, int y_pos) {
        super(inventory, index, x_pos, y_pos);
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        return ChunkLoaderTile.getBurnDuration(itemstack.getItem()) > 0;
    }
}
