package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMItemReferences;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class StickyPistonPushcartEntity extends PistonPushcartEntity{

    public StickyPistonPushcartEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public StickyPistonPushcartEntity(EntityType<?> type, World worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        if (ContainsPlayerPassenger()) {
            if (going_up && going_down) {
                attempt_contract();
            }
        }

    }

    public void attempt_contract() {
        if (getHeight() <= 1) return;

        this.moveTo(this.position().add(0, getHeight(), 0));
        setLastHeight(0);
        setHeight(0);

        if (this.level.isClientSide) {
            level.playLocalSound(position().x, position().y, position().z, SoundEvents.PISTON_CONTRACT, SoundCategory.NEUTRAL, 0.3F, 1.0F, false);
        }
    }

    @Override
    public void destroy(DamageSource source) {
        this.remove();
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack stack = new ItemStack(MMItemReferences.iron_pushcart);
            if (this.hasCustomName()) {
                stack.setHoverName(this.getCustomName());
            }
            this.spawnAtLocation(stack);
            this.spawnAtLocation(new ItemStack(Items.STICKY_PISTON));
        }
    }

    @Override
    public ItemStack getCartItem() { return new ItemStack(MMItemReferences.sticky_piston_pushcart); }

}
