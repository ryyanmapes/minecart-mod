package com.alc.moreminecarts.blocks.holographic_rails;

import com.alc.moreminecarts.blocks.WoodenRail;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("moreminecarts")
public class WoodenHolographicRail extends HolographicRail {

    public static final Block wooden_projector_rail = null;

    public WoodenHolographicRail(Properties builder) {
        super(builder);
    }

    @Override
    protected Block getProjectorRail() {return wooden_projector_rail;}

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return WoodenRail.WOODEN_MAX_SPEED;
    }

}
