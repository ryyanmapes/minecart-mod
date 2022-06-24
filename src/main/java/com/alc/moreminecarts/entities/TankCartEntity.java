package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.blocks.PistonDisplayBlock;
import com.alc.moreminecarts.containers.TankCartContainer;
import com.alc.moreminecarts.registry.MMBlocks;
import com.alc.moreminecarts.registry.MMItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;


public class TankCartEntity extends AbstractMinecart implements Container, MenuProvider {

    private static final EntityDataAccessor<CompoundTag> FLUID_TAG = SynchedEntityData.defineId(FlagCartEntity.class, EntityDataSerializers.COMPOUND_TAG);
    boolean changed_flag;

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

    public class CartTank extends FluidTank {
        public CartTank(int capacity) {
            super(capacity);
        }

        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            if (level != null && !TankCartEntity.this.level.isClientSide) {
                updateSynchedData();
            }
        }
    }

    public TankCartEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public TankCartEntity(EntityType<?> type, Level worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    LazyOptional<IFluidHandler> fluid_handler = LazyOptional.of(() -> new CartTank(40000));

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluid_handler.cast();
        }
        return super.getCapability(cap);
    }

    @Override
    public Type getMinecartType() {
        return Type.CHEST;
    }

    @Override
    protected Item getDropItem() {
        return MMItems.TANK_CART_ITEM.get();
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return MMBlocks.PISTON_DISPLAY_BLOCK.get().defaultBlockState().setValue(PistonDisplayBlock.VARIANT, 4);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public boolean stillValid(Player player) {
        return player.distanceToSqr((double)this.position().x + 0.5D, (double)this.position().y + 0.5D, (double)this.position().z + 0.5D) <= 64.0D;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLUID_TAG, new CompoundTag());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        ((FluidTank)fluid_handler.orElse(null)).writeToNBT(compound);
    }


    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        FluidTank tank = ((FluidTank)fluid_handler.resolve().get());
        tank.setFluid(tank.readFromNBT(compound).getFluid());
        changed_flag = true;
    }

    public FluidStack getFluidStack() {
        if (!level.isClientSide) return fluid_handler.orElse(null).getFluidInTank(0);
        return ((FluidTank)fluid_handler.orElse(null)).readFromNBT(entityData.get(FLUID_TAG)).getFluid();
    }

    public int getComparatorSignal() {
        return (int)Math.floor((float)((FluidTank)fluid_handler.resolve().get()).getFluidAmount() / ((FluidTank)fluid_handler.resolve().get()).getCapacity() * 15.0);
    }

    // Container stuff

    @Nullable
    public AbstractContainerMenu createMenu(int i, Inventory inv, Player player) {
        return new TankCartContainer(i, level, this, inv, player);
    }

    public InteractionResult interact(Player p_184230_1_, InteractionHand p_184230_2_) {
        InteractionResult ret = super.interact(p_184230_1_, p_184230_2_);
        if (ret.consumesAction()) return ret;
        p_184230_1_.openMenu(this);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick() {
        super.tick();
        if (changed_flag && !level.isClientSide) {
            updateSynchedData();
            changed_flag = false;
        }

    }

    protected void updateSynchedData() {
        CompoundTag compound = new CompoundTag();
        ((FluidTank)fluid_handler.orElse(null)).writeToNBT(compound);
        TankCartEntity.this.entityData.set(FLUID_TAG, compound);
    }

    //@Override
    //public ItemStack getCartItem() { return new ItemStack(MMItemReferences.tank_cart); }

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
