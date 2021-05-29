package com.alc.moreminecarts.tile_entities;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.containers.MinecartUnLoaderContainer;
import com.alc.moreminecarts.entities.ChunkLoaderCartEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.entity.item.minecart.FurnaceMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MinecartLoaderTile extends LockableTileEntity implements IMinecartUnLoaderTile {

    public enum ComparatorOutputType {
        done_loading,
        cart_full,
        cart_fullness;

        public int toInt() {
            switch(this) {
                case done_loading:
                    return 0;
                case cart_full:
                    return 1;
                case cart_fullness:
                    return 2;
            }
            return 3;
        }

        public static ComparatorOutputType next(ComparatorOutputType in) {
            switch(in) {
                case done_loading:
                    return cart_full;
                case cart_full:
                    return cart_fullness;
                case cart_fullness:
                    return done_loading;
            }
            return ComparatorOutputType.done_loading;
        }

        public static ComparatorOutputType fromInt(int n) {
            if (n == 0) return ComparatorOutputType.done_loading;
            else if (n == 1) return ComparatorOutputType.cart_full;
            else return ComparatorOutputType.cart_fullness;
        }
    }

    public static int MAX_COOLDOWN_TIME = 2;

    public static String LOCKED_MINECARTS_ONLY_PROPERTY = "locked_minecarts_only";
    public static String LEAVE_ONE_IN_STACK_PROPERTY = "leave_one_in_stack";
    public static String COMPARATOR_OUTPUT_PROPERTY = "comparator_output";
    public static String COOLDOWN_PROPERTY = "cooldown";

    protected NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    public final IIntArray dataAccess = new IIntArray() {
        @Override
        public int get(int index) {
            switch(index) {
                case 0:
                    return (locked_minecarts_only?1:0) + ((leave_one_in_stack?1:0) << 1) + (comparator_output.toInt() << 2);
                case 1:
                    // 0 for loader
                    return 0;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int set_to) {
            switch(index) {
                case 0:
                    locked_minecarts_only = set_to % 2 == 1;
                    leave_one_in_stack = (set_to & 2) == 2;
                    comparator_output = ComparatorOutputType.fromInt(set_to >> 2);
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

    public boolean locked_minecarts_only;
    public boolean leave_one_in_stack;
    public ComparatorOutputType comparator_output;

    public int comparator_output_value;
    public int cooldown_time;

    LazyOptional<IFluidHandler> fluid_handler = LazyOptional.of(() -> new FluidTank(2000));
    LazyOptional<IEnergyStorage> energy_handler = LazyOptional.of(() -> new EnergyStorage(2000));

    public MinecartLoaderTile() {
        super(MMReferences.minecart_loader_te);
        locked_minecarts_only = false;
        leave_one_in_stack = false;
        comparator_output = ComparatorOutputType.done_loading;
        comparator_output_value = -1;
    }

    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluid_handler.cast();
        }
        else if (cap == CapabilityEnergy.ENERGY) {
            return energy_handler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putBoolean(LOCKED_MINECARTS_ONLY_PROPERTY, locked_minecarts_only);
        compound.putBoolean(LEAVE_ONE_IN_STACK_PROPERTY, leave_one_in_stack);
        compound.putInt(COMPARATOR_OUTPUT_PROPERTY, comparator_output.toInt());
        compound.putInt(COOLDOWN_PROPERTY, cooldown_time);
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        locked_minecarts_only = compound.getBoolean(LOCKED_MINECARTS_ONLY_PROPERTY);
        leave_one_in_stack = compound.getBoolean(LEAVE_ONE_IN_STACK_PROPERTY);
        comparator_output = ComparatorOutputType.fromInt( compound.getInt(COMPARATOR_OUTPUT_PROPERTY) );
        cooldown_time = compound.getInt(COOLDOWN_PROPERTY);
        comparator_output_value = -1;
        super.load(state, compound);
    }

    public Container createMenu(int i, PlayerInventory inventory, PlayerEntity player) {
        return new MinecartUnLoaderContainer(i, level, worldPosition, inventory, player);
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
                    if (minecart instanceof ContainerMinecartEntity) {
                        criteria_total += doMinecartLoads((ContainerMinecartEntity) minecart);
                    } else if (minecart instanceof FurnaceMinecartEntity) {
                        criteria_total += doFuranceCartLoads((FurnaceMinecartEntity) minecart);
                    }
                }

                if (minecarts.size() == 0) criteria_total = 0;
                else criteria_total /= minecarts.size();

                if (comparator_output != ComparatorOutputType.cart_fullness)
                    criteria_total = (float) Math.floor(criteria_total);

                int new_comparator_output_value = (int) (criteria_total * 15);
                if (new_comparator_output_value != comparator_output_value) {
                    comparator_output_value = new_comparator_output_value;
                    level.updateNeighbourForOutputSignal(getBlockPos(), this.getBlockState().getBlock());
                }
            } else {
                decCooldown();
            }

        }
    }

    public List<AbstractMinecartEntity> getLoadableMinecartsInRange() {
        if (locked_minecarts_only) {
            List<AbstractMinecartEntity> acc = new ArrayList<AbstractMinecartEntity>();

            TileEntity te_above = level.getBlockEntity(getBlockPos().above());
            if (te_above instanceof LockingRailTile && ((LockingRailTile)te_above).locked_minecart != null ) acc.add(((LockingRailTile)te_above).locked_minecart);
            TileEntity te_below = level.getBlockEntity(getBlockPos().below());
            if (te_below instanceof LockingRailTile && ((LockingRailTile)te_below).locked_minecart != null ) acc.add(((LockingRailTile)te_below).locked_minecart);

            return acc;
        }
        else {
            return level.getEntitiesOfClass(AbstractMinecartEntity.class, this.getDectectionBox(), (entity) -> true);
        }
    }

    private AxisAlignedBB getDectectionBox() {
        BlockPos pos = getBlockPos();
        double d0 = 0.2D;
        return new AxisAlignedBB((double)pos.getX() + 0.2D, (double)pos.getY() - 1, (double)pos.getZ() + 0.2D, (double)(pos.getX() + 1) - 0.2D, (double)(pos.getY() + 2) - 0.2D, (double)(pos.getZ() + 1) - 0.2D);
    }

    public float doMinecartLoads(ContainerMinecartEntity minecart) {
        boolean changed = false;
        for (ItemStack stack : items) {

            if (!stack.isEmpty() && !(leave_one_in_stack && stack.getCount() == 1) ) {

                for (int i = 0; i < minecart.getContainerSize() && !stack.isEmpty(); i++) {

                    boolean did_load = false;

                    if (minecart.canPlaceItem(i, stack)) {

                        ItemStack add_to_stack = minecart.getItem(i);

                        if (add_to_stack.isEmpty()) {
                            ItemStack new_stack = stack.copy();
                            new_stack.setCount(1);
                            minecart.setItem(i, new_stack);
                            stack.shrink(1);
                            did_load = true;
                        }
                        else if (canMergeItems(add_to_stack, stack)) {
                            int true_count = stack.getCount() - (leave_one_in_stack? 1 : 0);
                            int to_fill = add_to_stack.getMaxStackSize() - add_to_stack.getCount();
                            int transfer = Math.min(1, Math.min(true_count, to_fill));
                            stack.shrink(transfer);
                            add_to_stack.grow(transfer);
                            did_load = transfer > 0;
                        }
                    }

                    if (did_load) {
                        changed = true;
                        break;
                    }
                }
            }
        }

        if (changed) {
            resetCooldown();
            setChanged();
        }

        if (comparator_output == ComparatorOutputType.done_loading) return changed? 0.0f : 1.0f;
        else if (minecart instanceof ChunkLoaderCartEntity && comparator_output == ComparatorOutputType.cart_fullness) {
            return ((ChunkLoaderCartEntity)minecart).getComparatorSignal() / 15.0f;
        }
        else {
            float fullness = Container.getRedstoneSignalFromContainer(minecart) / 15.0f;
            if (comparator_output == ComparatorOutputType.cart_full) fullness = (float)Math.floor(fullness);
            return fullness;
        }
    }

    // Copied from HopperTileEntity
    private static boolean canMergeItems(ItemStack p_145894_0_, ItemStack p_145894_1_) {
        if (p_145894_0_.getItem() != p_145894_1_.getItem()) {
            return false;
        } else if (p_145894_0_.getDamageValue() != p_145894_1_.getDamageValue()) {
            return false;
        } else {
            return p_145894_0_.getCount() > p_145894_0_.getMaxStackSize() ? false : ItemStack.tagMatches(p_145894_0_, p_145894_1_);
        }
    }

    public float doFuranceCartLoads(FurnaceMinecartEntity minecart) {
        boolean changed = false;

        TileEntity te_at_minecart = level.getBlockEntity(minecart.getCurrentRailPosition());

        if (te_at_minecart instanceof LockingRailTile && ((LockingRailTile)te_at_minecart).locked_minecart == minecart) {
            LockingRailTile locking_rail_tile = (LockingRailTile)te_at_minecart;

            if (locking_rail_tile.saved_fuel + 3600 <= 32000) {

                for (ItemStack stack : items) {
                    if (Ingredient.of(new IItemProvider[]{Items.COAL, Items.CHARCOAL}).test(stack) && !(leave_one_in_stack && stack.getCount() == 1)) {
                        stack.shrink(1);
                        locking_rail_tile.saved_fuel += 3600;
                        changed = true;
                    }
                }

            }
        }
        else {

            if (minecart.fuel + 3600 <= 32000) {

                for (ItemStack stack : items) {
                    if (Ingredient.of(new IItemProvider[]{Items.COAL, Items.CHARCOAL}).test(stack) && !(leave_one_in_stack && stack.getCount() == 1)) {
                        stack.shrink(1);
                        minecart.fuel += 3600;
                        changed = true;

                    }
                }
            }

            if (minecart.xPush == 0 && minecart.zPush == 0)
            {
                minecart.xPush = minecart.getDeltaMovement().x;
                minecart.zPush = minecart.getDeltaMovement().z;
            }

        }

        if (changed) {
            resetCooldown();
            setChanged();
        }

        if (comparator_output == ComparatorOutputType.done_loading) return changed? 0.0f : 1.0f;
        else {
            float fullness = Math.min(minecart.fuel / (32000.0f - 3600.0f), 1.0f);
            if (comparator_output == ComparatorOutputType.cart_full) fullness = (float)Math.floor(fullness);
            return fullness;
        }
    }

    public void resetCooldown() {
        cooldown_time = MAX_COOLDOWN_TIME;
    }

    public boolean isOnCooldown() {
        return cooldown_time != 0;
    }

    public void decCooldown() {
        cooldown_time -= 1;
        if (cooldown_time < 0) cooldown_time = 0;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        fluid_handler.invalidate();
        energy_handler.invalidate();
    }

    public FluidStack getFluidStack() {
        return fluid_handler.resolve().get().getFluidInTank(0);
    }

    public int getEnergyAmount() {
        return energy_handler.resolve().get().getEnergyStored();
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

    public int getContainerSize() {
        return 9;
    }

    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public ItemStack getItem(int p_70301_1_) {
        return this.items.get(p_70301_1_);
    }

    public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
        return ItemStackHelper.removeItem(this.items, p_70298_1_, p_70298_2_);
    }

    public ItemStack removeItemNoUpdate(int p_70304_1_) {
        return ItemStackHelper.takeItem(this.items, p_70304_1_);
    }

    public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
        this.items.set(p_70299_1_, p_70299_2_);
        if (p_70299_2_.getCount() > this.getMaxStackSize()) {
            p_70299_2_.setCount(this.getMaxStackSize());
        }
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
        this.items.clear();
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new StringTextComponent("Minecart Loader");
    }

    public int getComparatorSignal() {
        if (comparator_output_value < 0) return 0;
        return comparator_output_value;
    }
}
