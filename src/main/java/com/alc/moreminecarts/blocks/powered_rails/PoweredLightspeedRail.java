package com.alc.moreminecarts.blocks.powered_rails;

import com.alc.moreminecarts.MMConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

public class PoweredLightspeedRail extends PoweredRailBlock {

    public PoweredLightspeedRail(Properties builder) {
        super(builder, true);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, Level world, BlockPos pos, AbstractMinecart cart) {
        return MMConstants.LIGHTSPEED_MAX_SPEED;
    }

    @Override
    public boolean canMakeSlopes(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.isClientSide()) return;

        if (state.getValue(POWERED) && entityIn instanceof AbstractMinecart) {

            AbstractMinecart minecart = (AbstractMinecart) entityIn;

            if (!minecart.shouldDoRailFunctions()) return;

            RailShape railshape = getRailDirection(state, worldIn, pos, minecart);

            Vec3 minecartVelocity = minecart.getDeltaMovement();
            double minecartSpeedMagnitude = minecartVelocity.horizontalDistance();
            if (minecartSpeedMagnitude > 0.01D) {
                double d19 = MMConstants.POWERED_LIGHTSPEED_BOOST;
                minecart.setDeltaMovement(minecartVelocity.add(minecartVelocity.x / minecartSpeedMagnitude * d19, 0.0D, minecartVelocity.z / minecartSpeedMagnitude * d19));
            } else {
                Vec3 vector3d7 = minecart.getDeltaMovement();
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

    private boolean isRedstoneConductor(Level world, BlockPos p_213900_1_) {
        return world.getBlockState(p_213900_1_).isRedstoneConductor(world, p_213900_1_);
    }


}
