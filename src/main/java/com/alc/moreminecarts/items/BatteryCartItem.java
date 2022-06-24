package com.alc.moreminecarts.items;

import com.alc.moreminecarts.entities.BatteryCartEntity;
import com.alc.moreminecarts.registry.MMEntities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BatteryCartItem extends AbstractMinecartItem {

    public BatteryCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, Level world, double posX, double posY, double posZ) {

        BatteryCartEntity minecart = new BatteryCartEntity(MMEntities.BATTERY_CART_ENTITY.get(), world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
