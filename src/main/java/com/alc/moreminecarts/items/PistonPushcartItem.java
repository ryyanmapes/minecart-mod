package com.alc.moreminecarts.items;

import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.alc.moreminecarts.registry.MMEntities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PistonPushcartItem extends AbstractMinecartItem {

    public PistonPushcartItem(Properties builder) {
        super(builder);
    }


    @Override
    void createMinecart(ItemStack stack, Level world, double posX, double posY, double posZ) {

        PistonPushcartEntity minecart = new PistonPushcartEntity(MMEntities.PISTON_PUSHCART_ENTITY.get(), world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
