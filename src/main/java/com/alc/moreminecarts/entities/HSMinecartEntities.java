package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.MMItemReferences;
import com.alc.moreminecarts.MMReferences;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.UUID;

public class HSMinecartEntities {

    public static boolean upgradeMinecart(AbstractMinecart minecart) {
        double x = minecart.position().x;
        double y = minecart.position().y;
        double z = minecart.position().z;

        AbstractMinecart new_minecart = null;

        if (minecart instanceof IHSCart) return false;
        if (minecart instanceof Minecart) {
            HSMinecart newer_minecart = MMReferences.high_speed_minecart.create(minecart.level);
            for (Entity passenger : minecart.getPassengers()) {
                passenger.stopRiding();
            }
            new_minecart = newer_minecart;
        }
        else if (minecart instanceof MinecartChest) new_minecart = MMReferences.high_speed_chest_minecart.create(minecart.level);
        else if (minecart instanceof MinecartTNT) new_minecart = MMReferences.high_speed_tnt_minecart.create(minecart.level);
        else if (minecart instanceof MinecartCommandBlock) new_minecart = MMReferences.high_speed_command_block_minecart.create(minecart.level);
        else if (minecart instanceof MinecartHopper) new_minecart = MMReferences.high_speed_hopper_minecart.create(minecart.level);
        else if (minecart instanceof MinecartSpawner) new_minecart = MMReferences.high_speed_spawner_minecart.create(minecart.level);
        else if (minecart instanceof MinecartFurnace) new_minecart = MMReferences.high_speed_furnace_minecart.create(minecart.level);
        else if (minecart instanceof NetMinecartEntity) new_minecart = MMReferences.high_speed_net_minecart.create(minecart.level);
        else if (minecart instanceof ChunkLoaderCartEntity) new_minecart = MMReferences.high_speed_chunk_loader_minecart.create(minecart.level);
        else if (minecart instanceof OrbStasisCart) new_minecart = MMReferences.high_speed_stasis_minecart.create(minecart.level);
        else if (minecart instanceof FlagCartEntity) new_minecart = MMReferences.high_speed_flag_minecart.create(minecart.level);
        else if (minecart instanceof TankCartEntity) new_minecart = MMReferences.high_speed_tank_minecart.create(minecart.level);
        else if (minecart instanceof BatteryCartEntity) new_minecart = MMReferences.high_speed_battery_minecart.create(minecart.level);
        else if (minecart instanceof EndfireCartEntity) new_minecart = MMReferences.high_speed_endfire_minecart.create(minecart.level);
        else if (minecart instanceof SoulfireCartEntity) new_minecart = MMReferences.high_speed_soulfire_minecart.create(minecart.level);
        else if (minecart instanceof CampfireCartEntity) new_minecart = MMReferences.high_speed_campfire_minecart.create(minecart.level);
        else if (minecart instanceof StickyPistonPushcartEntity) new_minecart = MMReferences.high_speed_sticky_piston_pushcart.create(minecart.level);
        else if (minecart instanceof PistonPushcartEntity) new_minecart = MMReferences.high_speed_piston_pushcart.create(minecart.level);
        else if (minecart instanceof IronPushcartEntity) new_minecart = MMReferences.high_speed_pushcart.create(minecart.level);
        else return false;

        if (minecart instanceof IronPushcartEntity) {
            for (Entity passenger : minecart.getPassengers()) {
                passenger.stopRiding();
            }
        }

        CompoundTag data = new CompoundTag();
        minecart.saveWithoutId(data);
        // Weird workaround to prevent the new minecart taking the UUID of the old.
        UUID true_uuid = new_minecart.getUUID();
        new_minecart.load(data);
        new_minecart.setUUID(true_uuid);

        // does something like this still need to occur?
        //if (minecart instanceof AbstractMinecartContainer) ((AbstractMinecartContainer) minecart).dropContentsWhenDead(false);

        minecart.setRemoved(Entity.RemovalReason.DISCARDED);

        minecart.level.addFreshEntity(new_minecart);

        return true;
    }

    public static interface IHSCart{};

