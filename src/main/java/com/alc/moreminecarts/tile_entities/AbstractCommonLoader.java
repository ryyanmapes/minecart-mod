package com.alc.moreminecarts.tile_entities;

import com.alc.moreminecarts.misc.SettableEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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

public abstract class AbstractCommonLoader extends ContainerBlockEntity implements WorldlyContainer {

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
    public static String FILTER_PROPERTY = "filter_type";
    public static String COOLDOWN_PROPERTY = "cooldown";

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            switch(index) {
                case 0:
                    return (locked_minecarts_only?1:0) + ((leave_one_in_stack?1:0) << 1) + (comparator_output.toInt() << 2) + ((redstone_output?1:0) << 4) + (filterType.toInt() << 5);
                case 1:
                    return getIsUnloader()? 1 : 0;
                case 2:
                    return getBlockPos().getX();
                case 3:
                    return getBlockPos().getY();
                case 4:
                    return getBlockPos().getZ();
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
                    filterType = FilterUnloaderTile.FilterType.fromInt((set_to & 96) >> 5);
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public boolean redstone_output;
    public boolean locked_minecarts_only;
    public boolean leave_one_in_stack;
    public MinecartLoaderTile.ComparatorOutputType comparator_output;
    public FilterUnloaderTile.FilterType filterType = FilterUnloaderTile.FilterType.allow_per_slot;

    public boolean last_redstone_output;
    public int comparator_output_value;
    public int cooldown_time;

    public boolean changed_flag;

    LazyOptional<IFluidHandler> fluid_handler = LazyOptional.of(() -> new LoaderTank(FLUID_CAPACITY));
    LazyOptional<IEnergyStorage> energy_handler = LazyOptional.of(() -> new LoaderBattery(ENERGY_CAPACITY));

    public abstract boolean getIsUnloader();

    public AbstractCommonLoader(BlockEntityType<?> p_i48285_1_, BlockPos p_155545_, BlockState p_155546_) {
        super(p_i48285_1_, p_155545_, p_155546_);
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
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean(LOCKED_MINECARTS_ONLY_PROPERTY, locked_minecarts_only);
        compound.putBoolean(LEAVE_ONE_IN_STACK_PROPERTY, leave_one_in_stack);
        compound.putBoolean(REDSTONE_OUTPUT_PROPERTY, redstone_output);
        compound.putInt(COMPARATOR_OUTPUT_PROPERTY, comparator_output.toInt());
        compound.putInt(FILTER_PROPERTY, filterType.toInt());
        compound.putInt(COOLDOWN_PROPERTY, cooldown_time);
        ((FluidTank)fluid_handler.orElse(null)).writeToNBT(compound);
        compound.putInt(ENERGY_PROPERTY, energy_handler.orElse(null).getEnergyStored());
    }

    @Override
    public void load(CompoundTag compound) {
        locked_minecarts_only = compound.getBoolean(LOCKED_MINECARTS_ONLY_PROPERTY);
        leave_one_in_stack = compound.getBoolean(LEAVE_ONE_IN_STACK_PROPERTY);
        redstone_output = compound.getBoolean(REDSTONE_OUTPUT_PROPERTY);
        comparator_output = MinecartLoaderTile.ComparatorOutputType.fromInt( compound.getInt(COMPARATOR_OUTPUT_PROPERTY) );
        if (compound.contains(FILTER_PROPERTY))
            filterType = FilterUnloaderTile.FilterType.fromInt(compound.getInt(FILTER_PROPERTY));
        cooldown_time = compound.getInt(COOLDOWN_PROPERTY);
        comparator_output_value = -1;
        last_redstone_output = !redstone_output;
        FluidTank tank = ((FluidTank)fluid_handler.orElseGet(null));
        tank.setFluid(tank.readFromNBT(compound).getFluid());
        energy_handler.orElse(null).receiveEnergy(compound.getInt(ENERGY_PROPERTY), false);
        super.load(compound);
    }

    protected List<AbstractMinecart> getLoadableMinecartsInRange() {
        if (locked_minecarts_only) {
            List<AbstractMinecart> acc = new ArrayList<AbstractMinecart>();

            BlockEntity te_above = level.getBlockEntity(getBlockPos().above());
            if (te_above instanceof LockingRailTile && ((LockingRailTile)te_above).locked_minecart != null ) acc.add(((LockingRailTile)te_above).locked_minecart);
            BlockEntity te_below = level.getBlockEntity(getBlockPos().below());
            if (te_below instanceof LockingRailTile && ((LockingRailTile)te_below).locked_minecart != null ) acc.add(((LockingRailTile)te_below).locked_minecart);

            return acc;
        }
        else {
            return level.getEntitiesOfClass(AbstractMinecart.class, this.getDectectionBox(), (entity) -> true);
        }
    }

    protected AABB getDectectionBox() {
        BlockPos pos = getBlockPos();
        double d0 = 0.2D;
        return new AABB((double)pos.getX() + 0.2D, (double)pos.getY() - 1, (double)pos.getZ() + 0.2D, (double)(pos.getX() + 1) - 0.2D, (double)(pos.getY() + 2) - 0.2D, (double)(pos.getZ() + 1) - 0.2D);
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

    @Override
    public int[] getSlotsForFace(Direction p_180463_1_) {
        return FilterUnloaderTile.VALID_TAKE_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int p_180462_1_, ItemStack p_180462_2_, @Nullable Direction p_180462_3_) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
        return p_180461_1_ < FilterUnloaderTile.VALID_ITEM_SLOTS;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket(){
        CompoundTag compound = new CompoundTag();
        ((FluidTank)fluid_handler.orElse(null)).writeToNBT(compound);
        compound.putInt(ENERGY_PROPERTY, energy_handler.orElse(null).getEnergyStored());
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compound = new CompoundTag();
        ((FluidTank)fluid_handler.orElse(null)).writeToNBT(compound);
        compound.putInt(ENERGY_PROPERTY, energy_handler.orElse(null).getEnergyStored());
        return compound;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag){
        FluidTank tank = ((FluidTank)fluid_handler.resolve().get());
        tank.setFluid(tank.readFromNBT(tag).getFluid());
        ((SettableEnergyStorage)energy_handler.orElse(null)).setEnergy(tag.getInt(ENERGY_PROPERTY));
    }

    @Override
    public void setChanged() {
        if (level != null && !level.isClientSide) {
            super.setChanged();
            level.markAndNotifyBlock(getBlockPos(), level.getChunkAt(getBlockPos()), getBlockState(), getBlockState(), 2, 0);
        }
    }

    public boolean getOutputsRedstone() {
        return redstone_output;
    }

    public int getSignal() {
        if (comparator_output_value < 0) return 0;
        return comparator_output_value;
    }

    @Override
    public int getSlotCount() {
        return 9;
    }

}
