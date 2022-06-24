package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.registry.MMItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class WoodenPushcartEntity extends AbstractPushcart {
    public WoodenPushcartEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public WoodenPushcartEntity(EntityType<?> type, Level worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    @Override
    protected Item getDropItem() {
        return MMItems.WOODEN_PUSHCART_ITEM.get();
    }

    public double getControlSpeed() { return 40; }
    public double getUphillSpeed() { return 4.8; }
    public double getBrakeSpeed() { return 0.25; }

    // Wooden cart speed is capped at half that of a normal cart
    @Override
    protected double getMaxSpeed() {
        return 0.3D;
    }

    //@Override
    //public ItemStack getCartItem() { return new ItemStack(MMItemReferences.wooden_pushcart); }
}
