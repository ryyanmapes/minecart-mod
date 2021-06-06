package com.alc.moreminecarts.containers;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.FlagCartEntity;
import com.alc.moreminecarts.misc.FlagUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.world.World;

public class FlagCartContainer extends Container {

    private final IInventory inventory;
    private final IIntArray data;
    protected final World level;

    // For use on the client.
    public FlagCartContainer(int n, World world, PlayerInventory player_inventory, PlayerEntity player_entity) {
        super(MMReferences.flag_cart_c, n);

        this.inventory = new Inventory(9);
        this.data = new IntArray(2);
        this.level = world;

        CommonInitialization(player_inventory);
    }

    // For use with the entity cart.
    public FlagCartContainer(int n, World world, FlagCartEntity entity, PlayerInventory player_inventory, PlayerEntity player_entity) {
        super(MMReferences.flag_cart_c, n);

        this.inventory = entity;
        this.data = entity.dataAccess;
        this.level = player_inventory.player.level;

        CommonInitialization(player_inventory);
    }

    public void CommonInitialization(PlayerInventory player_inventory) {

        checkContainerSize(inventory, 9);
        checkContainerDataCount(data, 2);

        // content slots
        for(int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 42));
        }

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

    // Taken from the chest container. No clue what this does really.
    @Override
    public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
        int containerRows = 1;
        ItemStack lvt_3_1_ = ItemStack.EMPTY;
        Slot lvt_4_1_ = (Slot)this.slots.get(p_82846_2_);
        if (lvt_4_1_ != null && lvt_4_1_.hasItem()) {
            ItemStack lvt_5_1_ = lvt_4_1_.getItem();
            lvt_3_1_ = lvt_5_1_.copy();
            if (p_82846_2_ < containerRows * 9) {
                if (!this.moveItemStackTo(lvt_5_1_, containerRows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(lvt_5_1_, 0, containerRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (lvt_5_1_.isEmpty()) {
                lvt_4_1_.set(ItemStack.EMPTY);
            } else {
                lvt_4_1_.setChanged();
            }
        }

        return lvt_3_1_;
    }

    public int getSize() {
        return 9-getDiscludedSlots();
    }

    public int getSelectedSlot() {
        return data.get(0);
    }

    public int getDiscludedSlots() {
        return data.get(1);
    }

    public void changeSelection(boolean is_decrement, boolean is_disclude) {
        int selected_slot = getSelectedSlot();
        int discluded_slots = getDiscludedSlots();

        if (is_disclude) {
            if (!is_decrement && discluded_slots >= 8) return;
            else if (is_decrement && discluded_slots <= 0) return;
            discluded_slots += is_decrement ? -1 : 1;
            data.set(1, discluded_slots);
        }
        else {
            selected_slot = FlagUtil.getNextSelectedSlot(selected_slot, discluded_slots, is_decrement);
            data.set(0, selected_slot);
        }
    }
}
