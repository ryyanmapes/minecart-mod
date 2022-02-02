package com.alc.moreminecarts.blocks.holographic_rails;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.MMReferences;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class WoodenHolographicRail extends HolographicRail {

    public WoodenHolographicRail(Properties builder) {
        super(builder);
    }

    @Override
    protected Block getProjectorRail() {return MMReferences.wooden_projector_rail;}

    @Override
    public float getRailMaxSpeed(BlockState state, Level world, BlockPos pos, AbstractMinecart cart) {
        return MMConstants.WOODEN_MAX_SPEED;
    }

}
