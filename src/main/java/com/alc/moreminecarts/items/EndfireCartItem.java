package com.alc.moreminecarts.items;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.EndfireCartEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EndfireCartItem extends AbstractMinecartItem {

    public EndfireCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, Level world, double posX, double posY, double posZ) {

        EndfireCartEntity minecart = new EndfireCartEntity(MMReferences.endfire_cart, world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
