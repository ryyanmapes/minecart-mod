package com.alc.moreminecarts.entities;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;


// Pushcarts can't have entities besides players on them, so we always return canBeRidden as false,
// but we force it if it's a player
public abstract class AbstractPushcart extends AbstractMinecartEntity {

    public AbstractPushcart(EntityType<?> type, World world) {
        super(type, world);
    }

    public AbstractPushcart(EntityType<?> type, World worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    public ActionResultType interact(PlayerEntity player, Hand hand) {
        ActionResultType ret = super.interact(player, hand);
        if (ret.consumesAction()) return ret;
        if (player.isSecondaryUseActive()) {
            return ActionResultType.PASS;
        }
        else if (this.isVehicle()) {
            return ActionResultType.PASS; }
        else if (!this.level.isClientSide) {
            return player.startRiding(this, false) ? ActionResultType.CONSUME : ActionResultType.PASS;
        } else {
            return ActionResultType.SUCCESS;
        }
    }

    /*
    TODO is this needed?
    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return this.getPassengers().size() < 1 && passenger instanceof PlayerEntity;
    }*/

    /**
     * Called every tick the minecart is on an activator rail.
     */
    @Override
    public void activateMinecart(int x, int y, int z, boolean receivingPower) {
        if (receivingPower) {
            if (this.isVehicle()) {
                this.ejectPassengers();
            }

            // No clue what this is for
            // Actually, with these names, it makes a bit more sense.
            if (this.getHurtTime() == 0) {
                this.setHurtDir(-this.getHurtDir());
                this.setHurtTime(10);
                this.setDamage(50.0F);
                this.markHurt();
            }
        }

    }

    @Override
    public Type getMinecartType() {
        return AbstractMinecartEntity.Type.RIDEABLE;
    }

    // todo should this be higher? so it's closer to actual standing?
    // sure but requires some modeling modification to look good
    //@Override
    //public double getMountedYOffset() {
        //return 0.2875D;
    //}

    // Input Stuff

    public abstract double getControlSpeed() ;
    public abstract double getUphillSpeed() ;
    public abstract double getBrakeSpeed() ;

    @Override
    public void tick() {

        Entity entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
        if (entity instanceof PlayerEntity) {

            Vector3d motion = entity.getDeltaMovement();
            double speed = Math.sqrt((motion.x * motion.x) + (motion.z * motion.z));
            if (speed <= 0.0001) {


                Vector3d our_motion = this.getDeltaMovement();
                this.setDeltaMovement(our_motion.x * this.getBrakeSpeed(), our_motion.y, our_motion.z * this.getBrakeSpeed());
            } else {

                int i = MathHelper.floor(this.position().x);
                int j = MathHelper.floor(this.position().y);
                int k = MathHelper.floor(this.position().z);

                BlockPos pos = new BlockPos(i, j, k);
                BlockState state = this.level.getBlockState(pos);
                if (AbstractRailBlock.isRail(state)) {
                    RailShape railshape = ((AbstractRailBlock) state.getBlock()).getRailDirection(state, this.level, pos, this);

                    boolean is_uphill = (railshape == RailShape.ASCENDING_EAST || railshape == RailShape.ASCENDING_WEST
                            || railshape == RailShape.ASCENDING_NORTH || railshape == RailShape.ASCENDING_SOUTH);

                    double controlSpeed = is_uphill? this.getUphillSpeed() : this.getControlSpeed();
                    entity.setDeltaMovement(motion.scale(controlSpeed));

                }
            }

        }

        super.tick();

        if (entity instanceof PlayerEntity)  {
            entity.setDeltaMovement(Vector3d.ZERO);
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
