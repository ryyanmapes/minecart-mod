package com.alc.moreminecarts.blocks.containers;

import com.alc.moreminecarts.tile_entities.ChunkLoaderTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class ChunkLoaderBlock extends ContainerBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ChunkLoaderBlock(Properties builder) {
        super(builder);
        this.registerDefaultState(this.getStateDefinition().any().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace_result) {
        if (world.isClientSide) return ActionResultType.SUCCESS;

        TileEntity tile_entity = world.getBlockEntity(pos);
        if (tile_entity instanceof ChunkLoaderTile) {
            NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tile_entity, pos);
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }


    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader reader) {
        return new ChunkLoaderTile();
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState state_2, boolean bool) {
        if (!state.is(state_2.getBlock())) {
            TileEntity tile_entity = world.getBlockEntity(pos);
            if (tile_entity instanceof ChunkLoaderTile) {
                ChunkLoaderTile chunk_loader = (ChunkLoaderTile) tile_entity;
                InventoryHelper.dropContents(world, pos, chunk_loader);
                ChunkLoaderTile.dropExtras(world, chunk_loader.time_left, pos);
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, state_2, bool);
        }
    }

    // Stuff taken from FurnaceBlock.

    @Override
    public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
        if (p_180633_5_.hasCustomHoverName()) {
            TileEntity tileentity = p_180633_1_.getBlockEntity(p_180633_2_);
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
    public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos) {
        TileEntity tile_entity = world.getBlockEntity(pos);
        if (tile_entity instanceof ChunkLoaderTile) {
            return ((ChunkLoaderTile) tile_entity).getComparatorSignal();
        }
        return 0;
    }

}
