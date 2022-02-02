package com.alc.moreminecarts.tile_entities;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.CampfireCartEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class LockingRailTile extends BlockEntity {
    public static String LOCKED_CART_PROPERTY = "locked_cart";
    public static String SAVED_FUEL_PROPERTY = "saved_fuel";
    public static String SAVED_PUSH_X_PROPERTY = "saved_push_x";
    public static String SAVED_PUSH_Z_PROPERTY = "saved_push_z";

    private final int FURNACE_CART_MAX_FUEL = 32000;
    protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    public int saved_fuel;
    public double saved_push_x;
    public double saved_push_z;
    public UUID locked_minecart_uuid;

    @Nullable
    public AbstractMinecart locked_minecart;

    public LockingRailTile(BlockPos pos, BlockState state) {
        super(MMReferences.locking_rail_te, pos, state);
        locked_minecart = null;
    }

    public LockingRailTile(BlockPos pos, BlockState state, boolean is_powered) {
        super( is_powered? MMReferences.powered_locking_rail_te : MMReferences.locking_rail_te, pos, state );
        locked_minecart = null;
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        if (locked_minecart != null) compound.putUUID(LOCKED_CART_PROPERTY, locked_minecart.getUUID());
        compound.putInt(SAVED_FUEL_PROPERTY, saved_fuel);
        compound.putDouble(SAVED_PUSH_X_PROPERTY, saved_push_x);
        compound.putDouble(SAVED_PUSH_Z_PROPERTY, saved_push_z);
    }

    @Override
    public void load(CompoundTag compound) {
        try {
            locked_minecart_uuid = compound.getUUID(LOCKED_CART_PROPERTY);
            saved_fuel = compound.getInt(SAVED_FUEL_PROPERTY);
            saved_push_x = compound.getInt(SAVED_PUSH_X_PROPERTY);
            saved_push_z = compound.getInt(SAVED_PUSH_Z_PROPERTY);
        } catch (NullPointerException e) {
            locked_minecart = null;
            saved_fuel = 0;
            saved_push_x = 0;
            saved_push_z = 0;
        }

        super.load(compound);
    }

    @Override
    public void setRemoved() {
        if (!level.isClientSide) {
            lockOut();
        }
        super.setRemoved();
    }

    protected void lockIn(AbstractMinecart cart) {
        if (locked_minecart == cart) return;

        level.playLocalSound(getBlockPos().getX()+0.5, getBlockPos().getY(), getBlockPos().getZ()+0.5, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 0.5f, 1f, false);

        locked_minecart = cart;
        locked_minecart.setPos(getBlockPos().getX()+0.5, getBlockPos().getY(), getBlockPos().getZ() + 0.5);
        locked_minecart.setDeltaMovement(0,0,0);
        if (locked_minecart instanceof MinecartFurnace) {
            saved_fuel = ((MinecartFurnace)locked_minecart).fuel;
            ((MinecartFurnace)locked_minecart).fuel = 0;
            saved_push_x = ((MinecartFurnace)locked_minecart).xPush;
            saved_push_z = ((MinecartFurnace)locked_minecart).zPush;
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

        level.playLocalSound(getBlockPos().getX()+0.5, getBlockPos().getY(), getBlockPos().getZ()+0.5, SoundEvents.CHAIN_BREAK, SoundSource.BLOCKS, 0, 0.5f, false);

        if (locked_minecart instanceof MinecartFurnace) {
            MinecartFurnace furnace_minecart = ((MinecartFurnace)locked_minecart);
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
            List<AbstractMinecart> list = findMinecarts(this.level, this.getBlockPos(), AbstractMinecart.class, (cart) -> true);
            if (list.size() > 0) {
                lockIn(list.get(0));
                return true;
            }
        }
        return false;
    }

    protected <T extends AbstractMinecart> List<T> findMinecarts(Level worldIn, BlockPos pos, Class<T> cartType, @Nullable Predicate<Entity> filter) {
        return worldIn.getEntitiesOfClass(cartType, this.getDectectionBox(pos), filter);
    }

    private AABB getDectectionBox(BlockPos pos) {
        double d0 = 0.2D;
        return new AABB((double)pos.getX() + 0.2D, (double)pos.getY(), (double)pos.getZ() + 0.2D, (double)(pos.getX() + 1) - 0.2D, (double)(pos.getY() + 1) - 0.2D, (double)(pos.getZ() + 1) - 0.2D);
    }


    public int getComparatorSignal() {
        return locked_minecart == null ? 0 : 15;
    }

    public static void doTick(Level level, BlockPos pos, BlockState state, LockingRailTile ent) {

        if (level.isClientSide) return;

        if (ent.locked_minecart_uuid != null) {
            Entity locked_entity = ((ServerLevel)level).getEntity(ent.locked_minecart_uuid);
            if (locked_entity instanceof AbstractMinecart) ent.locked_minecart = (AbstractMinecart) locked_entity;
            else {
                ent.saved_fuel = 0;
                ent.saved_push_x = 0;
                ent.saved_push_z = 0;
            }
            ent.locked_minecart_uuid = null;
        }

        if (ent.locked_minecart != null) {
            if (ent.locked_minecart.isAlive()) {
                ent.locked_minecart.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                ent.locked_minecart.setDeltaMovement(0, 0, 0);
            } else {
                ent.locked_minecart = null;
                level.updateNeighbourForOutputSignal(pos, state.getBlock());
            }
        }
    }
}
