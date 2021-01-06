package com.example.examplemod.entities;

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
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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

    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        ActionResultType ret = super.processInitialInteract(player, hand);
        if (ret.isSuccessOrConsume()) return ret;
        if (player.isSecondaryUseActive()) {
            return ActionResultType.PASS;
        }
        else if (this.isBeingRidden()) {
            return ActionResultType.PASS; }
        else if (!this.world.isRemote) {
            return player.startRiding(this, true) ? ActionResultType.CONSUME : ActionResultType.PASS;
        } else {
            return ActionResultType.SUCCESS;
        }
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return false;
    }

    /**
     * Called every tick the minecart is on an activator rail.
     */
    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
        if (receivingPower) {
            if (this.isBeingRidden()) {
                this.removePassengers();
            }
            // No clue what this is for
            if (this.getRollingAmplitude() == 0) {
                this.setRollingDirection(-this.getRollingDirection());
                this.setRollingAmplitude(10);
                this.setDamage(50.0F);
                this.markVelocityChanged();
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


    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    // Input Stuff

    public abstract double getControlSpeed() ;
    public abstract double getUphillSpeed() ;
    public abstract double getBrakeSpeed() ;

    @Override
    public void tick() {


        Entity entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
        if (entity instanceof PlayerEntity) {
            Vector3d motion = entity.getMotion();
            double speed = Math.sqrt((motion.x * motion.x) + (motion.z * motion.z));
            if (speed <= 0.0001) {
                entity.setMotion(Vector3d.ZERO);

                Vector3d our_motion = this.getMotion();
                this.setMotion(our_motion.x * this.getBrakeSpeed(), our_motion.y, our_motion.z * this.getBrakeSpeed());
            } else {

                int i = MathHelper.floor(this.getPosX());
                int j = MathHelper.floor(this.getPosY());
                int k = MathHelper.floor(this.getPosZ());

                BlockPos pos = new BlockPos(i, j, k);
                BlockState state = this.world.getBlockState(pos);
                if (AbstractRailBlock.isRail(state)) {
                    RailShape railshape = ((AbstractRailBlock) state.getBlock()).getRailDirection(state, this.world, pos, this);

                    boolean is_uphill = (railshape != RailShape.ASCENDING_EAST && railshape != RailShape.ASCENDING_WEST
                            && railshape != RailShape.ASCENDING_NORTH && railshape != RailShape.ASCENDING_SOUTH);

                    double controlSpeed = is_uphill? this.getControlSpeed() : this.getUphillSpeed();
                    entity.setMotion(motion.scale(controlSpeed));

                }
            }
        }


        super.tick();
    }
}
