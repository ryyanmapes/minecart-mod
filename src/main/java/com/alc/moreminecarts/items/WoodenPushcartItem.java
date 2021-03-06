package com.alc.moreminecarts.items;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.WoodenPushcartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class WoodenPushcartItem extends AbstractMinecartItem {

    public WoodenPushcartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        WoodenPushcartEntity minecart = new WoodenPushcartEntity(MMReferences.wooden_pushcart, world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
