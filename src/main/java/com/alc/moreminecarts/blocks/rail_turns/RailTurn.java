package com.alc.moreminecarts.blocks.rail_turns;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RailTurn extends AbstractRailBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty FLIPPED = BooleanProperty.create("flipped");


    public RailTurn(Properties builder) {
        super(true, builder);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(POWERED, false).setValue(FLIPPED, false));
    }


    @Override
    protected void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn) {
        boolean flag1 = state.getValue(POWERED);
        boolean flag2 = worldIn.hasNeighborSignal(pos);
        if (flag1 != flag2) {
            worldIn.setBlock(pos, state.setValue(POWERED, flag2), 3);
            worldIn.updateNeighborsAt(pos.below(), this);
        }

    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    // Unused
    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            this.updateState(state, worldIn, pos, state.getBlock());
        }
        super.onPlace(state, worldIn, pos, oldState, isMoving);
    }

    @Override
    protected BlockState updateState(BlockState state, World world, BlockPos pos, boolean isMoving) {
        return state;
    }

    @Override
    protected BlockState updateDir(World worldIn, BlockPos pos, BlockState state, boolean placing) {
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE, POWERED, FLIPPED);
    }

    @Override
    public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }


    @Override
    public RailShape getRailDirection(BlockState state, IBlockReader world, BlockPos pos, @Nullable AbstractMinecartEntity cart) {

        boolean is_powered = state.getValue(POWERED);
        Direction facing = state.getValue(FACING);
        boolean is_x_axis = facing == Direction.NORTH || facing == Direction.SOUTH;
        boolean backwards = facing == Direction.NORTH || facing == Direction.EAST;
        boolean flipped = state.getValue(FLIPPED);
        if (cart != null) {
            boolean turn_approach = is_x_axis? Math.abs(cart.getDeltaMovement().x) > 0.05 : Math.abs(cart.getDeltaMovement().z) > 0.05;

            if (is_powered || turn_approach) {

                boolean backwards_approach = (!is_x_axis? cart.getDeltaMovement().x : -cart.getDeltaMovement().z) * (backwards? 1 : -1) <= 0 && !turn_approach;
                
                if (backwards_approach) {
                } else if (flipped) {
                    if (is_x_axis) {
                        return backwards ? RailShape.SOUTH_WEST : RailShape.NORTH_EAST;
                    } else {
                        return backwards ? RailShape.NORTH_WEST : RailShape.SOUTH_EAST;
                    }
                } else {
                    if (is_x_axis) {
                        return backwards ? RailShape.SOUTH_EAST : RailShape.NORTH_WEST;
                    } else {
                        return backwards ? RailShape.SOUTH_WEST : RailShape.NORTH_EAST;
                    }
                }
            }
        }

        return is_x_axis? RailShape.NORTH_SOUTH : RailShape.EAST_WEST;
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {
            worldIn.setBlockAndUpdate(pos, state.setValue(FLIPPED, !state.getValue(FLIPPED)));
            worldIn.playSound((PlayerEntity)null, pos, SoundEvents.LEVER_CLICK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return ActionResultType.sidedSuccess(worldIn.isClientSide());
    }


    // Taken straight from PoweredRailBlock
    public BlockState rotate(BlockState state, Rotation rot) {
        state = state.setValue(FACING, rot.rotate(state.getValue(FACING)));
        return state;
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        state = state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
        state = state.setValue(FLIPPED, !state.getValue(FLIPPED));
        return state;
    }

    // Not sure what these are for.
    public static boolean isXFacing(Direction facing) {
        return facing == Direction.NORTH || facing == Direction.SOUTH;
    }
    public static boolean getIsSimple(RailShape dir) {
        return dir == RailShape.EAST_WEST || dir == RailShape.NORTH_SOUTH;
    }

}
