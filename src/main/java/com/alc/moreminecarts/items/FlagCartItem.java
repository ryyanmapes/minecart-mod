package com.alc.moreminecarts.items;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.FlagCartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FlagCartItem extends AbstractMinecartItem {

    public FlagCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        FlagCartEntity minecart = new FlagCartEntity(MMReferences.flag_cart, world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
