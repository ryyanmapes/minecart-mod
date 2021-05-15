package com.alc.moreminecarts.tile_entities;

import com.alc.moreminecarts.MMReferences;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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

// todo does this need to tick?
public class LockingRailTile extends TileEntity {
    public static String LOCKED_CART_PROPERTY = "locked_cart";

    protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    @Nullable
    public AbstractMinecartEntity locked_minecart;

    public LockingRailTile() {
        super(MMReferences.locking_rail_te);
        locked_minecart = null;
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putUUID(LOCKED_CART_PROPERTY, locked_minecart.getUUID());
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        if (this.level.isClientSide()) {
            UUID locked_cart_UUID = compound.getUUID(LOCKED_CART_PROPERTY);
            Entity ent = ((ServerWorld)this.level).getEntity(locked_cart_UUID);
            if (ent instanceof AbstractMinecartEntity) {
                lockIn((AbstractMinecartEntity) ent);
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

    private void lockIn(AbstractMinecartEntity cart) {
        locked_minecart = cart;
        locked_minecart.setPos(getBlockPos().getX()+0.5, getBlockPos().getY(), getBlockPos().getZ() + 0.5);
        locked_minecart.setDeltaMovement(0,0,0);
        locked_minecart.setCurrentCartSpeedCapOnRail(0);
    }

    private void lockOut() {
        locked_minecart.setCurrentCartSpeedCapOnRail(locked_minecart.getMaxCartSpeedOnRail());
        locked_minecart = null;
    }

    public void updateLock(boolean locked) {
        if (!locked && locked_minecart != null)  lockOut();
        else if (locked && locked_minecart == null) {
            List<AbstractMinecartEntity> list = findMinecarts(this.level, this.getBlockPos(), AbstractMinecartEntity.class, (cart) -> true);
            if (list.size() > 0) {
                lockIn(list.get(0));
            }
        }
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
}
