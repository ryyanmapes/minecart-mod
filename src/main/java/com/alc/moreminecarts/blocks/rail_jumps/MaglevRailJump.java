package com.alc.moreminecarts.blocks.rail_jumps;

import com.alc.moreminecarts.MMConstants;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MaglevRailJump extends RailJump {

    public MaglevRailJump(Properties builder) {
        super(builder);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return MMConstants.MAGLEV_MAX_SPEED;
    }

}
