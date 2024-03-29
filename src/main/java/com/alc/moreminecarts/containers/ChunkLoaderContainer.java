package com.alc.moreminecarts.containers;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.misc.ChunkLoaderSlot;
import com.alc.moreminecarts.tile_entities.ChunkLoaderTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChunkLoaderContainer extends Container {

    private final IInventory inventory;
    private final IIntArray data;
    protected final World level;

    public ChunkLoaderContainer(int n, World world, PlayerInventory player_inventory, PlayerEntity player_entity) {
        super(MMReferences.chunk_loader_c, n);

        this.inventory = new Inventory(1);
        this.data = new IntArray(2);
        this.level = player_inventory.player.level;

        CommonInitialization(player_inventory);
    }

    // For use with the entity chunk loaders.
    public ChunkLoaderContainer(int n, World world, IInventory inventory, IIntArray data, PlayerInventory player_inventory, PlayerEntity player_entity) {
        super(MMReferences.chunk_loader_c, n);

        this.inventory = inventory;
        this.data = data;
        this.level = player_inventory.player.level;

        CommonInitialization(player_inventory);
    }

    // For use with tile entity chunk loaders.
    public ChunkLoaderContainer(int n, World world, BlockPos pos, PlayerInventory player_inventory, PlayerEntity player_entity) {
        super(MMReferences.chunk_loader_c, n);

        ChunkLoaderTile tile = (ChunkLoaderTile) world.getBlockEntity(pos);

        this.inventory = tile;
        this.data = tile.dataAccess;
        this.level = player_inventory.player.level;

        CommonInitialization(player_inventory);
    }

    public void CommonInitialization(PlayerInventory player_inventory) {

        checkContainerSize(inventory, 1);
        checkContainerDataCount(data, 2);

        this.addSlot(new ChunkLoaderSlot(inventory, 0, 112, 15){
            public void setChanged() {
                super.setChanged();
                ChunkLoaderContainer.this.slotsChanged(this.container);
            }});

        // player inventory slots, taken from the AbstractFurnaceContainer code.
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(player_inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(player_inventory, k, 8 + k * 18, 142));
        }

        this.addDataSlots(data);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return this.inventory.stillValid(player);
    }

    // Taken from the beacon container. No clue what this does really.
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (this.moveItemStackTo(itemstack1, 0, 1, false)) { //Forge Fix Shift Clicking in beacons with stacks larger then 1.
                return ItemStack.EMPTY;
            } else if (index >= 1 && index < 28) {
                if (!this.moveItemStackTo(itemstack1, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 28 && index < 37) {
                if (!this.moveItemStackTo(itemstack1, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 1, 37, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    public int getSize() {
        return 1;
    }

    public int getTimeLeft() {
        return Math.abs(this.data.get(0));
    }

    public double getLogProgress() {
        return (Math.log10( ((float)getTimeLeft()/ChunkLoaderTile.MAX_MINUTES)*9 + 1 ));
    }

    public boolean isEnabled() {
        return this.data.get(1) > 0;
    }

    public void setEnabled(boolean enabled) {
        this.setData(1, enabled? 1 : -1);
        this.broadcastChanges();
    }
}
