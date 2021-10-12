package com.alc.moreminecarts.blocks.rail_crossings;

import com.alc.moreminecarts.MMConstants;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LightspeedRailCrossing extends RailCrossing {

    public LightspeedRailCrossing(Properties builder) {
        super(builder);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return MMConstants.LIGHTSPEED_MAX_SPEED;
    }
}
