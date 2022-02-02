package com.alc.moreminecarts.blocks.rails;

import com.alc.moreminecarts.MMConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MaglevRail extends RailBlock {

    public MaglevRail(Properties builder) {
        super(builder);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, Level world, BlockPos pos, AbstractMinecart cart) {
        return MMConstants.MAGLEV_MAX_SPEED;
    }

}
