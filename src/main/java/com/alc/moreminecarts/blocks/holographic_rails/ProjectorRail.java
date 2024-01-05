package com.alc.moreminecarts.blocks.holographic_rails;

import com.alc.moreminecarts.blocks.containers.ChunkLoaderBlock;
import com.alc.moreminecarts.misc.RailUtil;
import com.alc.moreminecarts.registry.MMBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class ProjectorRail extends BaseRailBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ProjectorRail(Properties builder) {
        super(true, builder);
        this.registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE, POWERED, WATERLOGGED);
    }

    protected int getHologramLength() {return 5;}
    protected Block getHologramRail() {return MMBlocks.HOLOGRAM_RAIL.get();}

    @Override
    protected void updateState(BlockState state, Level worldIn, BlockPos pos, Block blockIn) {
        updateState(state, worldIn, pos, blockIn, false);
    }

    protected void updateState(BlockState state, Level worldIn, BlockPos pos, Block blockIn, boolean force) {
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
                    if (test_state.getValue(WATERLOGGED)) worldIn.setBlock(test_pos, Blocks.WATER.defaultBlockState(), 3);
                    else worldIn.setBlock(test_pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }

            // Add new holograms
            for (int i = 0; i < getHologramLength(); i++) {
                BlockPos test_pos = pos.relative(direction, i+1).above(now_powered? i : 0);
                BlockState test_state = worldIn.getBlockState(test_pos);
                if (test_state.is(getHologramRail()) && test_state.getValue(FACING) == direction && test_state.getValue(SHAPE) == shape) continue;
                if (test_state.isAir()) {
                    worldIn.setBlock(test_pos,
                            getHologramRail().defaultBlockState()
                                    .setValue(FACING, direction).setValue(SHAPE, shape).setValue(HolographicRail.LENGTH, i), 3);
                } else if (test_state.is(Blocks.WATER) && test_state.getValue(LiquidBlock.LEVEL) == 0) {
                    worldIn.setBlock(test_pos,
                            getHologramRail().defaultBlockState()
                                    .setValue(WATERLOGGED, true)
                                    .setValue(FACING, direction).setValue(SHAPE, shape).setValue(HolographicRail.LENGTH, i), 3);
                }
                else {
                    break;
                }

            }

            worldIn.updateNeighborsAt(pos.below(), this);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        return super.getStateForPlacement(context).setValue(FACING, direction)
                .setValue(SHAPE, direction.getAxis() == Direction.Axis.Z? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
    }

    // Unused
    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public RailShape getRailDirection(BlockState state, BlockGetter world, BlockPos pos, @Nullable AbstractMinecart cart) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z? RailShape.NORTH_SOUTH : RailShape.EAST_WEST;
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!worldIn.isClientSide()) {
            this.updateState(state, worldIn, pos, state.getBlock(), true);
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
    public boolean canMakeSlopes(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide()) {
            updateState(state, worldIn, pos, state.getBlock(), true);
            //worldIn.playSound((Player)null, pos, SoundEvents.LEVER_CLICK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.sidedSuccess(worldIn.isClientSide());
    }

    // Taken straight from PoweredRailBlock
    public BlockState rotate(BlockState state, Rotation rot) {
        state = state.setValue(FACING, rot.rotate(state.getValue(FACING)));
        return state;
    }

}
