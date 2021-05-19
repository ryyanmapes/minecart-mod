package com.alc.moreminecarts.blocks.holographic_rails;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.blocks.rails.WoodenRail;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WoodenHolographicRail extends HolographicRail {

    public WoodenHolographicRail(Properties builder) {
        super(builder);
    }

    @Override
    protected Block getProjectorRail() {return MMReferences.wooden_projector_rail;}

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return WoodenRail.WOODEN_MAX_SPEED;
    }

}
