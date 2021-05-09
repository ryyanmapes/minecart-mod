package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MoreMinecartsConstants;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.*;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

import java.util.UUID;

@ObjectHolder("moreminecarts")
public class HSMinecartEntities {

    public static final Item high_speed_upgrade = null;

    public static final EntityType<HSMinecart> high_speed_minecart = null;
    public static final EntityType<HSChestMinecart> high_speed_chest_minecart = null;
    public static final EntityType<HSTNTMinecart> high_speed_tnt_minecart = null;
    public static final EntityType<HSCommandBlockMinecart> high_speed_command_block_minecart = null;
    public static final EntityType<HSHopperMinecart> high_speed_hopper_minecart = null;
    public static final EntityType<HighSpeedSpawnerMinecart> high_speed_spawner_minecart = null;
    public static final EntityType<HighSpeedFurnaceMinecart> high_speed_furnace_minecart = null;
    public static final EntityType<HighSpeedNetMinecart> high_speed_net_minecart = null;
    public static final EntityType<HighSpeedChunkLoaderMinecart> high_speed_chunk_loader_minecart = null;

    public static boolean upgradeMinecart(AbstractMinecartEntity minecart) {
        double x = minecart.position().x;
        double y = minecart.position().y;
        double z = minecart.position().z;

        AbstractMinecartEntity new_minecart = null;

        if (minecart instanceof MinecartEntity) {
            new_minecart = high_speed_minecart.create(minecart.level);
            /*HSMinecart newer_minecart = high_speed_minecart.create(minecart.level);
            for (Entity passenger : minecart.getPassengers()) {
                passenger.stopRiding();
                passenger.startRiding(newer_minecart);
            }
            new_minecart = newer_minecart;*/
        }
        else if (minecart instanceof ChestMinecartEntity) {
            new_minecart = high_speed_chest_minecart.create(minecart.level);
            /*HSChestMinecart newer_minecart = high_speed_chest_minecart.create(minecart.level);
            Iterator<ItemStack> all_items = minecart.getAllSlots().iterator();
            for (int i = 0; i < ((ChestMinecartEntity) minecart).getContainerSize(); i++) {
                ItemStack stack = all_items.next();
                if (stack != ItemStack.EMPTY) newer_minecart.canPlaceItem()
            }*/
        }
        else if (minecart instanceof TNTMinecartEntity) new_minecart = high_speed_tnt_minecart.create(minecart.level);
        else if (minecart instanceof CommandBlockMinecartEntity) new_minecart = high_speed_command_block_minecart.create(minecart.level);
        else if (minecart instanceof HopperMinecartEntity) new_minecart = high_speed_hopper_minecart.create(minecart.level);
        else if (minecart instanceof SpawnerMinecartEntity) new_minecart = high_speed_spawner_minecart.create(minecart.level);
        else if (minecart instanceof FurnaceMinecartEntity) new_minecart = high_speed_furnace_minecart.create(minecart.level);
        else if (minecart instanceof NetMinecartEntity) new_minecart = high_speed_net_minecart.create(minecart.level);
        else if (minecart instanceof ChunkLoaderCartEntity) new_minecart = high_speed_chunk_loader_minecart.create(minecart.level);
        // todo campfire carts
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
        protected double getMaxSpeed() { return MoreMinecartsConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MoreMinecartsConstants.HS_SLOWDOWN, 0.0D, MoreMinecartsConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MoreMinecartsConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MoreMinecartsConstants.HS_AIR_DRAG; }
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
        protected double getMaxSpeed() { return MoreMinecartsConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MoreMinecartsConstants.HS_SLOWDOWN, 0.0D, MoreMinecartsConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MoreMinecartsConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MoreMinecartsConstants.HS_AIR_DRAG; }
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
        protected double getMaxSpeed() { return MoreMinecartsConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MoreMinecartsConstants.HS_SLOWDOWN, 0.0D, MoreMinecartsConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MoreMinecartsConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MoreMinecartsConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HSCommandBlockMinecart extends CommandBlockMinecartEntity implements IHSCart {
        public HSCommandBlockMinecart(EntityType<? extends CommandBlockMinecartEntity> type, World world) { super(type, world); }
        public HSCommandBlockMinecart(World worldIn, double x, double y, double z) { super(worldIn, x, y, z); }
        @Override
        protected double getMaxSpeed() { return MoreMinecartsConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MoreMinecartsConstants.HS_SLOWDOWN, 0.0D, MoreMinecartsConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MoreMinecartsConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MoreMinecartsConstants.HS_AIR_DRAG; }
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
        protected double getMaxSpeed() { return MoreMinecartsConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MoreMinecartsConstants.HS_SLOWDOWN, 0.0D, MoreMinecartsConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MoreMinecartsConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MoreMinecartsConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HighSpeedSpawnerMinecart extends SpawnerMinecartEntity implements IHSCart {
        public HighSpeedSpawnerMinecart(EntityType<? extends SpawnerMinecartEntity> type, World world) { super(type, world); }
        public HighSpeedSpawnerMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        protected double getMaxSpeed() { return MoreMinecartsConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MoreMinecartsConstants.HS_SLOWDOWN, 0.0D, MoreMinecartsConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MoreMinecartsConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MoreMinecartsConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HighSpeedFurnaceMinecart extends FurnaceMinecartEntity implements IHSCart {
        public HighSpeedFurnaceMinecart(EntityType<? extends FurnaceMinecartEntity> type, World world) { super(type, world); }
        public HighSpeedFurnaceMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        protected double getMaxSpeed() { return MoreMinecartsConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
        // Turned off because it actually makes them slower on maglev rails.
        //@Override
        //protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MoreMinecartsConstants.HS_SLOWDOWN, 0.0D, MoreMinecartsConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MoreMinecartsConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MoreMinecartsConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    // Modded high-speed variants

    public static class HighSpeedNetMinecart extends NetMinecartEntity implements IHSCart {
        public HighSpeedNetMinecart(EntityType<? extends NetMinecartEntity> type, World world) { super(type, world); }
        @Override
        protected double getMaxSpeed() { return MoreMinecartsConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MoreMinecartsConstants.HS_SLOWDOWN, 0.0D, MoreMinecartsConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MoreMinecartsConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MoreMinecartsConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public static class HighSpeedChunkLoaderMinecart extends ChunkLoaderCartEntity implements IHSCart {
        public HighSpeedChunkLoaderMinecart(EntityType<? extends ChunkLoaderCartEntity> type, World world) { super(type, world); }
        @Override
        protected double getMaxSpeed() { return MoreMinecartsConstants.HS_MAX_SPEED; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
        @Override
        protected void applyNaturalSlowdown() { this.setDeltaMovement(this.getDeltaMovement().multiply(MoreMinecartsConstants.HS_SLOWDOWN, 0.0D, MoreMinecartsConstants.HS_SLOWDOWN)); }
        @Override
        public float getMaxSpeedAirLateral() { return MoreMinecartsConstants.HS_FLYING_MAX_SPEED; }
        @Override
        public double getDragAir() { return MoreMinecartsConstants.HS_AIR_DRAG; }
        @Override
        public IPacket<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    // TODO campfire carts

}
