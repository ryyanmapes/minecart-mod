package com.alc.moreminecarts.blocks.powered_rails;

import com.alc.moreminecarts.MMConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class PoweredLightspeedRail extends PoweredRailBlock {

    public PoweredLightspeedRail(Properties builder) {
        super(builder, true);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return MMConstants.LIGHTSPEED_MAX_SPEED;
    }

    @Override
    public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.isClientSide()) return;

        if (state.getValue(POWERED) && entityIn instanceof AbstractMinecartEntity) {

            AbstractMinecartEntity minecart = (AbstractMinecartEntity) entityIn;

            if (!minecart.shouldDoRailFunctions()) return;

            RailShape railshape = getRailDirection(state, worldIn, pos, minecart);

            Vector3d minecartVelocity = minecart.getDeltaMovement();
            double minecartSpeedMagnitude = Math.sqrt(minecart.getHorizontalDistanceSqr(minecartVelocity));
            if (minecartSpeedMagnitude > 0.01D) {
                double d19 = MMConstants.POWERED_LIGHTSPEED_BOOST;
                minecart.setDeltaMovement(minecartVelocity.add(minecartVelocity.x / minecartSpeedMagnitude * d19, 0.0D, minecartVelocity.z / minecartSpeedMagnitude * d19));
            } else {
                Vector3d vector3d7 = minecart.getDeltaMovement();
                double d20 = vector3d7.x;
                double d21 = vector3d7.z;
                if (railshape == RailShape.EAST_WEST) {
                    if (isRedstoneConductor(worldIn, pos.west())) {
                        d20 = 0.02D;
                    } else if (isRedstoneConductor(worldIn, pos.east())) {
                        d20 = -0.02D;
                    }
                } else {
                    if (railshape != RailShape.NORTH_SOUTH) {
                        return;
                    }

                    if (isRedstoneConductor(worldIn, pos.north())) {
                        d21 = 0.02D;
                    } else if (isRedstoneConductor(worldIn, pos.south())) {
                        d21 = -0.02D;
                    }
                }

                minecart.setDeltaMovement(d20, vector3d7.y, d21);
            }
        }

    }

    private boolean isRedstoneConductor(World world, BlockPos p_213900_1_) {
        return world.getBlockState(p_213900_1_).isRedstoneConductor(world, p_213900_1_);
    }


}
