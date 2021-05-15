package com.alc.moreminecarts.items;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.IronPushcartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class IronPushcartItem extends AbstractMinecartItem {

    public IronPushcartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        IronPushcartEntity minecart = new IronPushcartEntity(MMReferences.iron_pushcart, world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
