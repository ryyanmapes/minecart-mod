package com.alc.moreminecarts.items;

import com.alc.moreminecarts.entities.WoodenPushcartEntity;
import com.alc.moreminecarts.registry.MMEntities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WoodenPushcartItem extends AbstractMinecartItem {

    public WoodenPushcartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, Level world, double posX, double posY, double posZ) {

        WoodenPushcartEntity minecart = new WoodenPushcartEntity(MMEntities.WOODEN_PUSHCART_ENTITY.get(), world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
