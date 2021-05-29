package com.alc.moreminecarts.blocks.rail_crossings;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class RailCrossing extends AbstractRailBlock {
    public static final Property<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;

    public RailCrossing(Properties builder) {
        super(false, builder);
    }

    @Override
    public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isFlexibleRail(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public RailShape getRailDirection(BlockState state, IBlockReader world, BlockPos pos, @Nullable AbstractMinecartEntity cart) {
        Vector3d movement = cart.getDeltaMovement();
        if (movement.x > movement.z) return RailShape.EAST_WEST;
        else return RailShape.NORTH_SOUTH;
    }
}