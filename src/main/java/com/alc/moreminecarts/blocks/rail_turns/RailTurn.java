package com.alc.moreminecarts.blocks.rail_turns;

import com.alc.moreminecarts.blocks.containers.ChunkLoaderBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

;

public class RailTurn extends BaseRailBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty FLIPPED = BooleanProperty.create("flipped");

    public RailTurn(BlockBehaviour.Properties builder) {
        super(true, builder);
        this.registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(SHAPE, RailShape.NORTH_SOUTH)
                .setValue(WATERLOGGED, Boolean.valueOf(false))
                .setValue(POWERED, false).setValue(FLIPPED, false));
    }


    @Override
    protected void updateState(BlockState state, Level worldIn, BlockPos pos, Block blockIn) {
        boolean flag1 = state.getValue(POWERED);
        boolean flag2 = worldIn.hasNeighborSignal(pos);
        if (flag1 != flag2) {
            worldIn.setBlock(pos, state.setValue(POWERED, flag2), 3);
            worldIn.updateNeighborsAt(pos.below(), this);
        }

    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    // Unused
    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            this.updateState(state, worldIn, pos, state.getBlock());
        }
        super.onPlace(state, worldIn, pos, oldState, isMoving);
    }

    @Override
    protected BlockState updateState(BlockState state, Level world, BlockPos pos, boolean isMoving) {
        return state;
    }

    @Override
    protected BlockState updateDir(Level worldIn, BlockPos pos, BlockState state, boolean placing) {
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE, POWERED, FLIPPED, WATERLOGGED);
    }

    @Override
    public boolean canMakeSlopes(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }


    @Override
    public RailShape getRailDirection(BlockState state, BlockGetter world, BlockPos pos, @Nullable AbstractMinecart cart) {

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
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide()) {
            worldIn.setBlockAndUpdate(pos, state.setValue(FLIPPED, !state.getValue(FLIPPED)));
            worldIn.playSound((Player)null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.sidedSuccess(worldIn.isClientSide());
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

}
