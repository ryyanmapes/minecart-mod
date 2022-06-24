package com.alc.moreminecarts.items;

import com.alc.moreminecarts.entities.TankCartEntity;
import com.alc.moreminecarts.registry.MMEntities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TankCartItem extends AbstractMinecartItem {

    public TankCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, Level world, double posX, double posY, double posZ) {

        TankCartEntity minecart = new TankCartEntity(MMEntities.TANK_CART_ENTITY.get(), world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
