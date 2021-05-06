package com.alc.moreminecarts.tile_entities;

import com.alc.moreminecarts.MoreMinecartsMod;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.FurnaceContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;

import javax.annotation.Nullable;

public class ChunkLoaderTile extends TileEntity implements ISidedInventory, ITickableTileEntity {

    public static final TileEntityType chunk_loader_tile = null;
    public static final String LAST_BLOCK_POS_X_PROPERTY = "last_block_pos_x";
    public static final String LAST_BLOCK_POS_Y_PROPERTY = "last_block_pos_y";
    public static final String LAST_BLOCK_POS_Z_PROPERTY = "last_block_pos_z";
    public static int MAX_TIME_LEFT = 7200001;

    protected ItemStack input_itemstack = ItemStack.EMPTY;
    protected final IIntArray dataAccess = new IIntArray() {
        @Override
        public int get(int index) {
            switch(index) {
                case 0:
                    return ChunkLoaderTile.this.time_left * (is_enabled ? 1 : -1);
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int set_to) {
            switch(index) {
                case 0:
                    ChunkLoaderTile.this.time_left = set_to;
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public boolean is_enabled;
    // This should never be zero. 1 is the minimum value here, for reasons.
    public int time_left;
    public BlockPos last_block_pos;

    public ChunkLoaderTile() {
        super(chunk_loader_tile);
        is_enabled = false;
        time_left = 1;
        last_block_pos = worldPosition;
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putInt(LAST_BLOCK_POS_X_PROPERTY, last_block_pos.getX());
        compound.putInt(LAST_BLOCK_POS_Y_PROPERTY, last_block_pos.getY());
        compound.putInt(LAST_BLOCK_POS_Z_PROPERTY, last_block_pos.getZ());
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        int new_old_x = compound.getInt(LAST_BLOCK_POS_X_PROPERTY);
        int new_old_y = compound.getInt(LAST_BLOCK_POS_Y_PROPERTY);
        int new_old_z = compound.getInt(LAST_BLOCK_POS_Z_PROPERTY);
        last_block_pos = new BlockPos(new_old_x, new_old_y, new_old_z);
        super.load(state, compound);
    }

    protected Container createMenu(int i, PlayerInventory inventory) {
        return new FurnaceContainer(i, inventory, this, this.dataAccess);
    }

    public static int getBurnDuration(Item item) {
        if (item == Items.AIR) return -1;
        if (item == Items.QUARTZ) return 1200;
        if (item == Items.EMERALD) return 6000;
        if (item == Items.EMERALD_BLOCK) return 54000;
        if (item == Items.DIAMOND) return 72000;
        if (item == Items.DIAMOND_BLOCK) return 648000;
        if (item == Items.NETHER_STAR) return 3456000;
        return -1;
    }

    public void tick() {

        boolean lit_last_tick = time_left == 1;
        if (isLit()) time_left--;

        if (!level.isClientSide) {

            int burn_duration = getBurnDuration(input_itemstack.getItem());
            if (burn_duration >= 0 && time_left + burn_duration <= MAX_TIME_LEFT) {
                time_left += burn_duration;
                input_itemstack.shrink(1);
            }

            if (isLit()) {
                if (worldPosition != last_block_pos || !lit_last_tick) {
                    forceChucksAt(last_block_pos, false);
                    forceChucksAt(worldPosition, true);
                    last_block_pos = worldPosition;
                }
            }
            else {
                forceChucksAt(last_block_pos, false);
                forceChucksAt(worldPosition, false);
                last_block_pos = worldPosition;
            }

            this.setChanged();
        }
    }

    private void forceChucksAt(BlockPos pos, boolean add) {
        int last_chunk_x = pos.getX() >> 4;
        int last_chunk_z = pos.getZ() >> 4;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                ForgeChunkManager.forceChunk((ServerWorld) level, MoreMinecartsMod.MODID, pos, last_chunk_x, last_chunk_z, add, true);
            }
        }
    }

    public boolean isLit() {
        return time_left > 1 && is_enabled;
    }

    // Inventory stuff below.

    @Override
    public int[] getSlotsForFace(Direction p_180463_1_) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int p_180462_1_, ItemStack p_180462_2_, @Nullable Direction p_180462_3_) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
        return false;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return input_itemstack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return input_itemstack;
    }

    @Override
    public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
        return input_itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return input_itemstack;
    }

    @Override
    public void setItem(int slot, ItemStack to_set) {
        input_itemstack = to_set;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        input_itemstack = ItemStack.EMPTY;
    }
}
