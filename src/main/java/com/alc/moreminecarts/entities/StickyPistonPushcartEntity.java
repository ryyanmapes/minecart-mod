package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.registry.MMItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class StickyPistonPushcartEntity extends PistonPushcartEntity{

    public StickyPistonPushcartEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public StickyPistonPushcartEntity(EntityType<?> type, Level worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        if (ContainsPlayerPassenger()) {
            if (going_up && going_down) {
                attemptContract();
            }
        }

    }

    public void attemptContract() {
        if (getHeight() <= 1) return;

        this.moveTo(this.position().add(0, getHeight(), 0));
        setLastHeight(0);
        setHeight(0);

        if (this.level().isClientSide) {
            level().playLocalSound(position().x, position().y, position().z, SoundEvents.PISTON_CONTRACT, SoundSource.NEUTRAL, 0.3F, 1.0F, false);
        }

        onHeightChanged();
    }

    public Item getDropItem() {
        return MMItems.STICKY_PISTON_PUSHCART_ITEM.get();
    }

    //@Override
    //public ItemStack getCartItem() { return new ItemStack(MMItemReferences.sticky_piston_pushcart); }

}
