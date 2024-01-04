package com.alc.moreminecarts.blocks.utility_rails;

import com.alc.moreminecarts.blocks.containers.ChunkLoaderBlock;
import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.alc.moreminecarts.entities.StickyPistonPushcartEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class PistonLifterRailBlock extends BaseRailBlock {

    public enum PistonLifterEffect implements StringRepresentable {
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

    public static final MapCodec<PistonLifterRailBlock> CODEC = simpleCodec(PistonLifterRailBlock::new);
    public MapCodec<PistonLifterRailBlock> codec() {
        return CODEC;
    }

    public PistonLifterRailBlock(Properties builder) {
        super(true, builder);
        this.registerDefaultState(defaultBlockState().setValue(POWERED, false)
                .setValue(SHAPE, RailShape.NORTH_SOUTH)
                .setValue(WATERLOGGED, Boolean.valueOf(false))
                .setValue(EFFECT, PistonLifterEffect.lift));
    }

    @Override
    public boolean canMakeSlopes(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.isClientSide() || !state.getValue(POWERED)) return;
        this.checkPressed(worldIn, pos, state);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        this.checkPressed(worldIn, pos, state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWERED, EFFECT, WATERLOGGED);
    }

    // Below is slightly modified from DetectorRailBlock

    private void checkPressed(Level worldIn, BlockPos pos, BlockState state) {
        if (this.canSurvive(state, worldIn, pos)) {

            List<AbstractMinecart> list = this.findMinecarts(worldIn, pos, AbstractMinecart.class, (entity) -> true);
            for (AbstractMinecart minecart : list) {

                if (state.getValue(EFFECT) == PistonLifterEffect.snap) {

                    if (minecart instanceof StickyPistonPushcartEntity) {
                        ((StickyPistonPushcartEntity)minecart).attemptContract();
                    }

                }
                else if (minecart instanceof PistonPushcartEntity) {
                    ((PistonPushcartEntity)minecart).attemptMove(state.getValue(EFFECT) == PistonLifterEffect.lift, true);
                }

            }

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
        double d0 = 0.2D;
        return new AABB((double)pos.getX() + 0.2D, (double)pos.getY(), (double)pos.getZ() + 0.2D, (double)(pos.getX() + 1) - 0.2D, (double)(pos.getY() + 1) - 0.2D, (double)(pos.getZ() + 1) - 0.2D);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide()) {
            PistonLifterEffect effect = (PistonLifterEffect) state.getValue(EFFECT);
            worldIn.setBlockAndUpdate(pos, state.setValue(EFFECT, effect.next() ));
            worldIn.playSound((Player)null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.sidedSuccess(worldIn.isClientSide());
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
