package com.alc.moreminecarts.blocks;

import com.alc.moreminecarts.MMConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailBlock;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MaglevRail extends RailBlock {

    public MaglevRail(Properties builder) {
        super(builder);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return MMConstants.MAGLEV_MAX_SPEED;
    }
}
