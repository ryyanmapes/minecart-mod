package com.alc.moreminecarts.blocks.holographic_rails;

import com.alc.moreminecarts.MMReferences;
import net.minecraft.block.*;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ProjectorRail extends AbstractRailBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ProjectorRail(Properties builder) {
        super(true, builder);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE, POWERED);
    }

    protected int getHologramLength() {return 5;}
    protected Block getHologramRail() {return MMReferences.hologram_rail;}

    @Override
    protected void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn) {
        boolean currently_powered = state.getValue(POWERED);
        boolean now_powered = worldIn.hasNeighborSignal(pos);
        if (currently_powered != now_powered) {
            worldIn.setBlock(pos, state.setValue(POWERED, now_powered), 3);

            Direction direction = state.getValue(FACING);
            RailShape shape = GetAscending(direction, now_powered);

            // Remove old holograms
            for (int i = 1; i <= getHologramLength(); i++) {
                BlockPos test_pos = pos.relative(direction, i).above(!now_powered? i : 0);
                if (worldIn.getBlockState(test_pos).is(getHologramRail())) {
                    worldIn.setBlock(test_pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }

            // Add new holograms
            for (int i = 1; i <= getHologramLength(); i++) {
                BlockPos test_pos = pos.relative(direction, i).above(now_powered? i : 0);
                if (!worldIn.getBlockState(test_pos).is(Blocks.AIR)) break;
                worldIn.setBlock(test_pos, getHologramRail().defaultBlockState().setValue(FACING, direction).setValue(SHAPE, shape), 3);
            }

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
    public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {
            updateState(state, worldIn, pos, state.getBlock());
            worldIn.playSound((PlayerEntity)null, pos, SoundEvents.LEVER_CLICK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return ActionResultType.sidedSuccess(worldIn.isClientSide());
    }

    // Taken straight from PoweredRailBlock
    public BlockState rotate(BlockState state, Rotation rot) {
        state = state.setValue(FACING, rot.rotate(state.getValue(FACING)));
        return state;
    }

    // Static Helper Methods

    public static RailShape GetAscending(Direction direction, boolean ascending) {
        switch (direction) {
            case NORTH:
                return ascending? RailShape.ASCENDING_NORTH : RailShape.NORTH_SOUTH;
            case EAST:
                return ascending? RailShape.ASCENDING_EAST : RailShape.EAST_WEST;
            case SOUTH:
                return ascending? RailShape.ASCENDING_SOUTH : RailShape.NORTH_SOUTH;
            case WEST:
                return ascending? RailShape.ASCENDING_WEST : RailShape.EAST_WEST;
        }
        // TODO error here
        return RailShape.NORTH_SOUTH;
    }

    // Comparator stuff

    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos) {
        return world.getEntitiesOfClass(AbstractMinecartEntity.class, this.getDectectionBox(state, pos),
                (cart) -> {
                    BlockState under = world.getBlockState(cart.blockPosition());
                    if (under.getBlock() == Blocks.AIR) under = world.getBlockState(cart.blockPosition().below());

                    return ((under.getBlock() == getHologramRail() || under.getBlock() == state.getBlock())
                            && under.getValue(FACING) == state.getValue(FACING));
                    }
                ).size() > 0? 15 : 0;
    }

    private AxisAlignedBB getDectectionBox(BlockState state, BlockPos pos) {
        boolean powered = state.getValue(POWERED);
        Direction direction = state.getValue(FACING);
        int length = getHologramLength();

        return new AxisAlignedBB(
                (double)(pos.getX() + Math.min(direction.getStepX()*length, 0)) + 0.2D,
                (double)pos.getY(),
                (double)(pos.getZ() + Math.min(direction.getStepZ()*length, 0)) + 0.2D,

                (double)(pos.getX() + Math.max(direction.getStepX()*length, 0) + 1) - 0.2D,
                (double)(pos.getY() + (powered? length : 0) + 1) - 0.2D,
                (double)(pos.getZ() + Math.max(direction.getStepZ()*length, 0) + 1) - 0.2D);
    }

}
