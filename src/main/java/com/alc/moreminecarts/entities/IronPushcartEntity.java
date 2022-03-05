package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMItemReferences;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class IronPushcartEntity extends AbstractPushcart {

    public IronPushcartEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public IronPushcartEntity(EntityType<?> type, Level worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    public double getControlSpeed() { return 100; }
    public double getUphillSpeed() { return 5.5; }
    public double getBrakeSpeed() { return 0; }

    @Override
    public void destroy(DamageSource source) {
        this.remove(RemovalReason.KILLED);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack stack = new ItemStack(MMItemReferences.iron_pushcart);
            if (this.hasCustomName()) {
                stack.setHoverName(this.getCustomName());
            }
            this.spawnAtLocation(stack);
        }
    }

    //@Override
    //public ItemStack getCartItem() { return new ItemStack(MMItemReferences.iron_pushcart); }
}
