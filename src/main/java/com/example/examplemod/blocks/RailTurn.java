package com.example.examplemod.blocks;

import com.example.examplemod.datagen.RailDataGen;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class RailTurn extends AbstractRailBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty FLIPPED = BooleanProperty.create("flipped");

    private static final Logger LOGGER = LogManager.getLogger();

    public RailTurn(Properties builder) {
        super(true, builder);
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(SHAPE, RailShape.NORTH_SOUTH).with(POWERED, Boolean.valueOf(false)).with(FLIPPED, Boolean.valueOf(false)));
    }


    @Override
    protected void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn) {
        boolean flag = state.get(POWERED);
        boolean flag1 = worldIn.isBlockPowered(pos);
        if (flag1 != flag) {
            worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(flag1)), 3);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this);
        }

    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getPlacementHorizontalFacing();
        return this.getDefaultState().with(FACING, direction);
    }

    // Unused
    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.isIn(state.getBlock())) {
            this.updateState(state, worldIn, pos, state.getBlock());
        }
        super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
    }

    @Override
    protected BlockState updateRailState(BlockState state, World world, BlockPos pos, boolean isMoving) {
        return state;
    }

    @Override
    protected BlockState getUpdatedState(World worldIn, BlockPos pos, BlockState state, boolean placing) {
        return state;

    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!worldIn.isRemote && worldIn.getBlockState(pos).isIn(this)) {
            this.updateState(state, worldIn, pos, blockIn);
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE, POWERED, FLIPPED);
    }

    @Override
    public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }


    @Override
    public RailShape getRailDirection(BlockState state, IBlockReader world, BlockPos pos, @Nullable AbstractMinecartEntity cart) {

        boolean is_powered = state.get(POWERED);
        Direction facing = state.get(FACING);
        boolean is_x_axis = facing == Direction.NORTH || facing == Direction.SOUTH;
        boolean backwards = facing == Direction.NORTH || facing == Direction.EAST;
        boolean flipped = state.get(FLIPPED);
        if (cart != null) {
            boolean turn_approach = is_x_axis? Math.abs(cart.getMotion().x) > 0.05 : Math.abs(cart.getMotion().z) > 0.05;

            if (is_powered || turn_approach) {

                boolean backwards_approach = (!is_x_axis? cart.getMotion().x : -cart.getMotion().z) * (backwards? 1 : -1) <= 0 && !turn_approach;
                
                if (backwards_approach) {
                } else if (flipped) {
                    if (is_x_axis) {
                        return backwards ? RailShape.SOUTH_WEST : RailShape.NORTH_EAST;
                    } else {
                        return backwards ? RailShape.NORTH_WEST : RailShape.SOUTH_EAST;
                    }
                } else {
                    if (is_x_axis) {
                        return backwards ? RailShape.SOUTH_EAST : RailShape.NORTH_WEST;
                    } else {
                        return backwards ? RailShape.SOUTH_WEST : RailShape.NORTH_EAST;
                    }
                }
            }
        }

        return is_x_axis? RailShape.NORTH_SOUTH : RailShape.EAST_WEST;
    }

    public static boolean isXFacing(Direction facing) {
        return facing == Direction.NORTH || facing == Direction.SOUTH;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            worldIn.setBlockState(pos, state.with(FLIPPED, !state.get(FLIPPED)));
            worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return ActionResultType.func_233537_a_(worldIn.isRemote);
    }

    public static boolean getIsSimple(RailShape dir) {
        return dir == RailShape.EAST_WEST || dir == RailShape.NORTH_SOUTH;
    }


    // Taken straight from PoweredRailBlock
    public BlockState rotate(BlockState state, Rotation rot) {
        state = state.with(FACING, rot.rotate(state.get(FACING)));
        return state;
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        state = state.rotate(mirrorIn.toRotation(state.get(FACING)));
        state = state.with(FLIPPED, !state.get(FLIPPED));
        return state;
    }

}
