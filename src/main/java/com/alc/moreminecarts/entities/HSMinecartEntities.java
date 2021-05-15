package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMItemReferences;
import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.MMConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.UUID;

public class HSMinecartEntities {

    public static boolean upgradeMinecart(AbstractMinecartEntity minecart) {
        double x = minecart.position().x;
        double y = minecart.position().y;
        double z = minecart.position().z;

        AbstractMinecartEntity new_minecart = null;

        if (minecart instanceof IHSCart) return false;
        if (minecart instanceof MinecartEntity) {
            HSMinecart newer_minecart = MMReferences.high_speed_minecart.create(minecart.level);
            for (Entity passenger : minecart.getPassengers()) {
                passenger.stopRiding();
            }
            new_minecart = newer_minecart;
        }
        else if (minecart instanceof ChestMinecartEntity) new_minecart = MMReferences.high_speed_minecart.create(minecart.level);
        else if (minecart instanceof TNTMinecartEntity) new_minecart = MMReferences.high_speed_tnt_minecart.create(minecart.level);
        else if (minecart instanceof CommandBlockMinecartEntity) new_minecart = MMReferences.high_speed_command_block_minecart.create(minecart.level);
        else if (minecart instanceof HopperMinecartEntity) new_minecart = MMReferences.high_speed_hopper_minecart.create(minecart.level);
        else if (minecart instanceof SpawnerMinecartEntity) new_minecart = MMReferences.high_speed_spawner_minecart.create(minecart.level);
        else if (minecart instanceof FurnaceMinecartEntity) new_minecart = MMReferences.high_speed_furnace_minecart.create(minecart.level);
        else if (minecart instanceof NetMinecartEntity) new_minecart = MMReferences.high_speed_net_minecart.create(minecart.level);
        else if (minecart instanceof ChunkLoaderCartEntity) new_minecart = MMReferences.high_speed_chunk_loader_minecart.create(minecart.level);
        else if (minecart instanceof SoulfireCartEntity) new_minecart = MMReferences.high_speed_soulfire_minecart.create(minecart.level);
        else if (minecart instanceof CampfireCartEntity) new_minecart = MMReferences.high_speed_campfire_minecart.create(minecart.level);
        else if (minecart instanceof IronPushcartEntity) {
            HSPushcart newer_minecart = MMReferences.high_speed_pushcart.create(minecart.level);
            for (Entity passenger : minecart.getPassengers()) {
                passenger.stopRiding();
            }
            new_minecart = newer_minecart;
        }
        else return false;

        CompoundNBT data = new CompoundNBT();
        minecart.saveWithoutId(data);
        // Weird workaround to prevent the new minecart taking the UUID of the old.
        UUID true_uuid = new_minecart.getUUID();
        new_minecart.load(data);
        new_minecart.setUUID(true_uuid);

        if (minecart instanceof ContainerMinecartEntity) ((ContainerMinecartEntity) minecart).dropContentsWhenDead(false);
        minecart.remove();

        minecart.level.addFreshEntity(new_minecart);

        return true;
    }

    public static interface IHSCart{};

    public static class HSMinecart extends MinecartEntity implements IHSCart {
        public HSMinecart(EntityType<?> type, World world) {
            super(type, world);
        }
        public HSMinecart(World worldIn, double x, double y, double z) { super(worldIn, x, y, z);}
        @Override
        protected double getMaxSpeed() { return MMConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MMConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSChestMinecart extends ChestMinecartEntity implements IHSCart {
        public HSChestMinecart(EntityType<HSChestMinecart> type, World world) { super(type, world); }
        public HSChestMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        protected double getMaxSpeed() { return MMConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MMConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSTNTMinecart extends TNTMinecartEntity implements IHSCart {
        public HSTNTMinecart(EntityType<? extends TNTMinecartEntity> type, World world) { super(type, world); }
        public HSTNTMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        protected double getMaxSpeed() { return MMConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MMConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSCommandBlockMinecart extends CommandBlockMinecartEntity implements IHSCart {
        public HSCommandBlockMinecart(EntityType<? extends CommandBlockMinecartEntity> type, World world) { super(type, world); }
        public HSCommandBlockMinecart(World worldIn, double x, double y, double z) { super(worldIn, x, y, z); }
        @Override
        protected double getMaxSpeed() { return MMConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MMConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSHopperMinecart extends HopperMinecartEntity implements IHSCart{
        public HSHopperMinecart(EntityType<? extends HopperMinecartEntity> type, World world) { super(type, world); }
        public HSHopperMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        protected double getMaxSpeed() { return MMConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MMConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSSpawnerMinecart extends SpawnerMinecartEntity implements IHSCart {
        public HSSpawnerMinecart(EntityType<? extends SpawnerMinecartEntity> type, World world) { super(type, world); }
        public HSSpawnerMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        protected double getMaxSpeed() { return MMConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MMConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSFurnaceMinecart extends FurnaceMinecartEntity implements IHSCart {
        public HSFurnaceMinecart(EntityType<? extends FurnaceMinecartEntity> type, World world) { super(type, world); }
        public HSFurnaceMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        protected double getMaxSpeed() { return MMConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        // Turned off because it actually makes them slower on maglev rails.
        //@Override
        //protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MoreMinecartsConstants.HS_SLOWDOWN, 0.0D, MoreMinecartsConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MMConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    // Modded high-speed variants

    public static class HSNetMinecart extends NetMinecartEntity implements IHSCart {
        public HSNetMinecart(EntityType<? extends NetMinecartEntity> type, World world) { super(type, world); }
        @Override
        protected double getMaxSpeed() { return MMConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MMConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSChunkLoaderMinecart extends ChunkLoaderCartEntity implements IHSCart {
        public HSChunkLoaderMinecart(EntityType<? extends ChunkLoaderCartEntity> type, World world) { super(type, world); }
        @Override
        protected double getMaxSpeed() { return MMConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MMConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSCampfireMinecart extends CampfireCartEntity implements IHSCart {
        public HSCampfireMinecart(EntityType<? extends CampfireCartEntity> type, World world) { super(type, world); }
        @Override
        protected double getMaxSpeed() { return MMConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        //@Override
        //protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MoreMinecartsConstants.HS_SLOWDOWN, 0.0D, MoreMinecartsConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MMConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSSoulfireMinecart extends SoulfireCartEntity implements IHSCart {
        public HSSoulfireMinecart(EntityType<? extends SoulfireCartEntity> type, World world) { super(type, world); }
        @Override
        protected double getMaxSpeed() { return MMConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        //@Override
        //protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MoreMinecartsConstants.HS_SLOWDOWN, 0.0D, MoreMinecartsConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MMConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSPushcart extends IronPushcartEntity implements IHSCart {
        public HSPushcart(EntityType<? extends IronPushcartEntity> type, World world) { super(type, world); }
        @Override
        protected double getMaxSpeed() { return MMConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MMConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public double getControlSpeed() { return 300; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

}
