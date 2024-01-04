package com.alc.moreminecarts.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;


// Pushcarts can't have entities besides players on them, so we always return canBeRidden as false,
// but we force it if it's a player
public abstract class AbstractPushcart extends AbstractMinecart {

    public AbstractPushcart(EntityType<?> type, Level world) {
        super(type, world);
    }

    public AbstractPushcart(EntityType<?> type, Level worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        InteractionResult ret = super.interact(player, hand);
        if (ret.consumesAction()) return ret;
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        else if (this.isVehicle()) {
            return InteractionResult.PASS; }
        else if (!this.level().isClientSide) {
            return player.startRiding(this, false) ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    /*
    TODO is this needed?
    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return this.getPassengers().size() < 1 && passenger instanceof Player;
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
        return AbstractMinecart.Type.RIDEABLE;
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
        if (entity instanceof Player) {

            Vec3 motion = entity.getDeltaMovement();
            double speed = Math.sqrt((motion.x * motion.x) + (motion.z * motion.z));
            if (speed <= 0.0001) {


                Vec3 our_motion = this.getDeltaMovement();
                this.setDeltaMovement(our_motion.x * this.getBrakeSpeed(), our_motion.y, our_motion.z * this.getBrakeSpeed());
            } else {

                int i = Mth.floor(this.position().x);
                int j = Mth.floor(this.position().y);
                int k = Mth.floor(this.position().z);

                BlockPos pos = new BlockPos(i, j, k);
                BlockState state = this.level().getBlockState(pos);
                if (RailBlock.isRail(state)) {
                    RailShape railshape = ((BaseRailBlock) state.getBlock()).getRailDirection(state, this.level(), pos, this);

                    boolean is_uphill = (railshape == RailShape.ASCENDING_EAST || railshape == RailShape.ASCENDING_WEST
                            || railshape == RailShape.ASCENDING_NORTH || railshape == RailShape.ASCENDING_SOUTH);

                    double controlSpeed = is_uphill? this.getUphillSpeed() : this.getControlSpeed();
                    entity.setDeltaMovement(motion.scale(controlSpeed));

                }
            }

        }

        super.tick();

        if (entity instanceof Player)  {
            entity.setDeltaMovement(Vec3.ZERO);
        }
    }


}
