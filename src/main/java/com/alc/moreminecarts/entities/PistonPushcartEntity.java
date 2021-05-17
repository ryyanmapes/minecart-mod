package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.MMReferences;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Direction;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class PistonPushcartEntity extends IronPushcartEntity {
    private static final ImmutableMap<Pose, ImmutableList<Integer>> POSE_DISMOUNT_HEIGHTS = ImmutableMap.of(Pose.STANDING, ImmutableList.of(0, 1, -1), Pose.CROUCHING, ImmutableList.of(0, 1, -1), Pose.SWIMMING, ImmutableList.of(0, 1));

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
        return 0.5 + height;
    }

    @Override
    public void tick() {
        super.tick();

        if (ContainsPlayerPassenger()) {
            if (height != last_height) last_height = height;

            if (going_up) height += MMConstants.PISTON_PUSHCART_VERTICAL_SPEED;
            if (going_down) height -= MMConstants.PISTON_PUSHCART_VERTICAL_SPEED;

            if (height < 0) height = 0;
            if (height > MMConstants.PISTON_PUSHCART_MAX_HEIGHT) height = MMConstants.PISTON_PUSHCART_MAX_HEIGHT;
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

    // Copied from AbstractMinecartEntity
    public Vector3d getDismountLocationForPassenger(LivingEntity p_230268_1_) {
        Direction direction = this.getMotionDirection();
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.getDismountLocationForPassenger(p_230268_1_);
        } else {
            int[][] aint = TransportationHelper.offsetsForDirection(direction);
            BlockPos blockpos = this.blockPosition();
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            ImmutableList<Pose> immutablelist = p_230268_1_.getDismountPoses();

            for(Pose pose : immutablelist) {
                EntitySize entitysize = p_230268_1_.getDimensions(pose);
                float f = Math.min(entitysize.width, 1.0F) / 2.0F;

                for(int i : POSE_DISMOUNT_HEIGHTS.get(pose)) {
                    for(int[] aint1 : aint) {
                        // CHANGED to add height to Y component
                        blockpos$mutable.set(blockpos.getX() + aint1[0], blockpos.getY() + height + i, blockpos.getZ() + aint1[1]);
                        double d0 = this.level.getBlockFloorHeight(TransportationHelper.nonClimbableShape(this.level, blockpos$mutable), () -> {
                            return TransportationHelper.nonClimbableShape(this.level, blockpos$mutable.below());
                        });
                        if (TransportationHelper.isBlockFloorValid(d0)) {
                            AxisAlignedBB axisalignedbb = new AxisAlignedBB((double)(-f), 0.0D, (double)(-f), (double)f, (double)entitysize.height, (double)f);
                            Vector3d vector3d = Vector3d.upFromBottomCenterOf(blockpos$mutable, d0);
                            if (TransportationHelper.canDismountTo(this.level, p_230268_1_, axisalignedbb.move(vector3d))) {
                                p_230268_1_.setPose(pose);
                                return vector3d;
                            }
                        }
                    }
                }
            }

            double d1 = this.getBoundingBox().maxY;
            blockpos$mutable.set((double)blockpos.getX(), d1, (double)blockpos.getZ());

            for(Pose pose1 : immutablelist) {
                double d2 = (double)p_230268_1_.getDimensions(pose1).height;
                int j = MathHelper.ceil(d1 - (double)blockpos$mutable.getY() + d2);
                double d3 = TransportationHelper.findCeilingFrom(blockpos$mutable, j, (p_242377_1_) -> {
                    return this.level.getBlockState(p_242377_1_).getCollisionShape(this.level, p_242377_1_);
                });
                if (d1 + d2 <= d3) {
                    p_230268_1_.setPose(pose1);
                    break;
                }
            }

            return super.getDismountLocationForPassenger(p_230268_1_);
        }
    }
}
