package com.alc.moreminecarts.blocks.holographic_rails;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.registry.MMBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class WoodenProjectorRail extends ProjectorRail {

    public WoodenProjectorRail(Properties builder) {
        super(builder);
    }

    @Override
    protected int getHologramLength() {return 3;}
    @Override
    protected Block getHologramRail() {return MMBlocks.WOODEN_HOLOGRAM_RAIL.get();}

    @Override
    public float getRailMaxSpeed(BlockState state, Level world, BlockPos pos, AbstractMinecart cart) {
        return MMConstants.WOODEN_MAX_SPEED;
    }

}
