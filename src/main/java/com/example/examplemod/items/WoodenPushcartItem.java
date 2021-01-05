package com.example.examplemod.items;

import com.example.examplemod.entities.AbstractPushcart;
import com.example.examplemod.entities.WoodenPushcartEntity;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("examplemod")
public class WoodenPushcartItem extends AbstractMinecartItem {

    public static final EntityType<WoodenPushcartEntity> wooden_pushcart = null;

    public WoodenPushcartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        WoodenPushcartEntity minecart = new WoodenPushcartEntity(wooden_pushcart, world, posX, posY, posZ);
        if (stack.hasDisplayName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addEntity(minecart);
    }
}
