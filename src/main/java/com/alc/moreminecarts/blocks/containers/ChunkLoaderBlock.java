package com.alc.moreminecarts.blocks.containers;

import com.alc.moreminecarts.registry.MMTileEntities;
import com.alc.moreminecarts.tile_entities.ChunkLoaderTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;


public class ChunkLoaderBlock extends BaseEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ChunkLoaderBlock(Properties builder) {
        super(builder);
        this.registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace_result) {
        if (world.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity tile_entity = world.getBlockEntity(pos);
        if (tile_entity instanceof ChunkLoaderTile) {
            player.openMenu((MenuProvider) tile_entity);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChunkLoaderTile(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState state_2, boolean bool) {
        if (!state.is(state_2.getBlock())) {
            BlockEntity tile_entity = world.getBlockEntity(pos);
            if (tile_entity instanceof ChunkLoaderTile) {
                ChunkLoaderTile chunk_loader = (ChunkLoaderTile) tile_entity;
                Containers.dropContents(world, pos, chunk_loader);
                ChunkLoaderTile.dropExtras(world, chunk_loader.time_left, pos);
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
            if (tileentity instanceof ChunkLoaderTile) {
                ((ChunkLoaderTile)tileentity).setCustomName(p_180633_5_.getHoverName());
            }
        }

    }

    // Comparator stuff

    @Override
    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        BlockEntity tile_entity = world.getBlockEntity(pos);
        if (tile_entity instanceof ChunkLoaderTile) {
            return ((ChunkLoaderTile) tile_entity).getComparatorSignal();
        }
        return 0;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
        return createTickerHelper(p_152182_, MMTileEntities.CHUNK_LOADER_TILE_ENTITY.get(), ChunkLoaderTile::doTick);
    }

}
