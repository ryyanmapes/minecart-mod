package com.alc.moreminecarts.items;

import com.alc.moreminecarts.entities.FlagCartEntity;
import com.alc.moreminecarts.registry.MMEntities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FlagCartItem extends AbstractMinecartItem {

    public FlagCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, Level world, double posX, double posY, double posZ) {

        FlagCartEntity minecart = new FlagCartEntity(MMEntities.FLAG_CART.get(), world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
