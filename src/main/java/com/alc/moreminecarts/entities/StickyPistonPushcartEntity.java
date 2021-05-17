package com.alc.moreminecarts.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
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
            if (going_up && going_down && height > 1) {
                this.moveTo(this.position().add(0, height, 0));
                last_height = 0;
                height = 0;

                if (this.level.isClientSide) {
                    level.playLocalSound(position().x, position().y, position().z, SoundEvents.PISTON_CONTRACT, SoundCategory.NEUTRAL, 0.3F, 1.0F, false);
                }
            }
        }

    }

}
