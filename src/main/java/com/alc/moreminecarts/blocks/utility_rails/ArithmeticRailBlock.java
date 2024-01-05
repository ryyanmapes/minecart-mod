package com.alc.moreminecarts.blocks.utility_rails;

import com.alc.moreminecarts.blocks.containers.ChunkLoaderBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;

public class ArithmeticRailBlock extends BaseRailBlock {

    public enum SignalEffect implements StringRepresentable {
        plus,
        minus,
        left,
        right;

        @Override
        public String getSerializedName() {
            switch(this) {
                case plus:
                    return "plus";
                case minus:
                    return "minus";
                case left:
                    return "left";
                case right:
                    return "right";
            }
            return "ERROR";
        }

        public boolean isShift() {
            return this == plus || this == minus;
        }

        public boolean isNegative() {
            return this == minus || this == right;
        }

        public SignalEffect next() {
            switch(this) {
                case plus:
                    return minus;
                case minus:
                    return left;
                case left:
                    return right;
                case right:
                    return plus;
            }
            return plus;
        }

    }

    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty EFFECT = EnumProperty.create("effect", SignalEffect.class,
            SignalEffect.plus, SignalEffect.minus, SignalEffect.left, SignalEffect.right);


    public ArithmeticRailBlock(Properties builder) {
        super(true, builder);
        this.registerDefaultState(defaultBlockState().setValue(POWERED, false).setValue(SHAPE, RailShape.NORTH_SOUTH)
                .setValue(EFFECT, SignalEffect.plus).setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    protected void updateState(BlockState state, Level worldIn, BlockPos pos, Block blockIn) {
        boolean old_powered = state.getValue(POWERED);
        boolean new_powered = worldIn.hasNeighborSignal(pos);
        if (old_powered != new_powered) {
            worldIn.setBlock(pos, state.setValue(POWERED, new_powered), 3);
            worldIn.updateNeighborsAt(pos.below(), this);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide()) {
            worldIn.setBlockAndUpdate(pos, state.setValue(EFFECT, ((SignalEffect)state.getValue(EFFECT)).next()  ));
            worldIn.playSound((Player)null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.sidedSuccess(worldIn.isClientSide());
    }

    @Override
    public boolean canMakeSlopes(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWERED, EFFECT, WATERLOGGED);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            this.updateState(state, worldIn, pos, state.getBlock());
        }
    }

    @Override
    public Property<RailShape> getShapeProperty() { return SHAPE; }
}
