package com.alc.moreminecarts.items;

import com.alc.moreminecarts.entities.NetMinecartEntity;
import com.alc.moreminecarts.registry.MMEntities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MinecartWithNetItem extends AbstractMinecartItem {

    public MinecartWithNetItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, Level world, double posX, double posY, double posZ) {

        NetMinecartEntity minecart = new NetMinecartEntity(MMEntities.MINECART_WITH_NET_ENTITY.get(), world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
