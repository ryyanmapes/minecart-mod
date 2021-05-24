package com.alc.moreminecarts.blocks.utility_rails;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.tile_entities.PoweredLockingRailTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PoweredLockingRailBlock extends LockingRailBlock implements ITileEntityProvider {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public PoweredLockingRailBlock(Properties builder) {
        super(builder);
        this.registerDefaultState(this.getStateDefinition().any().setValue(POWERED, false)
                .setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(INVERTED, true).setValue(FACING, Direction.NORTH));

    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader reader) {
        return new PoweredLockingRailTile();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWERED, INVERTED, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, direction).setValue(SHAPE, direction.getAxis() == Direction.Axis.X? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
    }

    @Override
    protected void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn) {
        super.updateState(state, worldIn, pos, blockIn);
        state.setValue(SHAPE, state.getValue(FACING).getAxis() == Direction.Axis.X? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
    }

    @Override
    public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        state.setValue(SHAPE, state.getValue(FACING).getAxis() == Direction.Axis.X? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
        super.onPlace(state, worldIn, pos, oldState, isMoving);
    }

    @Override
    protected BlockState updateDir(World p_208489_1_, BlockPos p_208489_2_, BlockState state, boolean p_208489_4_) {
        BlockState new_state = super.updateDir(p_208489_1_, p_208489_2_, state, p_208489_4_);
        new_state.setValue(SHAPE, state.getValue(FACING).getAxis() == Direction.Axis.X? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
        return new_state;
    }

    @Override
    public void neighborChanged(BlockState state, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        super.neighborChanged(state, p_220069_2_, p_220069_3_, p_220069_4_, p_220069_5_, p_220069_6_);
        state.setValue(SHAPE, state.getValue(FACING).getAxis() == Direction.Axis.X? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
    }

    @Override
    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);
        if (state.getValue(POWERED) ^ state.getValue(INVERTED)) return;
        Direction direction = state.getValue(FACING);

        if (entityIn instanceof AbstractMinecartEntity && getDectectionBox(pos).intersects(entityIn.getBoundingBox())) {
            entityIn.setDeltaMovement(
                entityIn.getDeltaMovement().add(
                    new Vector3d(direction.getStepX(), 0, direction.getStepZ()).scale(MMConstants.POWERED_LOCKING_RAIL_SPEED) ));
        }
    }

    private AxisAlignedBB getDectectionBox(BlockPos pos) {
        double d0 = 0.2D;
        return new AxisAlignedBB((double)pos.getX() + 0.2D, (double)pos.getY(), (double)pos.getZ() + 0.2D, (double)(pos.getX() + 1) - 0.2D, (double)(pos.getY() + 1) - 0.2D, (double)(pos.getZ() + 1) - 0.2D);
    }
}
