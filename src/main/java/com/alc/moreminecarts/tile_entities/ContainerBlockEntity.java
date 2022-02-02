package com.alc.moreminecarts.tile_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ContainerBlockEntity extends BaseContainerBlockEntity implements Container {

    // Must be initialized in
    protected NonNullList<ItemStack> items;

    protected ContainerBlockEntity(BlockEntityType<?> p_155076_, BlockPos p_155077_, BlockState p_155078_) {
        super(p_155076_, p_155077_, p_155078_);
        this.items = NonNullList.withSize(getSlotCount(), ItemStack.EMPTY);
    }

    public void load(CompoundTag p_155588_) {
        super.load(p_155588_);

        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(p_155588_, this.items);
    }

    protected void saveAdditional(CompoundTag p_187502_) {
        super.saveAdditional(p_187502_);

        ContainerHelper.saveAllItems(p_187502_, this.items);
    }

    public int getContainerSize() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.getItems().stream().allMatch(ItemStack::isEmpty);
    }

    public ItemStack getItem(int p_59611_) {
        return this.getItems().get(p_59611_);
    }

    public void setItem(int p_59315_, ItemStack p_59316_) {
        this.getItems().set(p_59315_, p_59316_);
        if (p_59316_.getCount() > this.getMaxStackSize()) {
            p_59316_.setCount(this.getMaxStackSize());
        }

    }

    @Override
    public ItemStack removeItem(int p_59613_, int p_59614_) {
        ItemStack itemstack = ContainerHelper.removeItem(this.getItems(), p_59613_, p_59614_);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    public ItemStack removeItemNoUpdate(int p_59630_) {
        return ContainerHelper.takeItem(this.getItems(), p_59630_);
    }

    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    protected void setItems(NonNullList<ItemStack> p_59110_) {
        this.items = p_59110_;
    }

    public boolean stillValid(Player p_59619_) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(p_59619_.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    public void clearContent() {
        this.getItems().clear();
    }

    public boolean canOpen(Player p_59643_) {
        return super.canOpen(p_59643_);
    }

    public abstract int getSlotCount();

}
