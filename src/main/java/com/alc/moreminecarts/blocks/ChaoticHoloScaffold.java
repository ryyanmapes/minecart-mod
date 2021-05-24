package com.alc.moreminecarts.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class ChaoticHoloScaffold extends HoloScaffold {

    public static final DirectionProperty GROW_DIRECTION = BlockStateProperties.FACING;
    public static final BooleanProperty READY_TO_GROW = BooleanProperty.create("ready_to_grow");

    public ChaoticHoloScaffold(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.registerDefaultState(this.stateDefinition.any().setValue(TRUE_DISTANCE, MAX_DISTANCE)
                .setValue(WATERLOGGED, false).setValue(BOTTOM, true)
                .setValue(STRENGTH, HoloScaffoldStrength.strong)
                .setValue(GROW_DIRECTION, Direction.UP)
                .setValue(READY_TO_GROW, true));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(TRUE_DISTANCE, WATERLOGGED, BOTTOM, STRENGTH, GROW_DIRECTION, READY_TO_GROW);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        super.tick(state, world, pos, rand);

        BlockState true_state = world.getBlockState(pos);
        if (true_state.getBlock() != state.getBlock()) return;

        boolean ready_to_grow = true_state.getValue(READY_TO_GROW);
        if (!ready_to_grow) return;

        world.setBlock(pos, true_state.setValue(READY_TO_GROW, false), 2);

        int distance = true_state.getValue(TRUE_DISTANCE);
        if (distance >= MAX_DISTANCE) return;
        Direction direction = true_state.getValue(GROW_DIRECTION);
        int new_distance = distance + 1;

        if (distance > 1 && rand.nextInt(5) == 0) {
            for (int i = rand.nextInt(3); i < 3; i++) {

                Direction new_direction = Direction.getRandom(rand);
                if (new_direction.getOpposite() == direction) continue;
                BlockPos new_pos = pos.relative(new_direction);
                if (!world.getBlockState(new_pos).isAir()) continue;

                world.setBlock(new_pos, true_state.setValue(TRUE_DISTANCE, new_distance)
                    .setValue(BOTTOM, this.isBottom(world, new_pos, new_distance))
                    .setValue(STRENGTH, HoloScaffoldStrength.getFromLength(new_distance))
                    .setValue(GROW_DIRECTION, new_direction), 2);
                world.getBlockTicks().scheduleTick(new_pos, true_state.getBlock(), 3, TickPriority.VERY_LOW);
            }
        }
        else {

            BlockPos new_pos = pos.relative(direction);
            if (world.getBlockState(new_pos).isAir()) {

                world.setBlock(new_pos, true_state.setValue(TRUE_DISTANCE, new_distance)
                    .setValue(BOTTOM, this.isBottom(world, new_pos, new_distance))
                    .setValue(STRENGTH, HoloScaffoldStrength.getFromLength(new_distance))
                    .setValue(GROW_DIRECTION, direction), 2);
                world.getBlockTicks().scheduleTick(new_pos, true_state.getBlock(), 3, TickPriority.VERY_LOW);
            }
        }

    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return super.getStateForPlacement(context)
            .setValue(GROW_DIRECTION, context.getClickedFace())
            .setValue(READY_TO_GROW, true);
    }
}
