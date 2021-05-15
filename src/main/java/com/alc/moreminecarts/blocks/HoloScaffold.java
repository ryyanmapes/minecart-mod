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
import net.minecraftforge.common.extensions.IForgeBlock;

import java.util.Random;

public class HoloScaffold extends Block implements IForgeBlock, IWaterLoggable {

    public static final int MAX_DISTANCE = 20;

    public static final IntegerProperty DISTANCE = BlockStateProperties.STABILITY_DISTANCE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public HoloScaffold(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, MAX_DISTANCE).setValue(WATERLOGGED, false));
    }

    // Gets lowest-distance neighbor in any direction, or -1 if there is none.
    private static int getDistance(IBlockReader reader, BlockPos pos) {
        int min_distance = -1;
        for(Direction direction : Direction.values()) {
            BlockState blockstate1 = reader.getBlockState(pos.relative(direction));
            if (blockstate1.is(MMReferences.holo_scaffold)) {
                min_distance = Math.min(min_distance, blockstate1.getValue(DISTANCE) + 1);
            }
            else if (blockstate1.is(MMReferences.holo_scaffold_generator)) {
                return 1;
            }
        }
        return min_distance;
    }

    private void tickNeighbors(IWorld world, BlockPos pos, int value, boolean only_greater) {
        for(Direction direction : Direction.values()) {
            BlockPos check_pos = pos.relative(direction);
            BlockState blockstate1 = world.getBlockState(check_pos);
            if (blockstate1.is(MMReferences.holo_scaffold)) {
                int distance = blockstate1.getValue(DISTANCE);
                if (only_greater && distance >= value) world.getBlockTicks().scheduleTick(check_pos, this, 1);
                if (!only_greater && distance <= value) world.getBlockTicks().scheduleTick(check_pos, this, 1);
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        if (p_196271_1_.getValue(WATERLOGGED)) {
            p_196271_4_.getLiquidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickDelay(p_196271_4_));
        }

        if (!p_196271_4_.isClientSide()) {
            p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, 1);
        }

        return p_196271_1_;
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        super.tick(state, world, pos, rand);

        int new_distance = getDistance(world, pos);

        if (new_distance == -1 || new_distance > MAX_DISTANCE) {
            world.destroyBlock(pos, true);
            return;
        }

        int old_distance = state.getValue(DISTANCE);

        if (new_distance < old_distance) {
            tickNeighbors(world, pos, new_distance, true);
        }
        else if (new_distance > old_distance) {
            tickNeighbors(world, pos, new_distance, false);
        }

        state.setValue(DISTANCE, new_distance);
    }

    @Override
    public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        if (!p_220069_2_.isClientSide()) {
            p_220069_2_.getBlockTicks().scheduleTick(p_220069_5_, this, 1);
        }
    }

    @Override
    public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
        return getDistance(p_196260_2_, p_196260_3_) < MAX_DISTANCE;
    }

    @Override
    public FluidState getFluidState(BlockState p_204507_1_) {
        return p_204507_1_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_204507_1_);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(DISTANCE, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return VoxelShapes.block();
    }

    @Override
    public VoxelShape getInteractionShape(BlockState p_199600_1_, IBlockReader p_199600_2_, BlockPos p_199600_3_) {
        return VoxelShapes.block();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        BlockPos blockpos = p_196258_1_.getClickedPos();
        World world = p_196258_1_.getLevel();
        int i = getDistance(world, blockpos);
        return this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(world.getFluidState(blockpos).getType() == Fluids.WATER)).setValue(DISTANCE, Integer.valueOf(i));
    }


    @Override
    public boolean isScaffolding(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
        return true;
    }
}
