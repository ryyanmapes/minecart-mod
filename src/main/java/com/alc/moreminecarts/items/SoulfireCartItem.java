package com.alc.moreminecarts.items;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.SoulfireCartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SoulfireCartItem extends AbstractMinecartItem {

    public SoulfireCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        SoulfireCartEntity minecart = new SoulfireCartEntity(MMReferences.soulfire_cart, world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