    public static class HSMinecart extends Minecart implements IHSCart {
        public HSMinecart(EntityType<?> type, Level world) {
            super(type, world);
        }
        public HSMinecart(Level worldIn, double x, double y, double z) { super(worldIn, x, y, z);}
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSChestMinecart extends MinecartChest implements IHSCart {
        public HSChestMinecart(EntityType<HSChestMinecart> type, Level world) { super(type, world); }
        public HSChestMinecart(Level worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSTNTMinecart extends MinecartTNT implements IHSCart {
        public HSTNTMinecart(EntityType<? extends MinecartTNT> type, Level world) { super(type, world); }
        public HSTNTMinecart(Level worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSCommandBlockMinecart extends MinecartCommandBlock implements IHSCart {
        public HSCommandBlockMinecart(EntityType<? extends MinecartCommandBlock> type, Level world) { super(type, world); }
        public HSCommandBlockMinecart(Level worldIn, double x, double y, double z) { super(worldIn, x, y, z); }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSHopperMinecart extends MinecartHopper implements IHSCart{
        public HSHopperMinecart(EntityType<? extends MinecartHopper> type, Level world) { super(type, world); }
        public HSHopperMinecart(Level worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSSpawnerMinecart extends MinecartSpawner implements IHSCart {
        public HSSpawnerMinecart(EntityType<? extends MinecartSpawner> type, Level world) { super(type, world); }
        public HSSpawnerMinecart(Level worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSFurnaceMinecart extends MinecartFurnace implements IHSCart {
        public HSFurnaceMinecart(EntityType<? extends MinecartFurnace> type, Level world) { super(type, world); }
        public HSFurnaceMinecart(Level worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }

        @Override
        protected void applyNaturalSlowdown() {
            double d0 = this.xPush * this.xPush + this.zPush * this.zPush;
            if (d0 > 1.0E-7D) {
                d0 = (double) Math.sqrt(d0);
                this.xPush /= d0;
                this.zPush /= d0;
                this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN).add(this.xPush, 0.0D, this.zPush));
            } else {
                this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN));
            }
        }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        protected double getMaxSpeed() {
            return MMConstants.LIGHTSPEED_MAX_SPEED;
        }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    // Modded high-speed variants

    public static class HSNetMinecart extends NetMinecartEntity implements IHSCart {
        public HSNetMinecart(EntityType<? extends NetMinecartEntity> type, Level world) { super(type, world); }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSChunkLoaderMinecart extends ChunkLoaderCartEntity implements IHSCart {
        public HSChunkLoaderMinecart(EntityType<? extends ChunkLoaderCartEntity> type, Level world) { super(type, world); }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSStasisMinecart extends OrbStasisCart implements IHSCart {
        public HSStasisMinecart(EntityType<? extends OrbStasisCart> type, Level world) { super(type, world); }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSFlagMinecart extends FlagCartEntity implements IHSCart {
        public HSFlagMinecart(EntityType<? extends FlagCartEntity> type, Level world) { super(type, world); }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSTankMinecart extends TankCartEntity implements IHSCart {
        public HSTankMinecart(EntityType<? extends TankCartEntity> type, Level world) { super(type, world); }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSBatteryMinecart extends BatteryCartEntity implements IHSCart {
        public HSBatteryMinecart(EntityType<? extends BatteryCartEntity> type, Level world) { super(type, world); }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSCampfireMinecart extends CampfireCartEntity implements IHSCart {
        public HSCampfireMinecart(EntityType<? extends CampfireCartEntity> type, Level world) { super(type, world); }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        //@Override
        //protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MoreMinecartsConstants.HS_SLOWDOWN, 0.0D, MoreMinecartsConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSSoulfireMinecart extends SoulfireCartEntity implements IHSCart {
        public HSSoulfireMinecart(EntityType<? extends SoulfireCartEntity> type, Level world) { super(type, world); }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        //@Override
        //protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MoreMinecartsConstants.HS_SLOWDOWN, 0.0D, MoreMinecartsConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSEndfireMinecart extends EndfireCartEntity implements IHSCart {
        public HSEndfireMinecart(EntityType<? extends EndfireCartEntity> type, Level world) { super(type, world); }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        //@Override
        //protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MoreMinecartsConstants.HS_SLOWDOWN, 0.0D, MoreMinecartsConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSPushcart extends IronPushcartEntity implements IHSCart {
        public HSPushcart(EntityType<? extends IronPushcartEntity> type, Level world) { super(type, world); }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public double getControlSpeed() { return 300; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSPistonPushcart extends PistonPushcartEntity implements IHSCart {
        public HSPistonPushcart(EntityType<? extends PistonPushcartEntity> type, Level world) { super(type, world); }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public double getControlSpeed() { return 300; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
        @Override
        public float getVerticalSpeed() {
            return MMConstants.PISTON_PUSHCART_AERODYNAMIC_VERTICAL_SPEED;
        }
    }

    public static class HSStickyPistonPushcart extends StickyPistonPushcartEntity implements IHSCart {
        public HSStickyPistonPushcart(EntityType<? extends StickyPistonPushcartEntity> type, Level world) { super(type, world); }
        @Override
        public double getMaxSpeedWithRail() {
            double max_speed = super.getMaxSpeedWithRail();
            this.setMaxSpeedAirLateral((float) max_speed);
            this.setMaxSpeedAirVertical((float) max_speed);
            return max_speed;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(MMItemReferences.high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MMConstants.HS_SLOWDOWN, 0.0D, MMConstants.HS_SLOWDOWN)); }
        @Override
        public double getDragAir() { return MMConstants.HS_AIR_DRAG; }
        @Override
        public double getControlSpeed() { return 300; }
        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
        @Override
        public float getVerticalSpeed() {
            return MMConstants.PISTON_PUSHCART_AERODYNAMIC_VERTICAL_SPEED;
        }
    }

}
