package com.alc.moreminecarts.blocks.containers;

import com.alc.moreminecarts.registry.MMTileEntities;
import com.alc.moreminecarts.tile_entities.MinecartLoaderTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class MinecartLoaderBlock extends BaseEntityBlock {
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    public MinecartLoaderBlock(Properties builder) {
        super(builder);
        this.registerDefaultState(defaultBlockState().setValue(ENABLED, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ENABLED);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace_result) {
        if (world.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity tile_entity = world.getBlockEntity(pos);
        if (tile_entity instanceof MinecartLoaderTile) {
            player.openMenu((MenuProvider)tile_entity);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MinecartLoaderTile(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState state_2, boolean bool) {
        if (!state.is(state_2.getBlock())) {
            BlockEntity tile_entity = world.getBlockEntity(pos);
            if (tile_entity instanceof MinecartLoaderTile) {
                MinecartLoaderTile loader = (MinecartLoaderTile) tile_entity;
                Containers.dropContents(world, pos, loader);
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, state_2, bool);
        }
    }

    // Stuff taken from FurnaceBlock.

    @Override
    public void setPlacedBy(Level p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
        if (p_180633_5_.hasCustomHoverName()) {
            BlockEntity tileentity = p_180633_1_.getBlockEntity(p_180633_2_);
            if (tileentity instanceof MinecartLoaderTile) {
                ((MinecartLoaderTile)tileentity).setCustomName(p_180633_5_.getHoverName());
            }
        }
    }

    // Signal stuff

    @Override
    public boolean isSignalSource(BlockState p_149744_1_) {
        return true;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        BlockEntity tile_entity = blockAccess.getBlockEntity(pos);
        if (tile_entity instanceof MinecartLoaderTile) {
            if (!((MinecartLoaderTile) tile_entity).getOutputsRedstone()) return 0;
            return ((MinecartLoaderTile) tile_entity).getSignal();
        }
        return 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        BlockEntity tile_entity = world.getBlockEntity(pos);
        if (tile_entity instanceof MinecartLoaderTile) {
            if (((MinecartLoaderTile) tile_entity).getOutputsRedstone()) return 0;
            return ((MinecartLoaderTile) tile_entity).getSignal();
        }
        return 0;
    }

    // Redstone stuff (taken from HopperBlock)

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState old_state, boolean p_220082_5_) {
        if (!old_state.is(state.getBlock())) {
            this.checkPoweredState(world, pos, state);
        }
    }

    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos changed_pos, boolean p_220069_6_) {
        this.checkPoweredState(world, pos, state);
    }

    private void checkPoweredState(Level world, BlockPos pos, BlockState state) {
        boolean flag = !world.hasNeighborSignal(pos);
        if (flag != state.getValue(ENABLED)) {
            world.setBlock(pos, state.setValue(ENABLED, flag), 4);
        }

    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        return createTickerHelper(p_152182_, MMTileEntities.MINECART_LOADER_TILE_ENTITY.get(), MinecartLoaderTile::doTick);
    }
}
