package com.alc.moreminecarts.items;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.CampfireCartEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CampfireCartItem extends AbstractMinecartItem {

    public CampfireCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, Level world, double posX, double posY, double posZ) {

        CampfireCartEntity minecart = new CampfireCartEntity(MMReferences.campfire_cart, world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
