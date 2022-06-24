package com.alc.moreminecarts.blocks.holo_scaffolds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.ticks.TickPriority;

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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(TRUE_DISTANCE, WATERLOGGED, BOTTOM, STRENGTH, GROW_DIRECTION, READY_TO_GROW);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
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
                world.scheduleTick(new_pos, true_state.getBlock(), 3, TickPriority.VERY_LOW);
            }
        }
        else {

            BlockPos new_pos = pos.relative(direction);
            if (world.getBlockState(new_pos).isAir()) {

                world.setBlock(new_pos, true_state.setValue(TRUE_DISTANCE, new_distance)
                    .setValue(BOTTOM, this.isBottom(world, new_pos, new_distance))
                    .setValue(STRENGTH, HoloScaffoldStrength.getFromLength(new_distance))
                    .setValue(GROW_DIRECTION, direction), 2);
                world.scheduleTick(new_pos, true_state.getBlock(), 3, TickPriority.VERY_LOW);
            }
        }

    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
            .setValue(GROW_DIRECTION, context.getClickedFace())
            .setValue(READY_TO_GROW, true);
    }
}
