package com.alc.moreminecarts.blocks.powered_rails;

import com.alc.moreminecarts.MMConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;

// todo update to turbo rail
public class PoweredLightspeedRail extends PoweredRailBlock {

    public PoweredLightspeedRail(Properties builder) {
        super(builder, true);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, Level world, BlockPos pos, AbstractMinecart cart) {
        return MMConstants.LIGHTSPEED_MAX_SPEED;
    }

    @Override
    public boolean canMakeSlopes(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

}
