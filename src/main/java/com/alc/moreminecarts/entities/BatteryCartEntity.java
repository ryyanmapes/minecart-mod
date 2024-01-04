package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.blocks.PistonDisplayBlock;
import com.alc.moreminecarts.containers.BatteryCartContainer;
import com.alc.moreminecarts.misc.SettableEnergyStorage;
import com.alc.moreminecarts.registry.MMBlocks;
import com.alc.moreminecarts.registry.MMItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;


public class BatteryCartEntity extends AbstractMinecart implements Container, MenuProvider {

    public static String ENERGY_PROPERTY = "energy";
    private static final EntityDataAccessor<Integer> ENERGY_AMOUNT = SynchedEntityData.defineId(BatteryCartEntity.class, EntityDataSerializers.INT);

    public class CartBattery extends SettableEnergyStorage {
        public CartBattery(int capacity) {
            super(capacity);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int ret = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && level() != null) BatteryCartEntity.this.entityData.set(ENERGY_AMOUNT, energy);
            return ret;
        }
        @Override
        public int extractEnergy(int maxReceive, boolean simulate) {
            int ret = super.extractEnergy(maxReceive, simulate);
            if (!simulate && level() != null) BatteryCartEntity.this.entityData.set(ENERGY_AMOUNT, energy);
            return ret;
        }
    }

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            switch(index) {
                case 0:
                    return getId();
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int set_to) {
            return;
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public BatteryCartEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public BatteryCartEntity(EntityType<?> type, Level worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    LazyOptional<IEnergyStorage> energy_handler = LazyOptional.of(() -> new CartBattery(40000));

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energy_handler.cast();
        }
        return super.getCapability(cap);
    }

    @Override
    public Type getMinecartType() {
        return Type.CHEST;
    }

    public Item getDropItem() {
        return MMItems.BATTERY_CART_ITEM.get();
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return MMBlocks.PISTON_DISPLAY_BLOCK.get().defaultBlockState().setValue(PistonDisplayBlock.VARIANT, 5);
    }

    // Container stuff

    @Nullable
    public AbstractContainerMenu createMenu(int i, Inventory inv, Player player) {
        return new BatteryCartContainer(i, level(), this, inv, player);
    }

    public InteractionResult interact(Player p_184230_1_, InteractionHand p_184230_2_) {
        InteractionResult ret = super.interact(p_184230_1_, p_184230_2_);
        if (ret.consumesAction()) return ret;
        p_184230_1_.openMenu(this);
        return InteractionResult.SUCCESS;
    }

    //@Override
    //public ItemStack getCartItem() { return new ItemStack(MMItemReferences.battery_cart); }


    public boolean stillValid(Player player) {
        return player.distanceToSqr((double)this.position().x + 0.5D, (double)this.position().y + 0.5D, (double)this.position().z + 0.5D) <= 64.0D;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ENERGY_AMOUNT, 0);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(ENERGY_PROPERTY, energy_handler.orElse(null).getEnergyStored());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        energy_handler.orElse(null).receiveEnergy( compound.getInt(ENERGY_PROPERTY), false );
    }

    public int getEnergyAmount() {
        return entityData.get(ENERGY_AMOUNT);
    }

    public int getComparatorSignal() {
        return (int)Math.floor((float)energy_handler.resolve().get().getEnergyStored() / energy_handler.resolve().get().getMaxEnergyStored() * 15.0);
    }

    // More Container stuff

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int p_18941_) {
        return null;
    }

    @Override
    public ItemStack removeItem(int p_18942_, int p_18943_) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_18951_) {
        return null;
    }

    @Override
    public void setItem(int p_18944_, ItemStack p_18945_) {

    }

    @Override
    public void setChanged() {

    }

    @Override
    public void clearContent() {

    }

}
