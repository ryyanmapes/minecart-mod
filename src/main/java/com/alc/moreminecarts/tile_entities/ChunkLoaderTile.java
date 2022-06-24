package com.alc.moreminecarts.tile_entities;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.blocks.containers.ChunkLoaderBlock;
import com.alc.moreminecarts.containers.ChunkLoaderContainer;
import com.alc.moreminecarts.registry.MMItems;
import com.alc.moreminecarts.registry.MMTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.world.ForgeChunkManager;

import javax.annotation.Nullable;

public class ChunkLoaderTile extends ContainerBlockEntity implements WorldlyContainer, Container {
    public static String LAST_CHUNK_X_PROPERTY = "last_block_pos_x";
    public static String LAST_CHUNK_Z_PROPERTY = "last_block_pos_z";
    public static String TIME_LEFT_PROPERTY = "time_left";
    public static int MAX_TIME = 10368000;
    public static int MAX_MINUTES = 8640;

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            switch(index) {
                case 0:
                    return (int)Math.ceil( (Math.abs(ChunkLoaderTile.this.time_left) - 1) / 1200.0) * get(1);
                case 1:
                    return ChunkLoaderTile.this.time_left > 0? 1 : -1;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int set_to) {
            switch(index) {
                case 0:
                    ChunkLoaderTile.this.time_left = set_to * 1200;
                    break;
                case 1:
                    ChunkLoaderTile.this.time_left = Math.abs(ChunkLoaderTile.this.time_left) * (set_to > 0? 1 : -1);
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public boolean lit_last_tick;
    // This should never be zero. 1 is the minimum value here, for reasons.
    public int time_left;
    public int last_chunk_x;
    public int last_chunk_z;

    public ChunkLoaderTile(BlockPos pos, BlockState state) {
        super(MMTileEntities.CHUNK_LOADER_TILE_ENTITY.get(), pos, state);

        this.items = NonNullList.withSize(1, ItemStack.EMPTY);

        lit_last_tick = false;
        time_left = -1;
        last_chunk_x = getBlockPos().getX() >> 4;
        last_chunk_z = getBlockPos().getZ() >> 4;
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt(LAST_CHUNK_X_PROPERTY, last_chunk_x);
        compound.putInt(LAST_CHUNK_Z_PROPERTY, last_chunk_z);
        compound.putInt(TIME_LEFT_PROPERTY, time_left);
    }

    @Override
    public void load(CompoundTag compound) {
        last_chunk_x = compound.getInt(LAST_CHUNK_X_PROPERTY);
        last_chunk_z = compound.getInt(LAST_CHUNK_Z_PROPERTY);
        time_left = compound.getInt(TIME_LEFT_PROPERTY);
        lit_last_tick = isLit();

        super.load(compound);
    }

    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new ChunkLoaderContainer(i, inventory, this, this.dataAccess);
    }

    @Override
    protected AbstractContainerMenu createMenu(int p_213906_1_, Inventory p_213906_2_) {
        return null;
    }

    public static int getBurnDuration(Item item) {
        double multiplier = MMConstants.CONFIG_CHUNK_LOADER_MULTIPLIER.get();
        double fuel = -1;

        if (item == Items.QUARTZ) fuel = 200;
        else if (item == Items.AMETHYST_SHARD) fuel = 600;
        else if (item == Items.AMETHYST_BLOCK) fuel = 600;
        else if (item == Items.EMERALD) fuel = 6000;
        else if (item == Items.EMERALD_BLOCK) fuel = 54000;
        else if (item == Items.DIAMOND) fuel = 72000;
        else if (item == Items.DIAMOND_BLOCK) fuel = 648000;
        else if (item == Items.NETHER_STAR) fuel = 3456000;
        else if (item == MMItems.CHUNKRODITE.get()) fuel = 18000;
        else if (item == MMItems.CHUNKRODITE_BLOCK_ITEM.get()) fuel = 162000;

        fuel *= multiplier;
        if (fuel <= 0) return -1;
        else return (int)Math.ceil(fuel);
    }

    public static void doTick(Level level, BlockPos pos, BlockState state, ChunkLoaderTile ent) {
        ent.tick();
    }

    public void tick() {

        boolean changed_flag = false;
        if (isLit()) time_left--;

        if (!level.isClientSide()) {

            int burn_duration = getBurnDuration(items.get(0).getItem());
            if (burn_duration >= 0 && Math.abs(time_left) + burn_duration <= MAX_TIME) {
                changed_flag = true;

                if (time_left > 0) time_left += burn_duration;
                else time_left -= burn_duration;

                items.get(0).shrink(1);
            }

            int chunk_x = getBlockPos().getX() >> 4;
            int chunk_z = getBlockPos().getZ() >> 4;

            if (chunk_x != last_chunk_x || chunk_z != last_chunk_z) {
                changed_flag = true;

                forceChucksAt(last_chunk_x, last_chunk_z, false);

                last_chunk_x = chunk_x;
                last_chunk_z = chunk_z;

                lit_last_tick = !isLit();
            }

            if (lit_last_tick != isLit()) {
                changed_flag = true;

                if (isLit()) {
                    forceChucksAt(chunk_x, chunk_z, true);
                }
                else {
                    forceChucksAt(chunk_x, chunk_z, false);
                }

                this.level.setBlock(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(ChunkLoaderBlock.POWERED, Boolean.valueOf(this.isLit())), 3);

            }

            if (changed_flag)
                this.setChanged();
        }

        lit_last_tick = isLit();
    }

    private void forceChucksAt(int chunk_x, int chunk_z, boolean add) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                boolean success = ForgeChunkManager.forceChunk((ServerLevel) level, MMConstants.modid, getBlockPos(), chunk_x + i, chunk_z + j, add, false);
                //MoreMinecartsMod.LOGGER.log(org.apache.logging.log4j.Level.WARN, "Turning chunks " + (add? "on" : "off") + ": " + (chunk_x + i) + " " + (chunk_z + j) + " " + (success? "Successful!" : "Failed!"));

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

    // Inventory stuff below, taken from AbstractFurnaceTileEntity.

    public int[] getSlotsForFace(Direction p_180463_1_) {
        return new int[0];
    }

    public boolean canPlaceItemThroughFace(int p_180462_1_, ItemStack p_180462_2_, @Nullable Direction p_180462_3_) {
        return true;
    }

    public boolean canTakeItemThroughFace(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public int getSlotCount() {
        return 1;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("Chunk Loader");
    }

    // For dropping MMItemReferences.
    public static void dropExtras(Level world, int time_left, BlockPos pos) {
        double multiplier = MMConstants.CONFIG_CHUNK_LOADER_MULTIPLIER.get();
        if (multiplier == 0) return;

        int count = (int)Math.floor( Math.abs(time_left / multiplier) / 24000.0f);

        Item to_drop = MMItems.CHUNKRODITE.get();
        if (count > 64) {
            count = (int)Math.floor(count / 9.0f);
            to_drop = MMItems.CHUNKRODITE_BLOCK_ITEM.get();
            if (count > 64) count = 64; // Should never occur, but just in case.
        }

        if (count != 0) {
            ItemStack drop_stack = new ItemStack(to_drop, count);
            NonNullList<ItemStack> drops = NonNullList.withSize(1, drop_stack);
            Containers.dropContents(world, pos, drops);
        }
    }

    public int getComparatorSignal() {
        float true_time_left = Math.abs(time_left) - 1;
        double log_proportion = Math.log10( ((true_time_left/ChunkLoaderTile.MAX_TIME)*9 + 1 ));
        return (int)Math.ceil(log_proportion * 15);
    }


}
