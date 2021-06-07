package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMItemReferences;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class IronPushcartEntity extends AbstractPushcart {

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
    public void destroy(DamageSource source) {
        this.remove();
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack stack = new ItemStack(MMItemReferences.iron_pushcart);
            if (this.hasCustomName()) {
                stack.setHoverName(this.getCustomName());
            }
            this.spawnAtLocation(stack);
        }
    }

    @Override
    public ItemStack getCartItem() { return new ItemStack(MMItemReferences.iron_pushcart); }
}
