package com.example.examplemod.items;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// Based on https://github.com/KingLemming/1.16/blob/master/CoFHCore/src/main/java/cofh/core/item/MinecartItemCoFH.java
// Seems to be mostly modified vanilla stuff.

public abstract class AbstractMinecartItem extends Item {

    public AbstractMinecartItem(Properties properties) {
        super(properties);
        DispenserBlock.registerDispenseBehavior(this, DISPENSER_BEHAVIOR);
    }

    public ActionResultType onItemUse(ItemUseContext context) {

        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        BlockState blockstate = world.getBlockState(blockpos);

        if (!blockstate.isIn(BlockTags.RAILS)) {
            return ActionResultType.FAIL;
        }
        ItemStack stack = context.getItem();
        if (!world.isRemote) {
            RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
            double d0 = 0.0D;
            if (railshape.isAscending()) {
                d0 = 0.5D;
            }
            createMinecart(stack, world, (double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.0625D + d0, (double) blockpos.getZ() + 0.5D);
        }
        stack.shrink(1);
        return ActionResultType.SUCCESS;
    }

    abstract void createMinecart(ItemStack stack, World world, double x, double y, double z);

    // Old body of CreateMinecart:
    //AbstractMinecartEntity minecart = factory.createMinecart(world, posX, posY, posZ);
    //if (stack.hasDisplayName()) {
    //    minecart.setCustomName(stack.getDisplayName());
    //}
    //world.addEntity(minecart)

    private static final IDispenseItemBehavior DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior() {

        private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

        /**
         * Dispense the specified stack, play the dispense sound and spawn particles.
         */
        public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {

            Direction direction = source.getBlockState().get(DispenserBlock.FACING);
            World world = source.getWorld();

            double d0 = source.getX() + (double) direction.getXOffset() * 1.125D;
            double d1 = Math.floor(source.getY()) + (double) direction.getYOffset();
            double d2 = source.getZ() + (double) direction.getZOffset() * 1.125D;

            BlockPos blockpos = source.getBlockPos().offset(direction);
            BlockState blockstate = world.getBlockState(blockpos);
            RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
            double d3;
            if (blockstate.isIn(BlockTags.RAILS)) {
                if (railshape.isAscending()) {
                    d3 = 0.6D;
                } else {
                    d3 = 0.1D;
                }
            } else {
                if (!blockstate.isAir(world, blockpos) || !world.getBlockState(blockpos.down()).isIn(BlockTags.RAILS)) {
                    return this.behaviourDefaultDispenseItem.dispense(source, stack);
                }
                BlockState state = world.getBlockState(blockpos.down());
                RailShape shape = state.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) state.getBlock()).getRailDirection(state, world, blockpos.down(), null) : RailShape.NORTH_SOUTH;
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
