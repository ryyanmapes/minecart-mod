package com.alc.moreminecarts.items;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.NetMinecartEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MinecartWithNetItem extends AbstractMinecartItem {

    public MinecartWithNetItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, Level world, double posX, double posY, double posZ) {

        NetMinecartEntity minecart = new NetMinecartEntity(MMReferences.minecart_with_net, world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
