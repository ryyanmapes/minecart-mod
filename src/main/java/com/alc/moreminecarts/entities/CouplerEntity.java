package com.alc.moreminecarts.entities;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
    private static final double PREFERRED_DISTANCE = 1.6;

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
        super(type, worldIn);
        this.vehicle1 = vehicle1;
        this.vehicle1_id = vehicle1.getEntityId();
        this.vehicle2 = vehicle2;
        this.vehicle2_id = vehicle2.getEntityId();
        this.updateDisplay();
    }

    public CouplerEntity(EntityType<CouplerEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void registerData() { }

    @Nullable
    public Entity getFirstVehicle() {
        if (this.vehicle1 == null && this.vehicle1_id != 0 && this.world.isRemote) {
            this.vehicle1 = this.world.getEntityByID(this.vehicle1_id);
        }
        return this.vehicle1;
    }

    @Nullable
    public Entity getSecondVehicle() {
        if (this.vehicle2 == null && this.vehicle2_id != 0 && this.world.isRemote) {
            this.vehicle2 = this.world.getEntityByID(this.vehicle2_id);
        }
        return this.vehicle2;
    }

    @Override
    public void tick() {

        if (world.isRemote()) {
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

        double distance = vehicle1.getDistance(vehicle2);

        if (distance > 6.0F) {
            this.onBroken(true);
        }
        else {
            Vector3d motion1 = vehicle1.getMotion();
            Vector3d motion2 = vehicle2.getMotion();

            double distance_diff = distance - PREFERRED_DISTANCE;
            lastDiff = distance_diff;

            Vector3d between = vehicle1.getPositionVec().subtract(vehicle2.getPositionVec());
            lastForceX = between.getX();
            lastForceY = between.getY();
            lastForceZ = between.getZ();
            Vector3d force = between.normalize().scale(getSpringForce(distance_diff));


            vehicle1.setMotion(motion1.add(force.scale(-1)));
            vehicle2.setMotion(motion2.add(force.scale(1)));
        }

        super.tick();

        updateDisplay();
    }

    public static double getSpringForce(double distance) {
        boolean is_neg = distance < 0;
        double unsigned = Math.pow( Math.abs(distance), 6) * 0.003;
        return is_neg? -1*unsigned : unsigned;
    }

    public static double getIntegratedSpringForce(double distance) {
        boolean is_neg = distance < 0;
        double unsigned = (1.0/7.0) * Math.pow( Math.abs(distance), 7) * 0.003;
        return is_neg? -1*unsigned : unsigned;
    }

    private void recreateCouple() {
        if (this.vehicleNBTTag == null) return;

        if (this.world instanceof ServerWorld) {

            if (this.vehicleNBTTag.hasUniqueId(TAG_COUPLED_UUID_1)) {
                UUID uuid = this.vehicleNBTTag.getUniqueId(TAG_COUPLED_UUID_1);
                Entity entity = ((ServerWorld)this.world).getEntityByUuid(uuid);
                if (entity != null) {
                    this.vehicle1 = entity;
                    this.vehicle1_id = vehicle1.getEntityId();
                }
            }
            if (this.vehicleNBTTag.hasUniqueId(TAG_COUPLED_UUID_2)) {
                UUID uuid = this.vehicleNBTTag.getUniqueId(TAG_COUPLED_UUID_2);
                Entity entity = ((ServerWorld)this.world).getEntityByUuid(uuid);
                if (entity != null) {
                    this.vehicle2 = entity;
                    this.vehicle2_id = vehicle2.getEntityId();
                }
            }

            if (this.vehicle1 != null && this.vehicle2 != null && !this.world.isRemote) {
                //CouplerPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(()->this), new CouplerPacketHandler.CouplePacket(this.getEntityId(), this.vehicle1_id , this.vehicle2_id));
            }

            if (this.ticksExisted > 100) {
                this.onBroken(true);
                this.vehicleNBTTag = null;
            }

        }
    }

    private void releaseTensionToOne(boolean is_first) {
        Entity to = is_first? vehicle1 : vehicle2;
        int scalar = is_first? 1 : -1;

        Vector3d motion = to.getMotion();
        Vector3d between = new Vector3d(lastForceX, lastForceY, lastForceZ);
        Vector3d force = between.normalize().scale(getIntegratedSpringForce(lastDiff));
        to.setMotion(motion.add(force.scale(scalar)));
    }

    private void releaseTensionToBoth() {
        Vector3d motion1 = vehicle1.getMotion();
        Vector3d motion2 = vehicle2.getMotion();

        Vector3d between = new Vector3d(lastForceX, lastForceY, lastForceZ);
        Vector3d force = between.normalize().scale(getIntegratedSpringForce(lastDiff));

        vehicle1.setMotion(motion1.add(force.scale(0.5)));
        vehicle2.setMotion(motion2.add(force.scale(-0.5)));
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (this.isAlive() && !this.world.isRemote) {
                this.releaseTensionToBoth();
                this.onBroken(!source.isCreativePlayer());
                this.markVelocityChanged();
            }

            return true;
        }
    }

    public void onBroken(boolean drop_item) {
        this.playSound(SoundEvents.BLOCK_CHAIN_BREAK, 1.0F, 1.0F);
        if (drop_item) entityDropItem(coupler);
        this.remove();
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        if (compound.contains(COUPLED_COMPOUND, 10)) {
            this.vehicleNBTTag = compound.getCompound(COUPLED_COMPOUND);
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        if (vehicle1 != null && vehicle2 != null) {
            CompoundNBT new_compound = new CompoundNBT();
            new_compound.putUniqueId(TAG_COUPLED_UUID_1, vehicle1.getUniqueID());
            new_compound.putUniqueId(TAG_COUPLED_UUID_2, vehicle2.getUniqueID());
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
            onBroken(true);
            return;
        }

        Vector3d v1 = ent1.getPositionVec();
        Vector3d v2 = ent2.getPositionVec();
        double x = (v1.x+ v2.x)/2;
        double y = (v1.y + v2.y)/2;
        double z = (v1.z + v2.z)/2;
        this.setPosition(x,y,z);
    }

    public boolean canBeCollidedWith() {
        return true;
    }




    @Override
    public IPacket<?> createSpawnPacket() {
        IPacket<?> packet = NetworkHooks.getEntitySpawningPacket(this);
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer() );
        try {
            packet.writePacketData(buf);
        } catch (IOException e) {
            LOGGER.info("UNABLE TO WRITE TO BUFFER!");
        }
        buf.writeInt(vehicle1_id);
        buf.writeInt(vehicle2_id);
        try {
            packet.readPacketData(buf);
        } catch (IOException e) {
            LOGGER.info("UNABLE TO READ FROM BUFFER!");
        }
        return packet;
    }



}
