package com.alc.moreminecarts.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MaglevParallelRail extends ParallelRail {

    public MaglevParallelRail(Properties builder) {
        super(builder);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return MaglevRail.MAGLEV_MAX_SPEED;
    }

}
