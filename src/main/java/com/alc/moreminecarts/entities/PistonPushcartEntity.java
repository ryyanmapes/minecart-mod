package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.MMItemReferences;
import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.proxy.MoreMinecartsPacketHandler;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PistonPushcartEntity extends IronPushcartEntity {
    private static final ImmutableMap<Pose, ImmutableList<Integer>> POSE_DISMOUNT_HEIGHTS = ImmutableMap.of(Pose.STANDING, ImmutableList.of(0, 1, -1), Pose.CROUCHING, ImmutableList.of(0, 1, -1), Pose.SWIMMING, ImmutableList.of(0, 1));

    public static final EntityDataAccessor<Float> HEIGHT_PARAMETER = SynchedEntityData.defineId(PistonPushcartEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> LAST_HEIGHT_PARAMETER = SynchedEntityData.defineId(PistonPushcartEntity.class, EntityDataSerializers.FLOAT);
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

    public PistonPushcartEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public PistonPushcartEntity(EntityType<?> type, Level worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    // Not synced
    public boolean going_up;
    public boolean going_down;

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions bounding_box = super.getDimensions(pose);
        return new EntityDimensions(bounding_box.width, bounding_box.height + 0.2f + getHeight(), bounding_box.fixed);
    }

    // Updates the bounding box when the piston is extended or retracted.
    public void onHeightChanged() {
        setBoundingBox(makeBoundingBox());
    }

    @Override
    protected AABB makeBoundingBox() {
        return this.getDimensions(Pose.STANDING).makeBoundingBox(position());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat(HEIGHT_NAME, getHeight());
        compound.putFloat(LAST_HEIGHT_NAME, getLastHeight());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setHeight(compound.getFloat(HEIGHT_NAME));
        this.setLastHeight(compound.getFloat(LAST_HEIGHT_NAME));
        onHeightChanged();
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
            if (going_up) attemptMove(true, false);
            if (going_down) attemptMove(false, false);
        }

    }

    public void attemptMove(boolean going_up, boolean is_reduced) {
        final float height = getHeight();

        if (going_up) {
            BlockPos test_pos = this.blockPosition().above((int)Math.ceil(height + 1.5));
            BlockState test_state = level.getBlockState( test_pos );
            if (!test_state.isCollisionShapeFullBlock(level, test_pos)) setHeight(height + getVerticalSpeed() * (is_reduced? 0.2f : 1) );
            if (getHeight() > MMConstants.PISTON_PUSHCART_MAX_HEIGHT) setHeight(MMConstants.PISTON_PUSHCART_MAX_HEIGHT);
        }
        else {
            setHeight(height - getVerticalSpeed() * (is_reduced? 0.2f : 1) );
            if (getHeight() < 0) setHeight(0);
        }

        onHeightChanged();
    }

    public float getVerticalSpeed() {
        return MMConstants.PISTON_PUSHCART_VERTICAL_SPEED;
    }

    public boolean ContainsPlayerPassenger() {
        for (Entity entity : getPassengers()) {
            if (entity instanceof Player) return true;
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
    public AABB getBoundingBoxForCulling() {
        AABB axisalignedbb = this.getBoundingBox();
        return this.hasCustomDisplay() ? axisalignedbb.inflate((double)Math.abs(this.getDisplayOffset()) / 16.0D) : axisalignedbb;
    }

    @Override
    public void destroy(DamageSource source) {
        this.remove(RemovalReason.KILLED);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack stack = new ItemStack(MMItemReferences.iron_pushcart);
            if (this.hasCustomName()) {
                stack.setHoverName(this.getCustomName());
            }
            this.spawnAtLocation(stack);
            this.spawnAtLocation(new ItemStack(Items.PISTON));
        }
    }

    // Copied from AbstractMinecart
    public Vec3 getDismountLocationForPassenger(LivingEntity p_230268_1_) {
        Direction direction = this.getMotionDirection();
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.getDismountLocationForPassenger(p_230268_1_);
        } else {
            int[][] aint = DismountHelper.offsetsForDirection(direction);
            BlockPos blockpos = this.blockPosition();
            BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
            ImmutableList<Pose> immutablelist = p_230268_1_.getDismountPoses();

            for(Pose pose : immutablelist) {
                EntityDimensions entitysize = p_230268_1_.getDimensions(pose);
                float f = Math.min(entitysize.width, 1.0F) / 2.0F;

                for(int i : POSE_DISMOUNT_HEIGHTS.get(pose)) {
                    for(int[] aint1 : aint) {
                        // CHANGED to add height to Y component
                        blockpos$mutable.set(blockpos.getX() + aint1[0], blockpos.getY() + getHeight() + i, blockpos.getZ() + aint1[1]);
                        double d0 = this.level.getBlockFloorHeight(DismountHelper.nonClimbableShape(this.level, blockpos$mutable), () -> {
                            return DismountHelper.nonClimbableShape(this.level, blockpos$mutable.below());
                        });
                        if (DismountHelper.isBlockFloorValid(d0)) {
                            AABB axisalignedbb = new AABB((double)(-f), 0.0D, (double)(-f), (double)f, (double)entitysize.height, (double)f);
                            Vec3 vector3d = Vec3.upFromBottomCenterOf(blockpos$mutable, d0);
                            if (DismountHelper.canDismountTo(this.level, p_230268_1_, axisalignedbb.move(vector3d))) {
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
                int j = Mth.ceil(d1 - (double)blockpos$mutable.getY() + d2);
                double d3 = DismountHelper.findCeilingFrom(blockpos$mutable, j, (p_242377_1_) -> {
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
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        InteractionResult result = super.interact(player, hand);
        //LOGGER.log(org.apache.logging.log4j.Level.WARN, "PISTON PUSHCART INTERACT");
        // Only used when they are too far away for the normal entity interaction packet.
        double distance = this.distanceToSqr(player);
        if (result == InteractionResult.SUCCESS && level.isClientSide && distance >= 36.0D && distance < 175.0) {
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(
                    MoreMinecartsPacketHandler.ExtendedInteractPacket.createExtendedInteractPacket(
                        this, player.isShiftKeyDown(), hand
                    ));
        }
        return result;
    }

    //@Override
    //public ItemStack getCartItem() { return new ItemStack(MMItemReferences.piston_pushcart); }
}
