package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.MMReferences;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
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

        if (ContainsPlayerPassenger()) {
            if (going_up)
                height += MMConstants.PISTON_PUSHCART_VERTICAL_SPEED;
            if (going_down) height -= MMConstants.PISTON_PUSHCART_VERTICAL_SPEED;

            if (height < 0) height = 0;
            if (height > MMConstants.PISTON_PUSHCART_MAX_HEIGHT) height = MMConstants.PISTON_PUSHCART_MAX_HEIGHT;

            if (height != last_height) last_height = height;
        }

    }

    public boolean ContainsPlayerPassenger() {
        for (Entity entity : getPassengers()) {
            if (entity instanceof PlayerEntity) return true;
        }
        return false;
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

    @Override
    public BlockState getDisplayBlockState() {
        return MMReferences.piston_display_block.defaultBlockState();
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return MMReferences.piston_display_block.defaultBlockState();
    }

    @Override
    public AxisAlignedBB getBoundingBoxForCulling() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        AxisAlignedBB true_axisalignedbb = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ,
                axisalignedbb.maxX, axisalignedbb.maxY + height, axisalignedbb.maxZ);
        return this.hasCustomDisplay() ? true_axisalignedbb.inflate((double)Math.abs(this.getDisplayOffset()) / 16.0D) : true_axisalignedbb;
    }


    @Override
    public AxisAlignedBB getBoundingBox() {
        AxisAlignedBB bounding_box = super.getBoundingBox();
        return new AxisAlignedBB(bounding_box.minX, bounding_box.minY, bounding_box.minZ,
                bounding_box.maxX, bounding_box.maxY + height, bounding_box.maxZ);
    }
}
