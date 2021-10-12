package com.alc.moreminecarts.items;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.OrbStasisCart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class OrbStasisCartItem extends AbstractMinecartItem {

    public OrbStasisCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        OrbStasisCart minecart = new OrbStasisCart(MMReferences.minecart_with_stasis, world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
