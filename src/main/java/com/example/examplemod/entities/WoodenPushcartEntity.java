package com.example.examplemod.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class WoodenPushcartEntity extends AbstractPushcart {

    public static final Item wooden_pushcart = null;

    public WoodenPushcartEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public WoodenPushcartEntity(EntityType<?> type, World worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    // todo minecart item drop

    // Wooden cart speed is capped at half that of a normal cart
    @Override
    protected double getMaximumSpeed() {
        return 0.2D;
    }
}
