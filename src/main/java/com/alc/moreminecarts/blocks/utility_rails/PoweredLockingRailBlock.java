package com.alc.moreminecarts.blocks.utility_rails;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.tile_entities.PoweredLockingRailTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class PoweredLockingRailBlock extends LockingRailBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public PoweredLockingRailBlock(Properties builder) {
        super(builder);
        this.registerDefaultState(defaultBlockState().setValue(POWERED, false)
                .setValue(SHAPE, RailShape.NORTH_SOUTH)
                .setValue(WATERLOGGED, Boolean.valueOf(false))
                .setValue(INVERTED, true).setValue(FACING, Direction.NORTH));

    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PoweredLockingRailTile(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWERED, INVERTED, FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, direction).setValue(SHAPE, direction.getAxis() == Direction.Axis.X? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
    }

    @Override
    protected void updateState(BlockState state, Level worldIn, BlockPos pos, Block blockIn) {
        super.updateState(state, worldIn, pos, blockIn);
        state.setValue(SHAPE, state.getValue(FACING).getAxis() == Direction.Axis.X? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        state.setValue(SHAPE, state.getValue(FACING).getAxis() == Direction.Axis.X? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
        super.onPlace(state, worldIn, pos, oldState, isMoving);
    }

    @Override
    protected BlockState updateDir(Level p_208489_1_, BlockPos p_208489_2_, BlockState state, boolean p_208489_4_) {
        BlockState new_state = super.updateDir(p_208489_1_, p_208489_2_, state, p_208489_4_);
        new_state.setValue(SHAPE, state.getValue(FACING).getAxis() == Direction.Axis.X? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
        return new_state;
    }

    @Override
    public void neighborChanged(BlockState state, Level p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        super.neighborChanged(state, p_220069_2_, p_220069_3_, p_220069_4_, p_220069_5_, p_220069_6_);
        state.setValue(SHAPE, state.getValue(FACING).getAxis() == Direction.Axis.X? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);
        if (state.getValue(POWERED) ^ state.getValue(INVERTED)) return;
        Direction direction = state.getValue(FACING);

        if (entityIn instanceof AbstractMinecart && getDectectionBox(pos).intersects(entityIn.getBoundingBox())) {
            entityIn.setDeltaMovement(
                entityIn.getDeltaMovement().add(
                    new Vec3(direction.getStepX(), 0, direction.getStepZ()).scale(MMConstants.POWERED_LOCKING_RAIL_SPEED) ));
        }
    }

    private AABB getDectectionBox(BlockPos pos) {
        double d0 = 0.2D;
        return new AABB((double)pos.getX() + 0.2D, (double)pos.getY(), (double)pos.getZ() + 0.2D, (double)(pos.getX() + 1) - 0.2D, (double)(pos.getY() + 1) - 0.2D, (double)(pos.getZ() + 1) - 0.2D);
    }
}
