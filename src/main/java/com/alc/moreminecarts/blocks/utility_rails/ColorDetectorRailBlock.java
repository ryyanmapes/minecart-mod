package com.alc.moreminecarts.blocks.utility_rails;

import com.alc.moreminecarts.blocks.containers.ChunkLoaderBlock;
import com.alc.moreminecarts.entities.FlagCartEntity;
import com.alc.moreminecarts.registry.MMItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class ColorDetectorRailBlock extends BaseRailBlock {

    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public java.util.function.Supplier<Item> detected_item;

    public ColorDetectorRailBlock(Properties builder,  java.util.function.Supplier<Item> det) {
        super(true, builder);
        this.registerDefaultState(defaultBlockState().setValue(POWERED, Boolean.valueOf(false)).setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
        this.detected_item = det;
    }

    @Override
    public boolean canMakeSlopes(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.isClientSide()) return;
        this.checkPressed(worldIn, pos, state);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        this.checkPressed(worldIn, pos, state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWERED, WATERLOGGED);
    }

    // Below is slightly modified from DetectorRailBlock

    private void checkPressed(Level worldIn, BlockPos pos, BlockState state) {
        if (this.canSurvive(state, worldIn, pos)) {
            boolean was_powered = state.getValue(POWERED);
            boolean activate = false;
            List<AbstractMinecart> list = this.findMinecarts(worldIn, pos, AbstractMinecart.class, (entity) -> true);
            if (!list.isEmpty()) {
                if (was_powered) activate = true;
                else {
                    for (AbstractMinecart minecart : list) {
                        if (minecart.getMinecartType() == AbstractMinecart.Type.RIDEABLE) {
                            List<Entity> passengers = minecart.getPassengers();
                            if (passengers.isEmpty()) continue;
                            Entity entity = passengers.get(0);
                            if (!(entity instanceof Player)) continue;
                            Player player = (Player) entity;
                            if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == detected_item.get()
                                    || player.getItemInHand(InteractionHand.OFF_HAND).getItem() == detected_item.get()) {
                                activate = true;
                            }
                        }
                        else if (minecart instanceof FlagCartEntity) {
                            if (((FlagCartEntity)minecart).getSelectedFlag() == detected_item.get()) {
                                activate = true;
                            }
                        }
                        else if (minecart instanceof AbstractMinecartContainer) {
                            AbstractMinecartContainer container = (AbstractMinecartContainer)minecart;
                            HashSet<Item> set = new HashSet<>();
                            set.add(detected_item.get());
                            if (container.hasAnyOf(set)) {
                                activate = true;
                            }
                        }
                    }
                }
            }

            if (activate && !was_powered) {
                BlockState blockstate = state.setValue(POWERED, Boolean.valueOf(true));
                worldIn.setBlock(pos, blockstate, 3);
                this.updatePowerToConnected(worldIn, pos, blockstate, true);
                worldIn.updateNeighborsAt(pos, this);
                worldIn.updateNeighborsAt(pos.below(), this);
                worldIn.setBlocksDirty(pos, state, blockstate);
            }

            if (!activate && was_powered) {
                BlockState blockstate1 = state.setValue(POWERED, Boolean.valueOf(false));
                worldIn.setBlock(pos, blockstate1, 3);
                this.updatePowerToConnected(worldIn, pos, blockstate1, false);
                worldIn.updateNeighborsAt(pos, this);
                worldIn.updateNeighborsAt(pos.below(), this);
                worldIn.setBlocksDirty(pos, state, blockstate1);
            }

            if (activate) {
                worldIn.scheduleTick(pos, this, 20);
            }

            worldIn.updateNeighbourForOutputSignal(pos, this);
        }
    }

    protected void updatePowerToConnected(Level worldIn, BlockPos pos, BlockState state, boolean powered) {
        RailState railstate = new RailState(worldIn, pos, state);

        for(BlockPos blockpos : railstate.getConnections()) {
            BlockState blockstate = worldIn.getBlockState(blockpos);
            blockstate.neighborChanged(worldIn, blockpos, blockstate.getBlock(), pos, false);
        }

    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            this.checkPressed(worldIn, pos, this.updateState(state, worldIn, pos, isMoving));
        }
    }

    protected <T extends AbstractMinecart> List<T> findMinecarts(Level worldIn, BlockPos pos, Class<T> cartType, @Nullable Predicate<Entity> filter) {
        return worldIn.getEntitiesOfClass(cartType, this.getDectectionBox(pos), filter);
    }

    private AABB getDectectionBox(BlockPos pos) {
        return new AABB((double)pos.getX() + 0.2D, (double)pos.getY(), (double)pos.getZ() + 0.2D, (double)(pos.getX() + 1) - 0.2D, (double)(pos.getY() + 1) - 0.2D, (double)(pos.getZ() + 1) - 0.2D);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public boolean isSignalSource(BlockState p_149744_1_) {
        return true;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (!blockState.getValue(POWERED)) {
            return 0;
        } else {
            return side == Direction.UP ? 15 : 0;
        }
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        switch(rot) {
            case CLOCKWISE_180:
                switch(state.getValue(SHAPE)) {
                    case ASCENDING_EAST:
                        return state.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                        return state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return state.setValue(SHAPE, RailShape.NORTH_WEST);
                    case SOUTH_WEST:
                        return state.setValue(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_WEST:
                        return state.setValue(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_EAST:
                        return state.setValue(SHAPE, RailShape.SOUTH_WEST);
                }
            case COUNTERCLOCKWISE_90:
                switch(state.getValue(SHAPE)) {
                    case ASCENDING_EAST:
                        return state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    case ASCENDING_WEST:
                        return state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_NORTH:
                        return state.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_SOUTH:
                        return state.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    case SOUTH_EAST:
                        return state.setValue(SHAPE, RailShape.NORTH_EAST);
                    case SOUTH_WEST:
                        return state.setValue(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return state.setValue(SHAPE, RailShape.SOUTH_WEST);
                    case NORTH_EAST:
                        return state.setValue(SHAPE, RailShape.NORTH_WEST);
                    case NORTH_SOUTH:
                        return state.setValue(SHAPE, RailShape.EAST_WEST);
                    case EAST_WEST:
                        return state.setValue(SHAPE, RailShape.NORTH_SOUTH);
                }
            case CLOCKWISE_90:
                switch(state.getValue(SHAPE)) {
                    case ASCENDING_EAST:
                        return state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_WEST:
                        return state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    case ASCENDING_NORTH:
                        return state.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_SOUTH:
                        return state.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    case SOUTH_EAST:
                        return state.setValue(SHAPE, RailShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return state.setValue(SHAPE, RailShape.NORTH_WEST);
                    case NORTH_WEST:
                        return state.setValue(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_EAST:
                        return state.setValue(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_SOUTH:
                        return state.setValue(SHAPE, RailShape.EAST_WEST);
                    case EAST_WEST:
                        return state.setValue(SHAPE, RailShape.NORTH_SOUTH);
                }
            default:
                return state;
        }
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        RailShape railshape = state.getValue(SHAPE);
        switch(mirrorIn) {
            case LEFT_RIGHT:
                switch(railshape) {
                    case ASCENDING_NORTH:
                        return state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return state.setValue(SHAPE, RailShape.NORTH_EAST);
                    case SOUTH_WEST:
                        return state.setValue(SHAPE, RailShape.NORTH_WEST);
                    case NORTH_WEST:
                        return state.setValue(SHAPE, RailShape.SOUTH_WEST);
                    case NORTH_EAST:
                        return state.setValue(SHAPE, RailShape.SOUTH_EAST);
                    default:
                        return super.mirror(state, mirrorIn);
                }
            case FRONT_BACK:
                switch(railshape) {
                    case ASCENDING_EAST:
                        return state.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                    default:
                        break;
                    case SOUTH_EAST:
                        return state.setValue(SHAPE, RailShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return state.setValue(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return state.setValue(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_EAST:
                        return state.setValue(SHAPE, RailShape.NORTH_WEST);
                }
        }

        return super.mirror(state, mirrorIn);
    }

    @Override
    public Property<RailShape> getShapeProperty() { return SHAPE; }
}
