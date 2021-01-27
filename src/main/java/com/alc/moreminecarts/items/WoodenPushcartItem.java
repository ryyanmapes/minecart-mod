package com.alc.moreminecarts.items;

import com.alc.moreminecarts.entities.WoodenPushcartEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("moreminecarts")
public class WoodenPushcartItem extends AbstractMinecartItem {

    public static final EntityType<WoodenPushcartEntity> wooden_pushcart = null;

    public WoodenPushcartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        WoodenPushcartEntity minecart = new WoodenPushcartEntity(wooden_pushcart, world, posX, posY, posZ);
        if (stack.hasDisplayName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addEntity(minecart);
    }
}
