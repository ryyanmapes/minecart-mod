package com.alc.moreminecarts.items;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.StickyPistonPushcartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class StickyPistonPushcartItem extends AbstractMinecartItem {

    public StickyPistonPushcartItem(Properties builder) {
        super(builder);
    }


    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        StickyPistonPushcartEntity minecart = new StickyPistonPushcartEntity(MMReferences.sticky_piston_pushcart, world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
