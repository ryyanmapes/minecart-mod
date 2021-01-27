package com.alc.moreminecarts.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("moreminecarts")
public class SoulfireCartEntity extends CampfireCartEntity {

    public static final Item soulfire_cart = null;

    public SoulfireCartEntity(EntityType<?> furnaceCart, World world) {
        super(furnaceCart, world);
    }

    public SoulfireCartEntity(EntityType<?> furnaceCart, World worldIn, double x, double y, double z) {
        super(furnaceCart, worldIn, x, y, z);
    }

    @Override
    public void killMinecart(DamageSource source) {
        this.remove();
        if (!source.isExplosion() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.entityDropItem(soulfire_cart);
        }

    }

    @Override
    public BlockState getDefaultDisplayTile() {
        return Blocks.SOUL_CAMPFIRE.getDefaultState().with(CampfireBlock.LIT, Boolean.valueOf(isMinecartPowered()));
    }

    @Override
    public double getSpeedCoeff() {
        return 7;
    }

    public boolean isGoingUphill() {
        return false;
    }

}
