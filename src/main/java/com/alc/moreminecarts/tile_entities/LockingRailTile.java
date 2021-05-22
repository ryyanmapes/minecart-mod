package com.alc.moreminecarts.tile_entities;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.CampfireCartEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.FurnaceMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class LockingRailTile extends TileEntity implements ITickableTileEntity {
    public static String LOCKED_CART_PROPERTY = "locked_cart";
    public static String SAVED_FUEL_PROPERTY = "saved_fuel";
    public static String SAVED_PUSH_X_PROPERTY = "saved_push_x";
    public static String SAVED_PUSH_Z_PROPERTY = "saved_push_z";

    private final int FURNACE_CART_MAX_FUEL = 32000;
    protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    public int saved_fuel;
    public double saved_push_x;
    public double saved_push_z;

    @Nullable
    public AbstractMinecartEntity locked_minecart;

    public LockingRailTile() {
        super(MMReferences.locking_rail_te);
        locked_minecart = null;
    }

    public LockingRailTile(boolean is_powered) {
        super( is_powered? MMReferences.powered_locking_rail_te : MMReferences.locking_rail_te );
        locked_minecart = null;
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putUUID(LOCKED_CART_PROPERTY, locked_minecart.getUUID());
        compound.putInt(SAVED_FUEL_PROPERTY, saved_fuel);
        compound.putDouble(SAVED_PUSH_X_PROPERTY, saved_push_x);
        compound.putDouble(SAVED_PUSH_Z_PROPERTY, saved_push_z);
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        if (!level.isClientSide) {
            UUID locked_cart_UUID = compound.getUUID(LOCKED_CART_PROPERTY);
            Entity ent = ((ServerWorld)this.level).getEntity(locked_cart_UUID);
            if (ent instanceof AbstractMinecartEntity) {
                locked_minecart = (AbstractMinecartEntity) ent;
                saved_fuel = compound.getInt(SAVED_FUEL_PROPERTY);
                saved_push_x = compound.getInt(SAVED_PUSH_X_PROPERTY);
                saved_push_z = compound.getInt(SAVED_PUSH_Z_PROPERTY);
            }
        }
        super.load(state, compound);
    }

    @Override
    public void setRemoved() {
        if (!level.isClientSide) {
            lockOut();
        }
        super.setRemoved();
    }

    protected void lockIn(AbstractMinecartEntity cart) {
        if (locked_minecart == cart) return;

        locked_minecart = cart;
        locked_minecart.setPos(getBlockPos().getX()+0.5, getBlockPos().getY(), getBlockPos().getZ() + 0.5);
        locked_minecart.setDeltaMovement(0,0,0);
        if (locked_minecart instanceof FurnaceMinecartEntity) {
            saved_fuel = ((FurnaceMinecartEntity)locked_minecart).fuel;
            ((FurnaceMinecartEntity)locked_minecart).fuel = 0;
            saved_push_x = ((FurnaceMinecartEntity)locked_minecart).xPush;
            saved_push_z = ((FurnaceMinecartEntity)locked_minecart).zPush;
        }
        if (locked_minecart instanceof CampfireCartEntity) {
            saved_fuel = ((CampfireCartEntity) locked_minecart).isMinecartPowered() ? 1 : 0;
            ((CampfireCartEntity) locked_minecart).setMinecartPowered(false);
            saved_push_x = ((CampfireCartEntity) locked_minecart).pushX;
            saved_push_z = ((CampfireCartEntity) locked_minecart).pushZ;
        }
    }

    protected void lockOut() {
        if (locked_minecart == null) return;

        if (locked_minecart instanceof FurnaceMinecartEntity) {
            FurnaceMinecartEntity furnace_minecart = ((FurnaceMinecartEntity)locked_minecart);
            int fuel = furnace_minecart.fuel;
            fuel += saved_fuel;
            if (fuel > FURNACE_CART_MAX_FUEL) fuel = FURNACE_CART_MAX_FUEL;
            furnace_minecart.fuel = fuel;
            furnace_minecart.xPush = saved_push_x;
            furnace_minecart.zPush = saved_push_z;
        }
        if (locked_minecart instanceof CampfireCartEntity) {
            ((CampfireCartEntity)locked_minecart).setMinecartPowered(saved_fuel >= 1);
            ((CampfireCartEntity)locked_minecart).pushX = saved_push_x;
            ((CampfireCartEntity)locked_minecart).pushZ = saved_push_z;
        }
        locked_minecart = null;
    }

    // Returns true if there is a comparator signal change.
    public boolean updateLock(boolean locked) {
        if (locked_minecart != null && (!locked || !locked_minecart.isAlive()))  {
            lockOut();
            return true;
        }
        else if (locked && locked_minecart == null) {
            List<AbstractMinecartEntity> list = findMinecarts(this.level, this.getBlockPos(), AbstractMinecartEntity.class, (cart) -> true);
            if (list.size() > 0) {
                lockIn(list.get(0));
                return true;
            }
        }
        return false;
    }

    protected <T extends AbstractMinecartEntity> List<T> findMinecarts(World worldIn, BlockPos pos, Class<T> cartType, @Nullable Predicate<Entity> filter) {
        return worldIn.getEntitiesOfClass(cartType, this.getDectectionBox(pos), filter);
    }

    private AxisAlignedBB getDectectionBox(BlockPos pos) {
        double d0 = 0.2D;
        return new AxisAlignedBB((double)pos.getX() + 0.2D, (double)pos.getY(), (double)pos.getZ() + 0.2D, (double)(pos.getX() + 1) - 0.2D, (double)(pos.getY() + 1) - 0.2D, (double)(pos.getZ() + 1) - 0.2D);
    }


    public int getComparatorSignal() {
        return locked_minecart == null ? 0 : 15;
    }

    @Override
    public void tick() {
        if (locked_minecart != null && !level.isClientSide) {
            if (locked_minecart.isAlive()) {
                locked_minecart.setPos(getBlockPos().getX() + 0.5, getBlockPos().getY(), getBlockPos().getZ() + 0.5);
                locked_minecart.setDeltaMovement(0, 0, 0);
            } else {
                locked_minecart = null;
                level.updateNeighbourForOutputSignal(getBlockPos(), this.getBlockState().getBlock());
            }
        }
    }
}
