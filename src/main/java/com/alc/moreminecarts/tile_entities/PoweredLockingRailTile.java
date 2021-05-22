package com.alc.moreminecarts.tile_entities;

import com.alc.moreminecarts.blocks.utility_rails.PoweredLockingRailBlock;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;

public class PoweredLockingRailTile extends LockingRailTile implements ITickableTileEntity {

    @Override
    protected void lockIn(AbstractMinecartEntity cart) {
        super.lockIn(cart);
        Direction direction = getBlockState().getValue(PoweredLockingRailBlock.FACING);
        this.saved_push_x = direction.getStepX();
        this.saved_push_z = direction.getStepZ();
    }
}
