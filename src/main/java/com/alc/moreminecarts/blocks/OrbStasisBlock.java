package com.alc.moreminecarts.blocks;

import com.alc.moreminecarts.tile_entities.OrbStasisTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

import javax.annotation.Nullable;

public class OrbStasisBlock extends Block implements ITileEntityProvider {

    public static final BooleanProperty CONTAINS_PEARL = BooleanProperty.create("has_orb");
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public OrbStasisBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(CONTAINS_PEARL, false).setValue(POWERED, false));
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CONTAINS_PEARL, POWERED);
    }

    @Override
    public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            this.updateState(state, worldIn, pos, state.getBlock());
        }
    }

    @Override
    public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        super.neighborChanged(p_220069_1_, p_220069_2_, p_220069_3_, p_220069_4_, p_220069_5_, p_220069_6_);
        updateState(p_220069_1_, p_220069_2_, p_220069_3_, p_220069_4_);
    }

    protected void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn) {
        boolean old_powered = state.getValue(POWERED);
        boolean new_powered = worldIn.hasNeighborSignal(pos);
        if (old_powered != new_powered) {
            BlockState new_state = state.setValue(POWERED, new_powered);
            worldIn.setBlock(pos, new_state, 3);
            worldIn.updateNeighborsAt(pos, this);
            updateTileEntity(new_state, worldIn, pos, null);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult blocktrace) {
        if (state.getValue(CONTAINS_PEARL)) return ActionResultType.sidedSuccess(false);

        ItemStack item_used = playerEntity.getItemBySlot(hand == Hand.MAIN_HAND? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND);
        if (item_used.getItem() == Items.ENDER_PEARL) {
            if (world instanceof ClientWorld) return ActionResultType.sidedSuccess(true);
            if (updateTileEntity(state, world, pos, playerEntity) && !playerEntity.isCreative()) {
                item_used.shrink(1);
            }
        }
        return ActionResultType.sidedSuccess(false);
    }

    private boolean updateTileEntity(BlockState state, World worldIn, BlockPos pos, PlayerEntity user) {
        TileEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof OrbStasisTile) {
            boolean now_has_pearl = ((OrbStasisTile)te).updateLock(state.getValue(POWERED), user);

            if (now_has_pearl != state.getValue(CONTAINS_PEARL)) {
                worldIn.setBlock(pos, state.setValue(CONTAINS_PEARL, now_has_pearl), 2);
                worldIn.updateNeighbourForOutputSignal(pos, state.getBlock());
            }

            return now_has_pearl;
        }
        return false;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
        return new OrbStasisTile();
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, World p_180641_2_, BlockPos p_180641_3_) {
        return state.getValue(CONTAINS_PEARL)? 15 : 0;
    }
}
