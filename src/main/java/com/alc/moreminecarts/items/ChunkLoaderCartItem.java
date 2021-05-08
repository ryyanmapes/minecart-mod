package com.alc.moreminecarts.items;

import com.alc.moreminecarts.entities.ChunkLoaderCartEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("moreminecarts")
public class ChunkLoaderCartItem extends AbstractMinecartItem {

    public static final EntityType<ChunkLoaderCartEntity> minecart_with_chunk_loader = null;

    public ChunkLoaderCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        ChunkLoaderCartEntity minecart = new ChunkLoaderCartEntity(minecart_with_chunk_loader, world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
