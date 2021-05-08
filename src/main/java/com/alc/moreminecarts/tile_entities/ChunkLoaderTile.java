package com.alc.moreminecarts.tile_entities;

import com.alc.moreminecarts.MoreMinecartsConstants;
import com.alc.moreminecarts.blocks.ChunkLoaderBlock;
import com.alc.moreminecarts.containers.ChunkLoaderContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder("moreminecarts")
public class ChunkLoaderTile extends TileEntity implements ISidedInventory, ITickableTileEntity, INamedContainerProvider {
    public static final TileEntityType<ChunkLoaderTile> chunk_loader_te = null;
    public static final Item chunkrodite = null;
    public static final Item chunkrodite_block = null;

    public static String LAST_CHUNK_X_PROPERTY = "last_block_pos_x";
    public static String LAST_CHUNK_Z_PROPERTY = "last_block_pos_z";
    public static String TIME_LEFT_PROPERTY = "time_left";
    public static int MAX_TIME_LEFT = 10368000;

    protected ItemStack input_itemstack = ItemStack.EMPTY;
    public final IIntArray dataAccess = new IIntArray() {
        @Override
        public int get(int index) {
            switch(index) {
                case 0:
                    return ChunkLoaderTile.this.time_left;
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

    public boolean lit_last_tick;
    // This should never be zero. 1 is the minimum value here, for reasons.
    public int time_left;
    public int last_chunk_x;
    public int last_chunk_z;

    public ChunkLoaderTile() {
        super(chunk_loader_te);
        lit_last_tick = false;
        time_left = -1;
        last_chunk_x = getBlockPos().getX() >> 4;
        last_chunk_z = getBlockPos().getZ() >> 4;
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putInt(LAST_CHUNK_X_PROPERTY, last_chunk_x);
        compound.putInt(LAST_CHUNK_Z_PROPERTY, last_chunk_z);
        compound.putInt(TIME_LEFT_PROPERTY, time_left);
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        last_chunk_x = compound.getInt(LAST_CHUNK_X_PROPERTY);
        last_chunk_z = compound.getInt(LAST_CHUNK_Z_PROPERTY);
        time_left = compound.getInt(TIME_LEFT_PROPERTY);
        lit_last_tick = isLit();
        super.load(state, compound);
    }

    public Container createMenu(int i, PlayerInventory inventory, PlayerEntity player) {
        return new ChunkLoaderContainer(i, level, worldPosition, inventory, player);
    }

    public static int getBurnDuration(Item item) {
        if (item == Items.AIR) return -1;
        if (item == Items.QUARTZ) return 600;
        if (item == Items.EMERALD) return 6000;
        if (item == Items.EMERALD_BLOCK) return 54000;
        if (item == Items.DIAMOND) return 72000;
        if (item == Items.DIAMOND_BLOCK) return 648000;
        if (item == Items.NETHER_STAR) return 3456000;
        if (item == chunkrodite) return 18000;
        if (item == chunkrodite_block) return 162000;
        return -1;
    }

    public void tick() {

        if (isLit()) time_left--;

        if (!level.isClientSide) {

            int burn_duration = getBurnDuration(input_itemstack.getItem());
            if (burn_duration >= 0 && Math.abs(time_left) + burn_duration <= MAX_TIME_LEFT) {

                if (time_left > 0) time_left += burn_duration;
                else time_left -= burn_duration;

                input_itemstack.shrink(1);
            }

            int chunk_x = getBlockPos().getX() >> 4;
            int chunk_z = getBlockPos().getZ() >> 4;

            if (chunk_x != last_chunk_x || chunk_z != last_chunk_z) {
                forceChucksAt(last_chunk_x, last_chunk_z, false);

                last_chunk_x = chunk_x;
                last_chunk_z = chunk_z;

                lit_last_tick = !isLit();
            }

            if (lit_last_tick != isLit()) {
                if (isLit()) {
                    forceChucksAt(chunk_x, chunk_z, true);
                }
                else {
                    forceChucksAt(chunk_x, chunk_z, false);
                }
                this.level.setBlock(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(ChunkLoaderBlock.POWERED, Boolean.valueOf(this.isLit())), 3);

            }

            this.setChanged();
        }

        lit_last_tick = isLit();
    }

    private void forceChucksAt(int chunk_x, int chunk_z, boolean add) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                ForgeChunkManager.forceChunk((ServerWorld) level, MoreMinecartsConstants.modid, getBlockPos(), chunk_x + i, chunk_z + j, add, true);
            }
        }
    }

    @Override
    public void setRemoved() {
        if (!level.isClientSide) {

            int chunk_x = getBlockPos().getX() >> 4;
            int chunk_z = getBlockPos().getZ() >> 4;

            forceChucksAt(chunk_x, chunk_z, false);
        }
        super.setRemoved();
    }

    public boolean isLit() {
        return time_left > 1 && isEnabled();
    }

    public boolean isEnabled() {
        return time_left > 0;
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

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }

    // For dropping chunkrodite
    public static void dropExtras(World world, int time_left, BlockPos pos) {
        int count = (int)Math.floor(time_left / 24000.0f);
        Item to_drop = chunkrodite;
        if (count > 64) {
            count = (int)Math.floor(time_left / 9.0f);
            to_drop = chunkrodite_block;
            if (count > 64) count = 64; // Should never occur, but just in case.
        }

        if (count != 0) {
            ItemStack drop_stack = new ItemStack(to_drop, count);
            NonNullList<ItemStack> drops = NonNullList.withSize(1, drop_stack);
            InventoryHelper.dropContents(world, pos, drops);
        }
    }
}
