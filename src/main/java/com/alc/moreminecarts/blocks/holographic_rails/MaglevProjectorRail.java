package com.alc.moreminecarts.blocks.holographic_rails;

import com.alc.moreminecarts.MoreMinecartsConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MaglevProjectorRail extends ProjectorRail {

    public static final Block maglev_hologram_rail = null;

    public MaglevProjectorRail(Properties builder) {
        super(builder);
    }

    @Override
    protected int getHologramLength() {return 8;}
    @Override
    protected Block getHologramRail() {return maglev_hologram_rail;}

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return MoreMinecartsConstants.MAGLEV_MAX_SPEED;
    }

}
