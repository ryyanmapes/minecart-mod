package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.MMItemReferences;
import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.proxy.MoreMinecartsPacketHandler;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class PistonPushcartEntity extends IronPushcartEntity {
    private static final ImmutableMap<Pose, ImmutableList<Integer>> POSE_DISMOUNT_HEIGHTS = ImmutableMap.of(Pose.STANDING, ImmutableList.of(0, 1, -1), Pose.CROUCHING, ImmutableList.of(0, 1, -1), Pose.SWIMMING, ImmutableList.of(0, 1));

    public static final DataParameter<Float> HEIGHT_PARAMETER = EntityDataManager.defineId(PistonPushcartEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> LAST_HEIGHT_PARAMETER = EntityDataManager.defineId(PistonPushcartEntity.class, DataSerializers.FLOAT);
    public static final String HEIGHT_NAME = "height";
    public static final String LAST_HEIGHT_NAME = "last_height";

    public float getHeight() {
        return this.entityData.get(HEIGHT_PARAMETER);
    }
    protected void setHeight(float height) {
        this.entityData.set(HEIGHT_PARAMETER, height);
    }
    public float getLastHeight() {
        return this.entityData.get(LAST_HEIGHT_PARAMETER);
    }
    protected void setLastHeight(float height) {
        this.entityData.set(LAST_HEIGHT_PARAMETER, height);
    }

    public PistonPushcartEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public PistonPushcartEntity(EntityType<?> type, World worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    // Not synced
    public boolean going_up;
    public boolean going_down;

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat(HEIGHT_NAME, getHeight());
        compound.putFloat(LAST_HEIGHT_NAME, getLastHeight());
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.setHeight(compound.getFloat(HEIGHT_NAME));
        this.setLastHeight(compound.getFloat(LAST_HEIGHT_NAME));
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.65 + getHeight();
    }

    @Override
    public void tick() {
        super.tick();

        final float height = getHeight();
        final float last_height = getLastHeight();

        if (height != last_height) setLastHeight(height);

        if (ContainsPlayerPassenger()) {
            if (going_up) attemptMove(true);
            if (going_down) attemptMove(false);
        }

    }

    public void attemptMove(boolean going_up) {
        final float height = getHeight();

        if (going_up) {
            BlockPos test_pos = this.blockPosition().above((int)Math.ceil(height + 1.5));
            BlockState test_state = level.getBlockState( test_pos );
            if (test_state.isAir()) setHeight(height + getVerticalSpeed());
            if (getHeight() > MMConstants.PISTON_PUSHCART_MAX_HEIGHT) setHeight(MMConstants.PISTON_PUSHCART_MAX_HEIGHT);
        }
        else {
            setHeight(height - getVerticalSpeed());
            if (getHeight() < 0) setHeight(0);
        }
    }

    public float getVerticalSpeed() {
        return MMConstants.PISTON_PUSHCART_VERTICAL_SPEED;
    }

    public boolean ContainsPlayerPassenger() {
        for (Entity entity : getPassengers()) {
            if (entity instanceof PlayerEntity) return true;
        }
        return false;
    }

    @Override
    public void activateMinecart(int x, int y, int z, boolean receivingPower) {
        if (receivingPower) attemptMove(true);
        else attemptMove(false);
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
        this.getEntityData().define(HEIGHT_PARAMETER, 0.0f);
        this.getEntityData().define(LAST_HEIGHT_PARAMETER, 0.0f);
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
                axisalignedbb.maxX, axisalignedbb.maxY + 0.65 + getHeight(), axisalignedbb.maxZ);
        return this.hasCustomDisplay() ? true_axisalignedbb.inflate((double)Math.abs(this.getDisplayOffset()) / 16.0D) : true_axisalignedbb;
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
            this.spawnAtLocation(new ItemStack(Items.PISTON));
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        AxisAlignedBB bounding_box = super.getBoundingBox();
        return new AxisAlignedBB(bounding_box.minX, bounding_box.minY, bounding_box.minZ,
                bounding_box.maxX, bounding_box.maxY + 0.65 + getHeight(), bounding_box.maxZ);
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
                        blockpos$mutable.set(blockpos.getX() + aint1[0], blockpos.getY() + getHeight() + i, blockpos.getZ() + aint1[1]);
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

    // Weird workaround to extend entity interaction distance limits.
    public ActionResultType interact(PlayerEntity player, Hand hand) {
        ActionResultType result = super.interact(player, hand);

        // Only used when they are too far away for the normal entity interaction packet.
        double distance = this.distanceToSqr(player);
        if (result == ActionResultType.SUCCESS && level.isClientSide && distance >= 36.0D && distance < 100.0) {
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(
                    new MoreMinecartsPacketHandler.ExtendedInteractPacket(this, hand, player.isShiftKeyDown()));
        }
        return result;
    }
}
