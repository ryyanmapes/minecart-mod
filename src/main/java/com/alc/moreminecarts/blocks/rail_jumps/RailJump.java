package com.alc.moreminecarts.blocks.rail_jumps;

import com.alc.moreminecarts.blocks.containers.ChunkLoaderBlock;
import com.alc.moreminecarts.misc.RailUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class RailJump extends BaseRailBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final Property<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;

    protected static final VoxelShape FULL_BLOCK_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public RailJump(Properties builder) {
        super(true, builder);
        this.registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE, WATERLOGGED);
    }

    @Override
    public boolean canMakeSlopes(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos changed_pos, boolean p_220069_6_) {
        if (!world.isClientSide && world.getBlockState(pos).is(this)) {
            if (!canSupportRigidBlock(world, pos.below())) {
                dropResources(state, world, pos);
                world.removeBlock(pos, p_220069_6_);
            } else {
                this.updateState(state, world, pos, block);
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return FULL_BLOCK_AABB;
    }

    @Override
    protected BlockState updateDir(Level p_208489_1_, BlockPos p_208489_2_, BlockState p_208489_3_, boolean p_208489_4_) {
        return p_208489_3_;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        RailShape shape = RailUtil.FacingToShape(direction.getOpposite(), true);
        return this.defaultBlockState().setValue(FACING, direction).setValue(SHAPE, shape);
    }


    @Override
    public RailShape getRailDirection(BlockState state, BlockGetter world, BlockPos pos, @Nullable AbstractMinecart cart) {
        return RailUtil.FacingToShape(state.getValue(FACING).getOpposite(), true);
    }
}
