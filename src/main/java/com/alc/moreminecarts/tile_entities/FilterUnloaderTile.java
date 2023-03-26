package com.alc.moreminecarts.tile_entities;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.containers.FilterUnloaderContainer;
import com.alc.moreminecarts.entities.ChunkLoaderCartEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.List;

public class FilterUnloaderTile extends AbstractCommonLoader implements ITickableTileEntity {

    public enum FilterType {
        allow_per_slot,
        allow_for_all,
        disallow_for_all;

        public int toInt() {
            switch(this) {
                case allow_per_slot:
                    return 0;
                case allow_for_all:
                    return 1;
                case disallow_for_all:
                    return 2;
            }
            return 3;
        }

        public static FilterUnloaderTile.FilterType next(FilterUnloaderTile.FilterType in) {
            switch(in) {
                case allow_per_slot:
                    return allow_for_all;
                case allow_for_all:
                    return disallow_for_all;
                case disallow_for_all:
                    return allow_per_slot;
            }
            return FilterUnloaderTile.FilterType.allow_per_slot;
        }

        public static FilterUnloaderTile.FilterType fromInt(int n) {
            if (n == 0) return FilterUnloaderTile.FilterType.allow_per_slot;
            else if (n == 1) return FilterUnloaderTile.FilterType.allow_for_all;
            else return FilterUnloaderTile.FilterType.disallow_for_all;
        }
    }

    public static int VALID_ITEM_SLOTS = 9;
    public static final int[] VALID_TAKE_SLOTS = new int[]{0,1,2,3,4,5,6,7,8};

    public FilterUnloaderTile() {
        super(MMReferences.filter_unloader_te);
        last_redstone_output = !redstone_output;
    }

    @Override
    public boolean getIsUnloader() {
        return true;
    }

    @Override
    public Container createMenu(int i, PlayerInventory inventory, PlayerEntity player) {
        setChanged();
        return new FilterUnloaderContainer(i, level, worldPosition, inventory, player);
    }

    @Override
    protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
        return null;
    }

    public void tick() {

        if (!level.isClientSide) {

            if (!isOnCooldown()) {
                List<AbstractMinecartEntity> minecarts = getLoadableMinecartsInRange();
                float criteria_total = 0;
                for (AbstractMinecartEntity minecart : minecarts) {

                    // No fluid or electric unloads

                    if (minecart instanceof ContainerMinecartEntity && !(minecart instanceof ChunkLoaderCartEntity)) {
                        criteria_total += doMinecartUnloads((ContainerMinecartEntity) minecart);
                    }

                }

                if (minecarts.size() == 0) criteria_total = 0;
                else criteria_total /= minecarts.size();

                if (comparator_output != ComparatorOutputType.cart_fullness)
                    criteria_total = (float) Math.floor(criteria_total);

                int new_comparator_output_value = (int) (criteria_total * 15);
                if (new_comparator_output_value != comparator_output_value || last_redstone_output != redstone_output) {
                    comparator_output_value = new_comparator_output_value;
                    last_redstone_output = redstone_output;
                    level.updateNeighbourForOutputSignal(getBlockPos(), this.getBlockState().getBlock());
                    level.updateNeighborsAt(getBlockPos(), this.getBlockState().getBlock());
                }

                if (changed_flag) {
                    this.setChanged();
                    changed_flag = false;
                }

            } else {
                decCooldown();
            }

        }
    }

    public float doMinecartUnloads(ContainerMinecartEntity minecart) {
        boolean changed = false;
        boolean all_empty = true;

        for (int i = 0; i < minecart.getContainerSize(); i++) {

            ItemStack unloadingStack = minecart.getItem(i);

            if (unloadingStack.isEmpty() || (leave_one_in_stack && unloadingStack.getCount() == 1)) continue;
            all_empty = false;

            for (int j = 0; j < VALID_ITEM_SLOTS; j++) {
                ItemStack add_to_stack = this.getItem(j);

                if (filterType == FilterType.allow_per_slot) {

                    ItemStack check_stack = this.getItem(j+VALID_ITEM_SLOTS);

                    if (!check_stack.isEmpty() && !itemsMatch(check_stack, unloadingStack)) {
                        continue;
                    }

                }
                else
                {
                    boolean matchesAny = false;

                    for (int k = VALID_ITEM_SLOTS; k < VALID_ITEM_SLOTS * 2; k++) {

                        ItemStack check_stack = this.getItem(k);

                        if (!check_stack.isEmpty() && itemsMatch(check_stack, unloadingStack)) {
                            matchesAny = true;
                            break;
                        }

                    }

                    if (matchesAny != (filterType == FilterType.allow_for_all)) continue;
                }

                boolean did_load = false;

                if (add_to_stack.isEmpty()) {
                    int true_count = unloadingStack.getCount() - (leave_one_in_stack? 1 : 0);
                    ItemStack new_stack = unloadingStack.copy();
                    int transfer_amount = Math.min(8, true_count);
                    new_stack.setCount(transfer_amount);
                    this.setItem(j, new_stack);
                    unloadingStack.shrink(transfer_amount);
                    did_load = true;
                }
                else if (canMergeItems(add_to_stack, unloadingStack)) {
                    int true_count = unloadingStack.getCount() - (leave_one_in_stack? 1 : 0);
                    int to_fill = add_to_stack.getMaxStackSize() - add_to_stack.getCount();
                    int transfer = Math.min(8, Math.min(true_count, to_fill));
                    unloadingStack.shrink(transfer);
                    add_to_stack.grow(transfer);
                    did_load = transfer > 0;
                }

                if (did_load) {
                    changed = true;
                    break;
                }

            }
        }

        if (changed) {
            resetCooldown();
            changed_flag = true;
        }

        if (comparator_output == ComparatorOutputType.done_loading) return changed? 0.0f : 1.0f;
        else if (comparator_output == ComparatorOutputType.cart_full) return all_empty? 1.0f : 0.0f;
        else if (minecart instanceof ChunkLoaderCartEntity && comparator_output == ComparatorOutputType.cart_fullness) {
            return ((ChunkLoaderCartEntity)minecart).getComparatorSignal() / 15.0f;
        }
        else {
            return Container.getRedstoneSignalFromContainer(minecart) / 15.0f;
        }
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new StringTextComponent("Filtered Unloader");
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack p_180462_2_, @Nullable Direction p_180462_3_) {
        return false;
    }

    @Override
    public int[] getSlotsForFace(Direction p_58363_) {
        return VALID_TAKE_SLOTS;
    }

    public boolean itemsMatch(ItemStack a, ItemStack b) {
        if (a.getItem() != b.getItem()) {
            return false;
        } else if (a.getDamageValue() != b.getDamageValue()) {
            return false;
        } else {
            return ItemStack.tagMatches(a, b);
        }
    }

    @Override
    public boolean canTakeItemThroughFace(int p_58392_, ItemStack p_58393_, Direction p_58394_) {
        return p_58392_ < VALID_ITEM_SLOTS;
    }

    net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
            net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.NORTH);

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (!this.remove && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handlers[0].cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (int x = 0; x < handlers.length; x++)
            handlers[x].invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.handlers = net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.NORTH);
    }

    @Override
    public int getContainerSize() {
        return 18;
    }
}
