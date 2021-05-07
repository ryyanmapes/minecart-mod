package com.alc.moreminecarts.blocks;

import com.alc.moreminecarts.MoreMinecartsConstants;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MaglevRailTurn extends RailTurn {

    public MaglevRailTurn(Properties builder) {
        super(builder);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return MoreMinecartsConstants.MAGLEV_MAX_SPEED;
    }
}
