package com.alc.moreminecarts.blocks.holographic_rails;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.MMConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MaglevHolographicRail extends HolographicRail {

    public MaglevHolographicRail(Properties builder) {
        super(builder);
    }

    @Override
    protected Block getProjectorRail() {return MMReferences.maglev_projector_rail;}

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return MMConstants.MAGLEV_MAX_SPEED;
    }

}
