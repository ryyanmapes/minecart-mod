package com.example.examplemod.items;

import com.example.examplemod.entities.IronPushcartEntity;
import com.example.examplemod.entities.WoodenPushcartEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("examplemod")
public class IronPushcartItem extends AbstractMinecartItem {

    public static final EntityType<IronPushcartEntity> iron_pushcart = null;

    public IronPushcartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        IronPushcartEntity minecart = new IronPushcartEntity(iron_pushcart, world, posX, posY, posZ);
        if (stack.hasDisplayName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addEntity(minecart);
    }
}
