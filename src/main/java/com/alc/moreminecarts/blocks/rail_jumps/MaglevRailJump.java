package com.alc.moreminecarts.blocks.rail_jumps;

import com.alc.moreminecarts.MMConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MaglevRailJump extends RailJump {

    public MaglevRailJump(Properties builder) {
        super(builder);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, Level world, BlockPos pos, AbstractMinecart cart) {
        return MMConstants.MAGLEV_MAX_SPEED;
    }

}
