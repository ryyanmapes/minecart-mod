package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.misc.MoreMinecartsPacketHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.UUID;

@ObjectHolder("moreminecarts")
public class CouplerEntity extends Entity {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String COUPLED_COMPOUND = "Couples";
    private static final String TAG_COUPLED_UUID_1 = "coupled_UUID_1";
    private static final String TAG_COUPLED_UUID_2 = "coupled_UUID_2";
    private static final double PREFERRED_DISTANCE = 2;

    private CompoundNBT vehicleNBTTag;

    public static final Item coupler = null;

    public Entity vehicle1;
    public int vehicle1_id;
    public Entity vehicle2;
    public int vehicle2_id;
    public double lastForceX = 0;
    public double lastForceY = 0;
    public double lastForceZ = 0;
    public double lastDiff = 0;

    public CouplerEntity(EntityType<?> type, World worldIn, Entity vehicle1, Entity vehicle2) {
        super( type, worldIn);
        this.vehicle1 = vehicle1;
        this.vehicle1_id = vehicle1.getId();
        this.vehicle2 = vehicle2;
        this.vehicle2_id = vehicle2.getId();
        this.updateDisplay();
    }

    public CouplerEntity(EntityType<CouplerEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Nullable
    public Entity getFirstVehicle() {
        if (this.vehicle1 == null && this.vehicle1_id != 0 && this.level.isClientSide) {
            this.vehicle1 = this.level.getEntity(this.vehicle1_id);
        }
        return this.vehicle1;
    }

    @Nullable
    public Entity getSecondVehicle() {
        if (this.vehicle2 == null && this.vehicle2_id != 0 && this.level.isClientSide) {
            this.vehicle2 = this.level.getEntity(this.vehicle2_id);
        }
        return this.vehicle2;
    }

    @Override
    public void tick() {

        if (level.isClientSide) {
            updateDisplay();
            return;
        }

        if (vehicle1 == null || vehicle2 == null) {
            recreateCouple();
            return;
        }

        if (!vehicle1.isAlive() || !vehicle2.isAlive()) {
            if (vehicle1.isAlive()) releaseTensionToOne(true);
            if (vehicle2.isAlive()) releaseTensionToOne(false);
            this.onBroken(true);
        }

        double distance = vehicle1.distanceTo(vehicle2);

        if (distance > 6.0F) {
            this.onBroken(true);
        }
        else {
            Vector3d motion1 = vehicle1.getDeltaMovement();
            Vector3d motion2 = vehicle2.getDeltaMovement();

            double distance_diff = distance - PREFERRED_DISTANCE;
            //if (distance_diff < 0) distance_diff = 0;

            lastDiff = distance_diff;

            Vector3d between = vehicle1.position().subtract(vehicle2.position());
            lastForceX = between.x;
            lastForceY = between.y;
            lastForceZ = between.z;
            Vector3d force = between.normalize().scale(getSpringForce(distance_diff));

            vehicle1.setDeltaMovement(motion1.add(force.scale(-1 * getEntityForceScale(vehicle1) )));
            vehicle2.setDeltaMovement(motion2.add(force.scale( 1 * getEntityForceScale(vehicle2) )));

            vehicle1.setDeltaMovement(vehicle1.getDeltaMovement().scale(0.95));
            vehicle2.setDeltaMovement(vehicle2.getDeltaMovement().scale(0.95));

        }

        super.tick();

        updateDisplay();
    }


    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() { return false; }

    @Override
    public boolean canCollideWith(Entity p_241849_1_) { return false; }

    @Override
    public PushReaction getPistonPushReaction() { return PushReaction.IGNORE; }

    public boolean isPickable() {
        return !this.removed;
    }

    public static double getEntityForceScale(Entity ent) {
        if (ent instanceof AbstractMinecartEntity) {
            if ( ((AbstractMinecartEntity) ent).getMinecartType() == AbstractMinecartEntity.Type.FURNACE) return 0.1;
        }
        return 1;
    }

    public static double getSpringForce(double distance) {
        boolean is_neg = distance < 0;
        double unsigned = Math.abs(Math.pow( Math.abs(distance), 3) * 0.3);
        return is_neg? -1*unsigned : unsigned;
    }

    public static double getIntegratedSpringForce(double distance) {
        boolean is_neg = distance < 0;
        double unsigned = Math.abs((1.0/3.0) * Math.pow( Math.abs(distance), 4) * 0.3);
        return is_neg? -1*unsigned : unsigned;
    }



    private void recreateCouple() {
        if (this.vehicleNBTTag == null) return;

        if (this.level instanceof ServerWorld) {

            if (this.vehicleNBTTag.hasUUID(TAG_COUPLED_UUID_1)) {
                UUID uuid = this.vehicleNBTTag.getUUID(TAG_COUPLED_UUID_1);
                Entity entity = ((ServerWorld)this.level).getEntity(uuid);
                if (entity != null) {
                    this.vehicle1 = entity;
                    this.vehicle1_id = vehicle1.getId();
                }
            }
            if (this.vehicleNBTTag.hasUUID(TAG_COUPLED_UUID_2)) {
                UUID uuid = this.vehicleNBTTag.getUUID(TAG_COUPLED_UUID_2);
                Entity entity = ((ServerWorld)this.level).getEntity(uuid);
                if (entity != null) {
                    this.vehicle2 = entity;
                    this.vehicle2_id = vehicle2.getId();
                }
            }

            if (this.vehicle1 != null && this.vehicle2 != null) {
                MoreMinecartsPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(()->this), new MoreMinecartsPacketHandler.CouplePacket(this.getId(), this.vehicle1_id , this.vehicle2_id));
            }

            if (this.tickCount > 100) {
                this.onBroken(true);
                this.vehicleNBTTag = null;
            }

        }
    }

