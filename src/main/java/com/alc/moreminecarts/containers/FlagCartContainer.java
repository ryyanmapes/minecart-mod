package com.alc.moreminecarts.containers;

import com.alc.moreminecarts.entities.FlagCartEntity;
import com.alc.moreminecarts.misc.FlagUtil;
import com.alc.moreminecarts.registry.MMContainers;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FlagCartContainer extends AbstractContainerMenu {

    private final Container inventory;
    private final ContainerData data;
    protected final Level level;

    // For use on the client.
    public FlagCartContainer(int n, Level world, Inventory player_inventory, Player player_entity) {
        super(MMContainers.FLAG_CART_CONTAINER.get(), n);

        this.inventory = new SimpleContainer(9);
        this.data = new SimpleContainerData(2);
        this.level = world;

        CommonInitialization(player_inventory);
    }

    // For use with the entity cart.
    public FlagCartContainer(int n, Level world, FlagCartEntity entity, Inventory player_inventory, Player player_entity) {
        super(MMContainers.FLAG_CART_CONTAINER.get(), n);

        this.inventory = entity;
        this.data = entity.dataAccess;
        this.level = player_inventory.player.level;

        CommonInitialization(player_inventory);
    }

    public void CommonInitialization(Inventory player_inventory) {

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
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    // Taken from the chest container. No clue what this does really.
    @Override
    public ItemStack quickMoveStack(Player p_82846_1_, int p_82846_2_) {
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
