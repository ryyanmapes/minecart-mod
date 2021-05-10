package com.alc.moreminecarts.blocks.holographic_rails;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("moreminecarts")
public class HolographicRail extends AbstractRailBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;

    public static final Block projector_rail = null;

    public HolographicRail(Properties builder) {
        super(true, builder);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(SHAPE, RailShape.NORTH_SOUTH));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE);
    }

    protected Block getProjectorRail() {return projector_rail;}

    public boolean canSurvive(BlockState state, IWorldReader world_reader, BlockPos pos) {

        BlockPos holograph_position = getSupportHolograph(state, pos);
        BlockState holograph_state = world_reader.getBlockState(holograph_position);
        if (holograph_state.getBlock() == state.getBlock()
            && holograph_state.getValue(FACING) == state.getValue(FACING)) {
            return true;
        }

        BlockPos projector_position = getSupportProjector(state, pos);
        BlockState projector_state = world_reader.getBlockState(projector_position);
        if (projector_state.getBlock() == getProjectorRail()
                && projector_state.getValue(FACING) == state.getValue(FACING)) {
            return true;
        }

        return false;
    }

    public BlockPos getSupportHolograph(BlockState state, BlockPos pos) {
        RailShape shape = state.getValue(SHAPE);
        return getSupportProjector(state, pos).below( shape.isAscending()? -1 : 0 );
    }

    public BlockPos getSupportProjector(BlockState state, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        return pos.relative(facing);
    }


    // Should never be used, but here anyways.
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
    public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
        return true;
    }


    // Taken straight from PoweredRailBlock
    public BlockState rotate(BlockState state, Rotation rot) {
        state = state.setValue(FACING, rot.rotate(state.getValue(FACING)));
        return state;
    }

}
