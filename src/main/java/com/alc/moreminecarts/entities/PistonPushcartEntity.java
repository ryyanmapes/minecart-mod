package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class PistonPushcartEntity extends IronPushcartEntity {
    public static final DataParameter<Float> HEIGHT_PARAMETER = EntityDataManager.defineId(PistonPushcartEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> LAST_HEIGHT_PARAMETER = EntityDataManager.defineId(PistonPushcartEntity.class, DataSerializers.FLOAT);

    public PistonPushcartEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public PistonPushcartEntity(EntityType<?> type, World worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    public float height;
    public float last_height;
    // Not synced
    public boolean going_up;
    public boolean going_down;

    @Override
    public double getPassengersRidingOffset() {
        return height;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getControllingPassenger() instanceof PlayerEntity) {
            if (going_up) height += MMConstants.PISTON_PUSHCART_VERTICAL_SPEED;
            if (going_down) height -= MMConstants.PISTON_PUSHCART_VERTICAL_SPEED;

            if (height < 0) height = 0;
            if (height > MMConstants.PISTON_PUSHCART_MAX_HEIGHT) height = MMConstants.PISTON_PUSHCART_MAX_HEIGHT;
        }

    }

    @Override
    protected void removePassenger(Entity p_184225_1_) {
        going_up = false;
        going_down = false;
        super.removePassenger(p_184225_1_);
    }

    @Override
    protected void addPassenger(Entity p_184200_1_) {
        going_up = false;
        going_down = false;
        super.addPassenger(p_184200_1_);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(HEIGHT_PARAMETER, height);
        this.getEntityData().define(LAST_HEIGHT_PARAMETER, last_height);
    }

    public void setElevating(boolean is_upwards, boolean is_down) {
        if (is_upwards) going_up = is_down;
        else going_down = is_down;
    }
}
