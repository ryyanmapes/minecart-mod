package com.alc.moreminecarts.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.*;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("moreminecarts")
public class HighSpeedMinecartEntities {

    public static final Item high_speed_upgrade = null;

    public static void upgradeMinecart(AbstractMinecartEntity minecart) {
        double x = minecart.position().x;
        double y = minecart.position().y;
        double z = minecart.position().z;

        AbstractMinecartEntity new_minecart = null;

        if (minecart instanceof MinecartEntity) new_minecart = new HighSpeedMinecart(minecart.level, x, y, z);
        else if (minecart instanceof ChestMinecartEntity) new_minecart = new HighSpeedChestMinecart(minecart.level, x, y, z);
        else if (minecart instanceof TNTMinecartEntity) new_minecart = new HighSpeedTNTMinecart(minecart.level, x, y, z);
        else if (minecart instanceof CommandBlockMinecartEntity) new_minecart = new HighSpeedCommandBlockMinecart(minecart.level, x, y, z);
        else if (minecart instanceof HopperMinecartEntity) new_minecart = new HighSpeedHopperMinecart(minecart.level, x, y, z);
        else if (minecart instanceof SpawnerMinecartEntity) new_minecart = new HighSpeedSpawnerMinecart(minecart.level, x, y, z);
        // todo modded carts
        else return;

        new_minecart.setYBodyRot(minecart.getYHeadRot()); // todo is this correct?
        new_minecart.setDeltaMovement(minecart.getDeltaMovement());

        minecart.remove();
    }

    public static class HighSpeedMinecart extends MinecartEntity {
        public HighSpeedMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        protected double getMaxSpeed() { return 0.6; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
    }

    public static class HighSpeedChestMinecart extends ChestMinecartEntity {
        public HighSpeedChestMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        protected double getMaxSpeed() {
            return 0.6;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
    }

    public static class HighSpeedTNTMinecart extends TNTMinecartEntity {
        public HighSpeedTNTMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        protected double getMaxSpeed() {
            return 0.6;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
    }

    public static class HighSpeedCommandBlockMinecart extends CommandBlockMinecartEntity {
        public HighSpeedCommandBlockMinecart(World worldIn, double x, double y, double z) { super(worldIn, x, y, z); }
        @Override
        protected double getMaxSpeed() { return 0.6; }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
    }

    public static class HighSpeedHopperMinecart extends HopperMinecartEntity {
        public HighSpeedHopperMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        protected double getMaxSpeed() {
            return 0.6;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
    }

    public static class HighSpeedSpawnerMinecart extends SpawnerMinecartEntity {
        public HighSpeedSpawnerMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        protected double getMaxSpeed() {
            return 0.6;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
    }

    // TODO furnace minecart

    // Modded high-speed variants

    public static class HighSpeedNetMinecart extends MinecartWithNet {
        public HighSpeedNetMinecart(EntityType<?> type, World worldIn, double x, double y, double z) { super(type, worldIn, x, y, z); }
        @Override
        protected double getMaxSpeed() {
            return 0.6;
        }
        @Override
        public void destroy(DamageSource source) {
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.spawnAtLocation(high_speed_upgrade);
        }
    }

    // TODO campfire carts

}
