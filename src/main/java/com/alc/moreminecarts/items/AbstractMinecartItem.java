package com.alc.moreminecarts.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

// Based on https://github.com/KingLemming/1.16/blob/master/CoFHCore/src/main/java/cofh/core/item/MinecartItemCoFH.java
// Seems to be mostly modified vanilla stuff.

public abstract class AbstractMinecartItem extends Item {

    public AbstractMinecartItem(Properties properties) {
        super(properties);
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    public InteractionResult useOn(UseOnContext context) {

        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = world.getBlockState(blockpos);

        if (!blockstate.is(BlockTags.RAILS)) {
            return InteractionResult.FAIL;
        }
        ItemStack stack = context.getItemInHand();
        if (!world.isClientSide()) {
            RailShape railshape = blockstate.getBlock() instanceof RailBlock ? ((RailBlock) blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
            double d0 = 0.0D;
            if (railshape.isAscending()) {
                d0 = 0.5D;
            }
            createMinecart(stack, world, (double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.0625D + d0, (double) blockpos.getZ() + 0.5D);
        }
        stack.shrink(1);
        return InteractionResult.SUCCESS;
    }

    abstract void createMinecart(ItemStack stack, Level world, double x, double y, double z);

    // Old body of CreateMinecart:
    //AbstractMinecart minecart = factory.createMinecart(world, posX, posY, posZ);
    //if (stack.hasDisplayName()) {
    //    minecart.setCustomName(stack.getDisplayName());
    //}
    //world.addEntity(minecart)

    private static final DispenseItemBehavior DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior() {

        private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

        /**
         * Dispense the specified stack, play the dispense sound and spawn particles.
         */
        public ItemStack execute(BlockSource source, ItemStack stack) {

            Direction direction = source.state().getValue(DispenserBlock.FACING);
            Level world = source.level();

            double d0 = source.pos().getX() + (double) direction.getStepX() * 1.125D;
            double d1 = Math.floor(source.pos().getY()) + (double) direction.getStepY();
            double d2 = source.pos().getZ() + (double) direction.getStepZ() * 1.125D;

            BlockPos blockpos = source.pos().offset(direction.getStepX(), direction.getStepY(), direction.getStepZ());
            BlockState blockstate = world.getBlockState(blockpos);
            RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
            double d3;
            if (blockstate.is(BlockTags.RAILS)) {
                if (railshape.isAscending()) {
                    d3 = 0.6D;
                } else {
                    d3 = 0.1D;
                }
            } else {
                if (!blockstate.isAir() || !world.getBlockState(blockpos.below()).is(BlockTags.RAILS)) {
                    return this.behaviourDefaultDispenseItem.dispense(source, stack);
                }
                BlockState state = world.getBlockState(blockpos.below());
                RailShape shape = state.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) state.getBlock()).getRailDirection(state, world, blockpos.below(), null) : RailShape.NORTH_SOUTH;
                if (direction != Direction.DOWN && shape.isAscending()) {
                    d3 = -0.4D;
                } else {
                    d3 = -0.9D;
                }
            }
            if (stack.getItem() instanceof AbstractMinecartItem) {
                ((AbstractMinecartItem) stack.getItem()).createMinecart(stack, world, d0, d1 + d3, d2);
                stack.shrink(1);
            }
            return stack;
        }
    };


}
