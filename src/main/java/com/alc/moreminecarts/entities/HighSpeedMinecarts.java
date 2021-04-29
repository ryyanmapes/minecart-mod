package com.alc.moreminecarts.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.entity.item.minecart.FurnaceMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.world.World;

public class HighSpeedMinecarts {

    public class HighSpeedMinecart extends MinecartEntity {
        public HighSpeedMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        public HighSpeedMinecart(EntityType<?> type, World world) {
            super(type, world);
        }
        @Override
        protected double getMaxSpeed() { return 0.6; }
    }

    public class HighSpeedChestMinecart extends ChestMinecartEntity {
        public HighSpeedChestMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        protected double getMaxSpeed() {
            return 0.6;
        }
    }

    public class HighSpeedTNTMinecart extends TNTMinecartEntity {
        public HighSpeedTNTMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        protected double getMaxSpeed() {
            return 0.6;
        }
    }

    public class HighSpeedFurnaceMinecart extends FurnaceMinecartEntity {
        public HighSpeedFurnaceMinecart(World worldIn, double x, double y, double z) {
            super(worldIn, x, y, z);
        }
        @Override
        protected double getMaxSpeed() {
            return 0.6;
        }
    }

}
