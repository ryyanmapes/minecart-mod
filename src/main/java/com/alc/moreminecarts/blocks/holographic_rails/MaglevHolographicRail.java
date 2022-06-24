package com.alc.moreminecarts.blocks.holographic_rails;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.registry.MMBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MaglevHolographicRail extends HolographicRail {

    public MaglevHolographicRail(Properties builder) {
        super(builder);
    }

    @Override
    protected Block getProjectorRail() {return MMBlocks.MAGLEV_PROJECTOR_RAIL.get();}

    @Override
    public float getRailMaxSpeed(BlockState state, Level world, BlockPos pos, AbstractMinecart cart) {
        return MMConstants.MAGLEV_MAX_SPEED;
    }

}
