package com.alc.moreminecarts.blocks.powered_rails;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.blocks.rails.LockingRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class PoweredLockingRailBlock extends LockingRailBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public PoweredLockingRailBlock(Properties builder) {
        super(builder);
        this.registerDefaultState(this.getStateDefinition().any().setValue(POWERED, false)
                .setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(INVERTED, false).setValue(FACING, Direction.NORTH));

    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWERED, INVERTED, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, direction);
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
