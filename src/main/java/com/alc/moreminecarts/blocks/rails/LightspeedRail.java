package com.alc.moreminecarts.blocks.rails;

import com.alc.moreminecarts.MMConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailBlock;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class LightspeedRail extends RailBlock {

    public LightspeedRail(Properties builder) {
        super(builder);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return MMConstants.LIGHTSPEED_MAX_SPEED;
    }

    @Override
    public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isFlexibleRail(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

}