    private void releaseTensionToOne(boolean is_first) {
        Entity to = is_first? vehicle1 : vehicle2;
        int scalar = is_first? 1 : -1;

        Vector3d motion = to.getDeltaMovement();
        Vector3d between = new Vector3d(lastForceX, lastForceY, lastForceZ);
        Vector3d force = between.normalize().scale(getIntegratedSpringForce(lastDiff));
        to.setDeltaMovement(motion.add(force.scale(scalar)));
    }

    private void releaseTensionToBoth() {
        Vector3d motion1 = vehicle1.getDeltaMovement();
        Vector3d motion2 = vehicle2.getDeltaMovement();

        Vector3d between = new Vector3d(lastForceX, lastForceY, lastForceZ);
        Vector3d force = between.normalize().scale(getIntegratedSpringForce(lastDiff));

        vehicle1.setDeltaMovement(motion1.add(force.scale(0.5)));
        vehicle2.setDeltaMovement(motion2.add(force.scale(-0.5)));
    }

    @Override
    public boolean skipAttackInteraction(Entity player) {
        if (player instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)player;
            return !this.level.mayInteract(playerentity, this.getOnPos()) ? true : this.hurt(DamageSource.playerAttack(playerentity), 0.0F);
        } else {
            return false;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (this.isAlive() && !this.level.isClientSide) {
                this.releaseTensionToBoth();
                this.onBroken(!source.isCreativePlayer());
                this.markHurt();
            }

            return true;
        }
    }

    public void onBroken(boolean drop_item) {
        this.playSound(SoundEvents.CHAIN_BREAK, 1.0F, 1.0F);
        if (drop_item) spawnAtLocation(coupler);
        this.remove();
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        if (compound.contains(COUPLED_COMPOUND, 10)) {
            this.vehicleNBTTag = compound.getCompound(COUPLED_COMPOUND);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        if (vehicle1 != null && vehicle2 != null) {
            CompoundNBT new_compound = new CompoundNBT();
            new_compound.putUUID(TAG_COUPLED_UUID_1, vehicle1.getUUID());
            new_compound.putUUID(TAG_COUPLED_UUID_2, vehicle2.getUUID());
            compound.put(COUPLED_COMPOUND, new_compound);
        }
        else if (this.vehicleNBTTag != null) {
            compound.put(COUPLED_COMPOUND, this.vehicleNBTTag.copy());
        }
    }

    protected void updateDisplay() {
        Entity ent1 = getFirstVehicle();
        Entity ent2 = getSecondVehicle();

        if (ent1 == null || ent2 == null) {
            return;
        }

        Vector3d v1 = ent1.position();
        Vector3d v2 = ent2.position();
        double x = (v1.x + v2.x)/2;
        double y = (v1.y + v2.y)/2;
        double z = (v1.z + v2.z)/2;
        this.setPos(x,y,z);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        IPacket<?> packet = NetworkHooks.getEntitySpawningPacket(this);
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer() );
        try {
            packet.write(buf);
        } catch (IOException e) {
            LOGGER.info("UNABLE TO WRITE TO BUFFER!");
        }
        buf.writeInt(vehicle1_id);
        buf.writeInt(vehicle2_id);
        try {
            packet.read(buf);
        } catch (IOException e) {
            LOGGER.info("UNABLE TO READ FROM BUFFER!");
        }
        return packet;
    }



}
