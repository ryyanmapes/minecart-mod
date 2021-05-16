package com.alc.moreminecarts.blocks;

import com.alc.moreminecarts.MMReferences;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class HoloScaffold extends Block implements IWaterLoggable {

    public static final int MAX_DISTANCE = 20;

    private static final VoxelShape STABLE_SHAPE;
    private static final VoxelShape UNSTABLE_SHAPE;
    private static final VoxelShape UNSTABLE_SHAPE_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    private static final VoxelShape BELOW_BLOCK = VoxelShapes.block().move(0.0D, -1.0D, 0.0D);
    public static final IntegerProperty TRUE_DISTANCE = IntegerProperty.create("distance", 0, MAX_DISTANCE);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;


    public HoloScaffold(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.registerDefaultState(this.stateDefinition.any().setValue(TRUE_DISTANCE, MAX_DISTANCE).setValue(WATERLOGGED, false).setValue(BOTTOM, true));
    }

    // Gets lowest-distance neighbor in any direction, or -1 if there is none.
    public static int getDistance(IBlockReader reader, BlockPos pos) {
        int min_distance = MAX_DISTANCE + 1;
        for(Direction direction : Direction.values()) {
            BlockState blockstate1 = reader.getBlockState(pos.relative(direction));
            if (blockstate1.is(MMReferences.holo_scaffold)) {
                min_distance = Math.min(min_distance, blockstate1.getValue(TRUE_DISTANCE) + 1);
            }
            else if (blockstate1.is(MMReferences.holo_scaffold_generator)) {
                return 1;
            }
        }
        return min_distance;
    }

    public static boolean isValidDistance(IBlockReader reader, BlockPos pos) {
        int distance = getDistance(reader, pos);
        return distance >= 0 && distance <= MAX_DISTANCE;
    }

    private void tickNeighbors(IWorld world, BlockPos pos, int value, boolean only_greater) {
        for(Direction direction : Direction.values()) {
            BlockPos check_pos = pos.relative(direction);
            BlockState blockstate1 = world.getBlockState(check_pos);
            if (blockstate1.is(MMReferences.holo_scaffold)) {
                int distance = blockstate1.getValue(TRUE_DISTANCE);
                if (only_greater && distance >= value) world.getBlockTicks().scheduleTick(check_pos, this, 1);
                if (!only_greater && distance <= value) world.getBlockTicks().scheduleTick(check_pos, this, 1);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        super.tick(state, world, pos, rand);

        int new_distance = getDistance(world, pos);
        boolean new_bottom = isBottom(world, pos, new_distance);

        if (new_distance == -1 || new_distance > MAX_DISTANCE) {
            world.destroyBlock(pos, true);
            return;
        }

        int old_distance = state.getValue(TRUE_DISTANCE);
        boolean old_bottom = state.getValue(BOTTOM);

        if (new_distance < old_distance) {
            tickNeighbors(world, pos, new_distance, true);
        }
        else if (new_distance > old_distance) {
            tickNeighbors(world, pos, new_distance, false);
        }

        if (new_distance != old_distance || new_bottom != old_bottom) {
            world.setBlock(pos, state.setValue(TRUE_DISTANCE, new_distance).setValue(BOTTOM, this.isBottom(world, pos, new_distance)), 2);
        }
    }

    @Override
    public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        if (!p_220069_2_.isClientSide()) {
            p_220069_2_.getBlockTicks().scheduleTick(p_220069_5_, this, 1);
        }
    }

    @Override
    public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
        return isValidDistance(p_196260_2_, p_196260_3_);
    }

    @Override
    public FluidState getFluidState(BlockState p_204507_1_) {
        return p_204507_1_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_204507_1_);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(TRUE_DISTANCE, WATERLOGGED, BOTTOM);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        BlockPos blockpos = p_196258_1_.getClickedPos();
        World world = p_196258_1_.getLevel();
        int i = getDistance(world, blockpos);
        // These should hopefully never matter.
        if (i < 0) i = 0;
        if (i > MAX_DISTANCE) i = MAX_DISTANCE;

        return this.defaultBlockState()
                .setValue(WATERLOGGED, world.getFluidState(blockpos).getType() == Fluids.WATER)
                .setValue(TRUE_DISTANCE, i)
                .setValue(BOTTOM, this.isBottom(world, blockpos, i));
    }

    private boolean isBottom(IBlockReader p_220116_1_, BlockPos p_220116_2_, int p_220116_3_) {
        return p_220116_3_ > 0 && !p_220116_1_.getBlockState(p_220116_2_.below()).is(this);
    }

    @Override
    public boolean isScaffolding(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
        return true;
    }


    // Taken from ScaffoldingBlock


    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        if (!p_220053_4_.isHoldingItem(p_220053_1_.getBlock().asItem())) {
            return p_220053_1_.getValue(BOTTOM) ? UNSTABLE_SHAPE : STABLE_SHAPE;
        } else {
            return VoxelShapes.block();
        }
    }

    public VoxelShape getInteractionShape(BlockState p_199600_1_, IBlockReader p_199600_2_, BlockPos p_199600_3_) {
        return VoxelShapes.block();
    }

    public boolean canBeReplaced(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
        return p_196253_2_.getItemInHand().getItem() == this.asItem();
    }

    public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        if (p_196271_1_.getValue(WATERLOGGED)) {
            p_196271_4_.getLiquidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickDelay(p_196271_4_));
        }

        if (!p_196271_4_.isClientSide()) {
            p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, 1);
        }

        return p_196271_1_;
    }

    public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
        if (p_220071_4_.isAbove(VoxelShapes.block(), p_220071_3_, true) && !p_220071_4_.isDescending()) {
            return STABLE_SHAPE;
        } else {
            return p_220071_1_.getValue(TRUE_DISTANCE) != 0 && p_220071_1_.getValue(BOTTOM) && p_220071_4_.isAbove(BELOW_BLOCK, p_220071_3_, true) ? UNSTABLE_SHAPE_BOTTOM : VoxelShapes.empty();
        }
    }

    static {
        VoxelShape voxelshape = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        VoxelShape voxelshape1 = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 2.0D);
        VoxelShape voxelshape2 = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
        VoxelShape voxelshape3 = Block.box(0.0D, 0.0D, 14.0D, 2.0D, 16.0D, 16.0D);
        VoxelShape voxelshape4 = Block.box(14.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
        STABLE_SHAPE = VoxelShapes.or(voxelshape, voxelshape1, voxelshape2, voxelshape3, voxelshape4);
        VoxelShape voxelshape5 = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 2.0D, 16.0D);
        VoxelShape voxelshape6 = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
        VoxelShape voxelshape7 = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 2.0D, 16.0D);
        VoxelShape voxelshape8 = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 2.0D);
        UNSTABLE_SHAPE = VoxelShapes.or(UNSTABLE_SHAPE_BOTTOM, STABLE_SHAPE, voxelshape6, voxelshape5, voxelshape8, voxelshape7);
    }
}
