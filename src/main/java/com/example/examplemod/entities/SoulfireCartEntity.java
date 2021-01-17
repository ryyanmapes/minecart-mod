package com.example.examplemod.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.FurnaceMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Random;

public class SoulfireCartEntity extends CampfireCartEntity {


    public SoulfireCartEntity(EntityType<?> furnaceCart, World world) {
        super(furnaceCart, world);
    }

    public SoulfireCartEntity(EntityType<?> furnaceCart, World worldIn, double x, double y, double z) {
        super(furnaceCart, worldIn, x, y, z);
    }

    @Override
    public void killMinecart(DamageSource source) {
        super.killMinecart(source);
        if (!source.isExplosion() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.entityDropItem(Blocks.SOUL_CAMPFIRE);
        }

    }

    @Override
    public BlockState getDefaultDisplayTile() {
        return Blocks.SOUL_CAMPFIRE.getDefaultState().with(CampfireBlock.LIT, Boolean.valueOf(isMinecartPowered()));
    }

    @Override
    public double getSpeedCoeff() {
        return 6;
    }

    public boolean isGoingUphill() {
        return false;
    }

}
