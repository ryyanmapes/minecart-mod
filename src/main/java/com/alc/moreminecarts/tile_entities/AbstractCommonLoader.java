package com.alc.moreminecarts.tile_entities;

import com.alc.moreminecarts.misc.SettableEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommonLoader extends LockableTileEntity {

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

        public static MinecartLoaderTile.ComparatorOutputType next(MinecartLoaderTile.ComparatorOutputType in) {
            switch(in) {
                case done_loading:
                    return cart_full;
                case cart_full:
                    return cart_fullness;
                case cart_fullness:
                    return done_loading;
            }
            return MinecartLoaderTile.ComparatorOutputType.done_loading;
        }

        public static MinecartLoaderTile.ComparatorOutputType fromInt(int n) {
            if (n == 0) return MinecartLoaderTile.ComparatorOutputType.done_loading;
            else if (n == 1) return MinecartLoaderTile.ComparatorOutputType.cart_full;
            else return MinecartLoaderTile.ComparatorOutputType.cart_fullness;
        }
    }

    public class LoaderTank extends FluidTank {
        public LoaderTank(int capacity) {
            super(capacity);
        }

        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            changed_flag = true;
        }
    }

    public class LoaderBattery extends SettableEnergyStorage {
        public LoaderBattery(int capacity) {
            super(capacity);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int ret = super.receiveEnergy(maxReceive, simulate);
            if (!simulate) changed_flag = true;
            return ret;
        }
        @Override
        public int extractEnergy(int maxReceive, boolean simulate) {
            int ret = super.extractEnergy(maxReceive, simulate);
            if (!simulate) changed_flag = true;
            return ret;
        }
    }

    public static int MAX_COOLDOWN_TIME = 2;
    public static int FLUID_CAPACITY = 2000;
    public static int ENERGY_CAPACITY = 2000;

    public static String ENERGY_PROPERTY = "energy";
    public static String REDSTONE_OUTPUT_PROPERTY = "redstone_output";
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
                    return (locked_minecarts_only?1:0) + ((leave_one_in_stack?1:0) << 1) + (comparator_output.toInt() << 2) + ((redstone_output?1:0) << 4);
                case 1:
                    return getIsUnloader()? 1 : 0;
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
                    comparator_output = MinecartLoaderTile.ComparatorOutputType.fromInt((set_to & 12) >> 2);
                    redstone_output = (set_to & 16) == 16;
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

    public boolean redstone_output;
    public boolean locked_minecarts_only;
    public boolean leave_one_in_stack;
    public MinecartLoaderTile.ComparatorOutputType comparator_output;

    public boolean last_redstone_output;
    public int comparator_output_value;
    public int cooldown_time;

    public boolean changed_flag;

    LazyOptional<IFluidHandler> fluid_handler = LazyOptional.of(() -> new LoaderTank(FLUID_CAPACITY));
    LazyOptional<IEnergyStorage> energy_handler = LazyOptional.of(() -> new LoaderBattery(ENERGY_CAPACITY));

    public abstract boolean getIsUnloader();

    public AbstractCommonLoader(TileEntityType<?> p_i48285_1_) {
        super(p_i48285_1_);
        locked_minecarts_only = false;
        leave_one_in_stack = false;
        comparator_output = MinecartLoaderTile.ComparatorOutputType.done_loading;
        comparator_output_value = -1;
    }

    @Override
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
        compound.putBoolean(REDSTONE_OUTPUT_PROPERTY, redstone_output);
        compound.putInt(COMPARATOR_OUTPUT_PROPERTY, comparator_output.toInt());
        compound.putInt(COOLDOWN_PROPERTY, cooldown_time);
        ((FluidTank)fluid_handler.orElse(null)).writeToNBT(compound);
        compound.putInt(ENERGY_PROPERTY, energy_handler.orElse(null).getEnergyStored());
        ItemStackHelper.saveAllItems(compound, this.items);
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        locked_minecarts_only = compound.getBoolean(LOCKED_MINECARTS_ONLY_PROPERTY);
        leave_one_in_stack = compound.getBoolean(LEAVE_ONE_IN_STACK_PROPERTY);
        redstone_output = compound.getBoolean(REDSTONE_OUTPUT_PROPERTY);
        comparator_output = MinecartLoaderTile.ComparatorOutputType.fromInt( compound.getInt(COMPARATOR_OUTPUT_PROPERTY) );
        cooldown_time = compound.getInt(COOLDOWN_PROPERTY);
        comparator_output_value = -1;
        last_redstone_output = !redstone_output;
        FluidTank tank = ((FluidTank)fluid_handler.orElseGet(null));
        tank.setFluid(tank.readFromNBT(compound).getFluid());
        energy_handler.orElse(null).receiveEnergy(compound.getInt(ENERGY_PROPERTY), false);
        changed_flag = true;
        ItemStackHelper.loadAllItems(compound, this.items);
        super.load(state, compound);
    }

    protected List<AbstractMinecartEntity> getLoadableMinecartsInRange() {
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

    protected AxisAlignedBB getDectectionBox() {
        BlockPos pos = getBlockPos();
        double d0 = 0.2D;
        return new AxisAlignedBB((double)pos.getX() + 0.2D, (double)pos.getY() - 1, (double)pos.getZ() + 0.2D, (double)(pos.getX() + 1) - 0.2D, (double)(pos.getY() + 2) - 0.2D, (double)(pos.getZ() + 1) - 0.2D);
    }

    // Copied from HopperTileEntity
    protected static boolean canMergeItems(ItemStack p_145894_0_, ItemStack p_145894_1_) {
        if (p_145894_0_.getItem() != p_145894_1_.getItem()) {
            return false;
        } else if (p_145894_0_.getDamageValue() != p_145894_1_.getDamageValue()) {
            return false;
        } else {
            return p_145894_0_.getCount() > p_145894_0_.getMaxStackSize() ? false : ItemStack.tagMatches(p_145894_0_, p_145894_1_);
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
        return fluid_handler.orElse(null).getFluidInTank(0);
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
    public SUpdateTileEntityPacket getUpdatePacket(){
        CompoundNBT compound = new CompoundNBT();
        ((FluidTank)fluid_handler.orElse(null)).writeToNBT(compound);
        compound.putInt(ENERGY_PROPERTY, energy_handler.orElse(null).getEnergyStored());
        return new SUpdateTileEntityPacket(getBlockPos(), -1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
        CompoundNBT compound = pkt.getTag();
        FluidTank tank = ((FluidTank)fluid_handler.resolve().get());
        tank.setFluid(tank.readFromNBT(compound).getFluid());
        ((SettableEnergyStorage)energy_handler.orElse(null)).setEnergy(compound.getInt(ENERGY_PROPERTY));
    }

    @Override
    public void setChanged() {
        if (level != null && !level.isClientSide) {
            super.setChanged();
            level.markAndNotifyBlock(getBlockPos(), level.getChunkAt(getBlockPos()), getBlockState(), getBlockState(), 2, 0);
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

    public boolean getOutputsRedstone() {
        return redstone_output;
    }

    public int getSignal() {
        if (comparator_output_value < 0) return 0;
        return comparator_output_value;
    }

}
