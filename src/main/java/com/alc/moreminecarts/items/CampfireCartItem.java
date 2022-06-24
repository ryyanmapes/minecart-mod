package com.alc.moreminecarts.items;

import com.alc.moreminecarts.entities.CampfireCartEntity;
import com.alc.moreminecarts.registry.MMEntities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CampfireCartItem extends AbstractMinecartItem {

    public CampfireCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, Level world, double posX, double posY, double posZ) {

        CampfireCartEntity minecart = new CampfireCartEntity(MMEntities.CAMPFIRE_CART_ENTITY.get(), world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
