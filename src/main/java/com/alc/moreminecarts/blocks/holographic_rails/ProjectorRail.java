package com.alc.moreminecarts.blocks.holographic_rails;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.misc.RailUtil;
import net.minecraft.block.*;
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

public class ProjectorRail extends AbstractRailBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ProjectorRail(Properties builder) {
        super(true, builder);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE, POWERED);
    }

    protected int getHologramLength() {return 5;}
    protected Block getHologramRail() {return MMReferences.hologram_rail;}

    @Override
    protected void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn) {
        updateState(state, worldIn, pos, blockIn, false);
    }

    protected void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn, boolean force) {
        boolean currently_powered = state.getValue(POWERED);
        boolean now_powered = worldIn.hasNeighborSignal(pos);
        if (currently_powered != now_powered || force) {
            worldIn.setBlock(pos, state.setValue(POWERED, now_powered), 3);

            Direction direction = state.getValue(FACING);
            RailShape shape = RailUtil.FacingToShape(direction, now_powered);
            RailShape old_shape = RailUtil.FacingToShape(direction, !now_powered);

            // Remove old holograms
            for (int i = 0; i < getHologramLength(); i++) {
                BlockPos test_pos = pos.relative(direction, i+1).above(!now_powered? i: 0);
                BlockState test_state = worldIn.getBlockState(test_pos);
                if (test_state.is(getHologramRail()) && test_state.getValue(FACING) == direction && test_state.getValue(SHAPE) == old_shape) {
                    worldIn.setBlock(test_pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }

            // Add new holograms
            for (int i = 0; i < getHologramLength(); i++) {
                BlockPos test_pos = pos.relative(direction, i+1).above(now_powered? i : 0);
                BlockState test_state = worldIn.getBlockState(test_pos);
                if (test_state.is(getHologramRail()) && test_state.getValue(FACING) == direction && test_state.getValue(SHAPE) == shape) continue;
                if (!test_state.isAir()) break;
                worldIn.setBlock(test_pos,
                        getHologramRail().defaultBlockState().setValue(FACING, direction).setValue(SHAPE, shape).setValue(HolographicRail.LENGTH, i), 3);
            }

            worldIn.updateNeighborsAt(pos.below(), this);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, direction)
                .setValue(SHAPE, direction.getAxis() == Direction.Axis.Z? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
    }

    // Unused
    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public RailShape getRailDirection(BlockState state, IBlockReader world, BlockPos pos, @Nullable AbstractMinecartEntity cart) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z? RailShape.NORTH_SOUTH : RailShape.EAST_WEST;
    }

    @Override
    public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!worldIn.isClientSide()) {
            this.updateState(state, worldIn, pos, state.getBlock(), true);
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
        return false;
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {
            updateState(state, worldIn, pos, state.getBlock(), true);
            //worldIn.playSound((PlayerEntity)null, pos, SoundEvents.LEVER_CLICK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return ActionResultType.sidedSuccess(worldIn.isClientSide());
    }

    // Taken straight from PoweredRailBlock
    public BlockState rotate(BlockState state, Rotation rot) {
        state = state.setValue(FACING, rot.rotate(state.getValue(FACING)));
        return state;
    }

}
