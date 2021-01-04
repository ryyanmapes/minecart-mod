package com.example.examplemod.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
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

    // todo somehow make player control more effective? where is that even done in the code?

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
    @Override
    public double getMountedYOffset() {
        return 0.2875D;
    }


    @Override
    public IPacket<?> createSpawnPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
