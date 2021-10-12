package com.alc.moreminecarts.items;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.TankCartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TankCartItem extends AbstractMinecartItem {

    public TankCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        TankCartEntity minecart = new TankCartEntity(MMReferences.tank_cart, world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
