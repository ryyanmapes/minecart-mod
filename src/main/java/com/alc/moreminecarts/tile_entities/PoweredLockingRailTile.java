package com.alc.moreminecarts.tile_entities;

import com.alc.moreminecarts.blocks.utility_rails.PoweredLockingRailBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;

public class PoweredLockingRailTile extends LockingRailTile {

    public PoweredLockingRailTile(BlockPos pos, BlockState state) {
        super(pos, state, true);
    }

    @Override
    protected void lockIn(AbstractMinecart cart) {
        super.lockIn(cart);
        Direction direction = getBlockState().getValue(PoweredLockingRailBlock.FACING);
        this.saved_push_x = direction.getStepX();
        this.saved_push_z = direction.getStepZ();
    }
}
