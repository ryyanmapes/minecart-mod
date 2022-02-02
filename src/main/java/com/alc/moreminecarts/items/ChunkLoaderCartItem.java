package com.alc.moreminecarts.items;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.ChunkLoaderCartEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ChunkLoaderCartItem extends AbstractMinecartItem {

    public ChunkLoaderCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, Level world, double posX, double posY, double posZ) {

        ChunkLoaderCartEntity minecart = new ChunkLoaderCartEntity(MMReferences.minecart_with_chunk_loader, world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
