package com.alc.moreminecarts.blocks.holographic_rails;

import com.alc.moreminecarts.MMReferences;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class HolographicRail extends AbstractRailBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    // MAX LENGTH: 16
    public static final IntegerProperty LENGTH = BlockStateProperties.LEVEL;

    protected static final VoxelShape FULL_BLOCK_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public HolographicRail(Properties builder) {
        super(true, builder);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(LENGTH, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE, LENGTH);
    }

    protected Block getProjectorRail() {return MMReferences.projector_rail;}

    public boolean canSurvive(BlockState state, IWorldReader world_reader, BlockPos pos) {

        int length = state.getValue(LENGTH);

        if (length != 0) {

            BlockPos holograph_position = getSupportHolograph(state, pos);
            BlockState holograph_state = world_reader.getBlockState(holograph_position);
            if (holograph_state.getBlock() != state.getBlock()
                    || holograph_state.getValue(FACING) != state.getValue(FACING)) {
                return false;
            }
        }

        // Is this check necessary for all holographic rails?
        BlockPos projector_position = getSupportProjector(state, pos);
        BlockState projector_state = world_reader.getBlockState(projector_position);
        if (projector_state.getBlock() != getProjectorRail()
                || projector_state.getValue(FACING) != state.getValue(FACING)) {
            return false;
        }

        return true;
    }

    public BlockPos getSupportHolograph(BlockState state, BlockPos pos) {
        RailShape shape = state.getValue(SHAPE);
        Direction facing = state.getValue(FACING);
        return pos.below( shape.isAscending()? 1 : 0 ).relative(facing.getOpposite());
    }

    public BlockPos getSupportProjector(BlockState state, BlockPos pos) {
        RailShape shape = state.getValue(SHAPE);
        Direction facing = state.getValue(FACING);
        int length = state.getValue(LENGTH);
        return pos.below( shape.isAscending()? length : 0 ).relative(facing.getOpposite(), length + 1);
    }


    // Should never be used, but here anyways.
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        if (!p_220069_2_.isClientSide && p_220069_2_.getBlockState(p_220069_3_).is(this)) {
            if (!canSurvive(p_220069_1_, p_220069_2_, p_220069_3_)) {
                dropResources(p_220069_1_, p_220069_2_, p_220069_3_);
                p_220069_2_.removeBlock(p_220069_3_, p_220069_6_);
            }

        }
    }

    // Unused
    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader block_reader, BlockPos pos, ISelectionContext selection_context) {
        RailShape railshape = state.is(this) ? state.getValue(this.getShapeProperty()) : null;
        return railshape != null && railshape.isAscending() ? FULL_BLOCK_AABB : FLAT_AABB;
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
    public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
        return true;
    }


    // Taken straight from PoweredRailBlock
    public BlockState rotate(BlockState state, Rotation rot) {
        state = state.setValue(FACING, rot.rotate(state.getValue(FACING)));
        return state;
    }

}
