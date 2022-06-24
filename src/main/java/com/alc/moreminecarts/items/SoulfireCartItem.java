package com.alc.moreminecarts.items;

import com.alc.moreminecarts.entities.SoulfireCartEntity;
import com.alc.moreminecarts.registry.MMEntities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SoulfireCartItem extends AbstractMinecartItem {

    public SoulfireCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, Level world, double posX, double posY, double posZ) {

        SoulfireCartEntity minecart = new SoulfireCartEntity(MMEntities.SOULFIRE_CART_ENTITY.get(), world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
