package com.alc.moreminecarts.items;

import com.alc.moreminecarts.entities.MinecartWithNet;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("moreminecarts")
public class MinecartWithNetItem extends AbstractMinecartItem {

    public static final EntityType<MinecartWithNet> minecart_with_net = null;

    public MinecartWithNetItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        MinecartWithNet minecart = new MinecartWithNet(minecart_with_net, world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
