package com.alc.moreminecarts.items;

import com.alc.moreminecarts.entities.ChunkLoaderCartEntity;
import com.alc.moreminecarts.registry.MMEntities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ChunkLoaderCartItem extends AbstractMinecartItem {

    public ChunkLoaderCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, Level world, double posX, double posY, double posZ) {

        ChunkLoaderCartEntity minecart = new ChunkLoaderCartEntity(MMEntities.CHUNK_LOADER_CART.get(), world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
