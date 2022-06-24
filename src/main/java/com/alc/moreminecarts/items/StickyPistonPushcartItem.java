package com.alc.moreminecarts.items;

import com.alc.moreminecarts.entities.StickyPistonPushcartEntity;
import com.alc.moreminecarts.registry.MMEntities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StickyPistonPushcartItem extends AbstractMinecartItem {

    public StickyPistonPushcartItem(Properties builder) {
        super(builder);
    }


    @Override
    void createMinecart(ItemStack stack, Level world, double posX, double posY, double posZ) {

        StickyPistonPushcartEntity minecart = new StickyPistonPushcartEntity(MMEntities.STICKY_PISTON_PUSHCART_ENTITY.get(), world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
