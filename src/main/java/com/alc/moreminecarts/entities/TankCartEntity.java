package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMItemReferences;
import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.blocks.PistonDisplayBlock;
import com.alc.moreminecarts.containers.TankCartContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IIntArray;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;


public class TankCartEntity extends AbstractMinecartEntity implements INamedContainerProvider {

    private static final DataParameter<CompoundNBT> FLUID_TAG = EntityDataManager.defineId(FlagCartEntity.class, DataSerializers.COMPOUND_TAG);
    boolean changed_flag;

    public final IIntArray dataAccess = new IIntArray() {
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

    public TankCartEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public TankCartEntity(EntityType<?> type, World worldIn, double x, double y, double z) {
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
    public void destroy(DamageSource source) {
        super.destroy(source);
        if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(MMItemReferences.chunk_loader); // todo change to proper item
        }
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return MMReferences.piston_display_block.defaultBlockState().setValue(PistonDisplayBlock.VARIANT, 4);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public boolean stillValid(PlayerEntity player) {
        return player.distanceToSqr((double)this.position().x + 0.5D, (double)this.position().y + 0.5D, (double)this.position().z + 0.5D) <= 64.0D;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLUID_TAG, new CompoundNBT());
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        ((FluidTank)fluid_handler.orElse(null)).writeToNBT(compound);
    }


    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
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
    public Container createMenu(int i, PlayerInventory inv, PlayerEntity player) {
        return new TankCartContainer(i, level, this, inv, player);
    }

    public ActionResultType interact(PlayerEntity p_184230_1_, Hand p_184230_2_) {
        ActionResultType ret = super.interact(p_184230_1_, p_184230_2_);
        if (ret.consumesAction()) return ret;
        p_184230_1_.openMenu(this);
        return ActionResultType.SUCCESS;
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
        CompoundNBT compound = new CompoundNBT();
        ((FluidTank)fluid_handler.orElse(null)).writeToNBT(compound);
        TankCartEntity.this.entityData.set(FLUID_TAG, compound);
    }

    @Override
    public ItemStack getCartItem() { return new ItemStack(MMItemReferences.tank_cart); }
}
