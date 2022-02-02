package com.alc.moreminecarts.blocks;

import com.alc.moreminecarts.tile_entities.OrbStasisTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class OrbStasisBlock extends Block implements EntityBlock {

    public static final BooleanProperty CONTAINS_PEARL = BooleanProperty.create("has_orb");
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public OrbStasisBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(CONTAINS_PEARL, false).setValue(POWERED, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONTAINS_PEARL, POWERED);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            this.updateState(state, worldIn, pos, state.getBlock());
        }
    }

    @Override
    public void neighborChanged(BlockState p_220069_1_, Level p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        super.neighborChanged(p_220069_1_, p_220069_2_, p_220069_3_, p_220069_4_, p_220069_5_, p_220069_6_);
        updateState(p_220069_1_, p_220069_2_, p_220069_3_, p_220069_4_);
    }

    protected void updateState(BlockState state, Level worldIn, BlockPos pos, Block blockIn) {
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
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player playerEntity, InteractionHand hand, BlockHitResult blocktrace) {
        if (state.getValue(CONTAINS_PEARL)) return InteractionResult.PASS;

        ItemStack item_used = playerEntity.getItemInHand(hand);
        if (item_used.getItem() == Items.ENDER_PEARL) {
            if (world.isClientSide) return InteractionResult.CONSUME;
            if (updateTileEntity(state, world, pos, playerEntity) && !playerEntity.isCreative()) {
                item_used.shrink(1);
            }
        }
        return InteractionResult.PASS;
    }

    private boolean updateTileEntity(BlockState state, Level worldIn, BlockPos pos, Player user) {
        BlockEntity te = worldIn.getBlockEntity(pos);
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153277_, BlockState p_153278_) {
        return new OrbStasisTile(p_153277_, p_153278_);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level p_180641_2_, BlockPos p_180641_3_) {
        return state.getValue(CONTAINS_PEARL)? 15 : 0;
    }
}
