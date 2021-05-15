package com.alc.moreminecarts.items;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.ChunkLoaderCartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ChunkLoaderCartItem extends AbstractMinecartItem {

    public ChunkLoaderCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        ChunkLoaderCartEntity minecart = new ChunkLoaderCartEntity(MMReferences.minecart_with_chunk_loader, world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
