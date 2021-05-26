package com.alc.moreminecarts.blocks.utility_rails;

import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.alc.moreminecarts.entities.StickyPistonPushcartEntity;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class PistonLifterRailBlock extends AbstractRailBlock {

    public enum PistonLifterEffect implements IStringSerializable {
        lift,
        delift,
        snap;

        @Override
        public String getSerializedName() {
            switch(this) {
                case lift:
                    return "lift";
                case delift:
                    return "delift";
                case snap:
                    return "snap";
            }
            return "ERROR";
        }

        public PistonLifterEffect next() {
            switch(this) {
                case lift:
                    return delift;
                case delift:
                    return snap;
                case snap:
                    return lift;
            }
            return lift;
        }

    }

    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty EFFECT = EnumProperty.create("effect", PistonLifterEffect.class,
            PistonLifterEffect.lift, PistonLifterEffect.delift, PistonLifterEffect.snap);

    public PistonLifterRailBlock(Properties builder) {
        super(true, builder);
        this.registerDefaultState(this.getStateDefinition().any().setValue(POWERED, false)
                .setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(EFFECT, PistonLifterEffect.lift));
    }

    @Override
    public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    @Override
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.isClientSide() || !state.getValue(POWERED)) return;
        this.checkPressed(worldIn, pos, state);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        this.checkPressed(worldIn, pos, state);
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWERED, EFFECT);
    }

    // Below is slightly modified from DetectorRailBlock

    private void checkPressed(World worldIn, BlockPos pos, BlockState state) {
        if (this.canSurvive(state, worldIn, pos)) {

            List<AbstractMinecartEntity> list = this.findMinecarts(worldIn, pos, AbstractMinecartEntity.class, (entity) -> true);
            for (AbstractMinecartEntity minecart : list) {

                if (state.getValue(EFFECT) == PistonLifterEffect.snap) {

                    if (minecart instanceof StickyPistonPushcartEntity) {
                        ((StickyPistonPushcartEntity)minecart).attempt_contract();
                    }

                }
                else if (minecart instanceof PistonPushcartEntity) {
                    ((PistonPushcartEntity)minecart).attemptMove(state.getValue(EFFECT) == PistonLifterEffect.lift, true);
                }

            }

        }
    }

    @Override
    public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            this.checkPressed(worldIn, pos, this.updateState(state, worldIn, pos, isMoving));
        }
    }

    protected <T extends AbstractMinecartEntity> List<T> findMinecarts(World worldIn, BlockPos pos, Class<T> cartType, @Nullable Predicate<Entity> filter) {
        return worldIn.getEntitiesOfClass(cartType, this.getDectectionBox(pos), filter);
    }

    private AxisAlignedBB getDectectionBox(BlockPos pos) {
        double d0 = 0.2D;
        return new AxisAlignedBB((double)pos.getX() + 0.2D, (double)pos.getY(), (double)pos.getZ() + 0.2D, (double)(pos.getX() + 1) - 0.2D, (double)(pos.getY() + 1) - 0.2D, (double)(pos.getZ() + 1) - 0.2D);
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {
            PistonLifterEffect effect = (PistonLifterEffect) state.getValue(EFFECT);
            worldIn.setBlockAndUpdate(pos, state.setValue(EFFECT, effect.next() ));
            worldIn.playSound((PlayerEntity)null, pos, SoundEvents.LEVER_CLICK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return ActionResultType.sidedSuccess(worldIn.isClientSide());
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
