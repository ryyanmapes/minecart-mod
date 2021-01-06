package com.example.examplemod.blocks;

import com.example.examplemod.datagen.RailDataGen;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class RailTurn extends AbstractRailBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public boolean is_left;


    public RailTurn(Properties builder, boolean is_left) {
        super(true, builder);
        this.is_left = is_left;
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(SHAPE, RailShape.NORTH_SOUTH).with(POWERED, Boolean.valueOf(false)));
    }


    @Override
    protected void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn) {
        boolean flag = state.get(POWERED);
        boolean flag1 = worldIn.isBlockPowered(pos);
        if (flag1 != flag) {
            worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(flag1)), 3);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this);
        }

    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }




    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {

        builder.add(FACING, SHAPE, POWERED);
    }

    @Override
    public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    @Override
    public Property<RailShape> getShapeProperty() { return SHAPE; }

    @Override
    public RailShape getRailDirection(BlockState state, IBlockReader world, BlockPos pos, @Nullable AbstractMinecartEntity cart) {

        RailShape railshape = state.get(SHAPE);
        boolean is_powered = state.get(POWERED);
        Direction facing = state.get(FACING);
        if (is_powered && cart != null) {

            boolean backwards_approach = false;
            boolean backwards = false;
            switch (facing) {
                case NORTH:
                    backwards_approach = cart.getMotion().x <= 0;
                    break;
                case EAST:
                    backwards_approach = cart.getMotion().z <= 0;
                    break;
                case SOUTH:
                    backwards_approach = cart.getMotion().x >= 0;
                    backwards = true;
                    break;
                case WEST:
                    backwards_approach = cart.getMotion().z >= 0;
                    backwards = true;
                    break;
            }

            if (backwards_approach){}
            else if (this.is_left) {
                switch (railshape) {
                    case NORTH_SOUTH:
                        return backwards? RailShape.NORTH_EAST : RailShape.SOUTH_WEST;
                    case EAST_WEST:
                        return backwards? RailShape.NORTH_WEST : RailShape.SOUTH_EAST;
                }
            }
            else {
                switch (railshape) {
                    case NORTH_SOUTH:
                        return backwards? RailShape.NORTH_WEST : RailShape.SOUTH_EAST;
                    case EAST_WEST:
                        return backwards? RailShape.NORTH_EAST : RailShape.SOUTH_WEST;
                }
            }
        }

        return railshape;
    }

    public static boolean getIsStraight(RailShape dir) {
        return dir != RailShape.NORTH_EAST && dir != RailShape.NORTH_WEST && dir != RailShape.SOUTH_EAST && dir != RailShape.SOUTH_WEST;
    }

    public static boolean getIsFlat(RailShape dir) {
        return dir != RailShape.NORTH_EAST && dir != RailShape.NORTH_WEST && dir != RailShape.SOUTH_EAST && dir != RailShape.SOUTH_WEST;
    }

    public static boolean getIsSimple(RailShape dir) {
        return dir == RailShape.EAST_WEST && dir == RailShape.NORTH_SOUTH;
    }


    // Taken straight from PoweredRailBlock
    public BlockState rotate(BlockState state, Rotation rot) {
        state = state.with(FACING, rot.rotate(state.get(FACING)));
        switch(rot) {
            case CLOCKWISE_180:
                switch((RailShape)state.get(SHAPE)) {
                    case ASCENDING_EAST:
                        return state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.with(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                        return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);
                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);
                    case NORTH_SOUTH: //Forge fix: MC-196102
                    case EAST_WEST:
                        return state;
                }
            case COUNTERCLOCKWISE_90:
                switch((RailShape)state.get(SHAPE)) {
                    case NORTH_SOUTH:
                        return state.with(SHAPE, RailShape.EAST_WEST);
                    case EAST_WEST:
                        return state.with(SHAPE, RailShape.NORTH_SOUTH);
                    case ASCENDING_EAST:
                        return state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case ASCENDING_WEST:
                        return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_NORTH:
                        return state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_SOUTH:
                        return state.with(SHAPE, RailShape.ASCENDING_EAST);
                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);
                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);
                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);
                }
            case CLOCKWISE_90:
                switch((RailShape)state.get(SHAPE)) {
                    case NORTH_SOUTH:
                        return state.with(SHAPE, RailShape.EAST_WEST);
                    case EAST_WEST:
                        return state.with(SHAPE, RailShape.NORTH_SOUTH);
                    case ASCENDING_EAST:
                        return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_WEST:
                        return state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case ASCENDING_NORTH:
                        return state.with(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_SOUTH:
                        return state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);
                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);
                }
            default:
                return state;
        }
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        state = state.rotate(mirrorIn.toRotation(state.get(FACING)));
        RailShape railshape = state.get(SHAPE);
        switch(mirrorIn) {
            case LEFT_RIGHT:
                switch(railshape) {
                    case ASCENDING_NORTH:
                        return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);
                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);
                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);
                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);
                    default:
                        return super.mirror(state, mirrorIn);
                }
            case FRONT_BACK:
                switch(railshape) {
                    case ASCENDING_EAST:
                        return state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.with(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                    default:
                        break;
                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);
                }
        }

        return super.mirror(state, mirrorIn);
    }

}
