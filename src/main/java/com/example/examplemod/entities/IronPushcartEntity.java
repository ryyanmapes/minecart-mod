package com.example.examplemod.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("examplemod")
public class IronPushcartEntity extends AbstractPushcart {

    public static final Item iron_pushcart = null;

    public IronPushcartEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public IronPushcartEntity(EntityType<?> type, World worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    public double getControlSpeed() { return 100; }
    public double getUphillSpeed() { return 5.5; }
    public double getBrakeSpeed() { return 0; }

    @Override
    public void killMinecart(DamageSource source) {
        this.remove();
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            ItemStack stack = new ItemStack(iron_pushcart, 1);
            if (this.hasCustomName()) {
                stack.setDisplayName(this.getCustomName());
            }
            this.entityDropItem(stack);
        }
    }
}
