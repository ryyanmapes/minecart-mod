package com.alc.moreminecarts.blocks.rail_jumps;

import com.alc.moreminecarts.misc.RailUtil;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RailJump extends AbstractRailBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final Property<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;

    protected static final VoxelShape FULL_BLOCK_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public RailJump(Properties builder) {
        super(true, builder);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE);
    }

    @Override
    public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos changed_pos, boolean p_220069_6_) {
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
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return FULL_BLOCK_AABB;
    }

    @Override
    protected BlockState updateDir(World p_208489_1_, BlockPos p_208489_2_, BlockState p_208489_3_, boolean p_208489_4_) {
        return p_208489_3_;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        RailShape shape = RailUtil.FacingToShape(direction.getOpposite(), true);
        return this.defaultBlockState().setValue(FACING, direction).setValue(SHAPE, shape);
    }


    @Override
    public RailShape getRailDirection(BlockState state, IBlockReader world, BlockPos pos, @Nullable AbstractMinecartEntity cart) {
        return RailUtil.FacingToShape(state.getValue(FACING).getOpposite(), true);
    }
}
