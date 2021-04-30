package com.alc.moreminecarts.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.RailBlock;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MaglevRail extends RailBlock {
    public static final float MAGLEV_MAX_SPEED = 0.6f;

    public MaglevRail(Properties builder) {
        super(builder);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return MAGLEV_MAX_SPEED;
    }
}
