package com.alc.moreminecarts.blocks.holographic_rails;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.MMConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MaglevProjectorRail extends ProjectorRail {

    public MaglevProjectorRail(Properties builder) {
        super(builder);
    }

    @Override
    protected int getHologramLength() {return 8;}
    @Override
    protected Block getHologramRail() {return MMReferences.maglev_hologram_rail;}

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return MMConstants.MAGLEV_MAX_SPEED;
    }

}
