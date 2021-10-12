package com.alc.moreminecarts.blocks.utility_rails;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ArithmeticRailBlock extends AbstractRailBlock {

    public enum SignalEffect implements IStringSerializable {
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
        this.registerDefaultState(this.getStateDefinition().any().setValue(POWERED, false).setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(EFFECT, SignalEffect.plus));
    }

    @Override
    protected void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn) {
        boolean old_powered = state.getValue(POWERED);
        boolean new_powered = worldIn.hasNeighborSignal(pos);
        if (old_powered != new_powered) {
            worldIn.setBlock(pos, state.setValue(POWERED, new_powered), 3);
            worldIn.updateNeighborsAt(pos.below(), this);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {
            worldIn.setBlockAndUpdate(pos, state.setValue(EFFECT, ((SignalEffect)state.getValue(EFFECT)).next()  ));
            worldIn.playSound((PlayerEntity)null, pos, SoundEvents.LEVER_CLICK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return ActionResultType.sidedSuccess(worldIn.isClientSide());
    }

    @Override
    public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWERED, EFFECT);
    }

    @Override
    public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            this.updateState(state, worldIn, pos, state.getBlock());
        }
    }

    @Override
    public Property<RailShape> getShapeProperty() { return SHAPE; }
}
