package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.blocks.containers.ChunkLoaderBlock;
import com.alc.moreminecarts.containers.ChunkLoaderContainer;
import com.alc.moreminecarts.registry.MMBlocks;
import com.alc.moreminecarts.registry.MMItems;
import com.alc.moreminecarts.tile_entities.ChunkLoaderTile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.network.NetworkHooks;


public class ChunkLoaderCartEntity extends AbstractMinecartContainer {
    private static final EntityDataAccessor<Boolean> POWERED = SynchedEntityData.defineId(ChunkLoaderCartEntity.class, EntityDataSerializers.BOOLEAN);

    public ChunkLoaderCartEntity(EntityType<?> type, Level world) {
        super(type, world);
        lit_last_tick = false;
        time_left = -1;
        last_chunk_x = getOnPos().getX() >> 4;
        last_chunk_z = getOnPos().getZ() >> 4;
    }

    public ChunkLoaderCartEntity(EntityType<?> type, Level worldIn, double x, double y, double z) {
        super(type, x, y, z, worldIn);
        lit_last_tick = false;
        time_left = -1;
        last_chunk_x = getOnPos().getX() >> 4;
        last_chunk_z = getOnPos().getZ() >> 4;
    }

    @Override
    public Type getMinecartType() {
        return Type.CHEST;
    }

    @Override
    public void destroy(DamageSource source) {
        super.destroy(source);
        if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ChunkLoaderTile.dropExtras(level, time_left, getOnPos());
        }
        onRemoval();

    }

    @Override
    protected Item getDropItem() {
        return MMItems.MINECART_WITH_CHUNK_LOADER_ITEM.get();
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        onRemoval();
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inv) {
        return new ChunkLoaderContainer(i, level, this, dataAccess, inv, inv.player);
    }

    protected boolean isMinecartPowered() {
        return this.entityData.get(POWERED);
    }

    protected void setMinecartPowered(boolean powered) {
        this.entityData.set(POWERED, powered);
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return MMBlocks.CHUNK_LOADER_BLOCK.get().defaultBlockState().setValue(ChunkLoaderBlock.POWERED, Boolean.valueOf(isMinecartPowered()));
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void activateMinecart(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
        setEnabled(p_96095_4_);
    }

    // Container stuff

    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr((double)this.position().x + 0.5D, (double)this.position().y + 0.5D, (double)this.position().z + 0.5D) <= 64.0D;
    }

    // Mostly copied from ChunkLoaderBlock

    // See ChunkLoaderBlock for an explanation of this monstrosity.
    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            switch(index) {
                case 0:
                    return (int)Math.ceil( (Math.abs(ChunkLoaderCartEntity.this.time_left) - 1) / 1200.0) * get(1);
                case 1:
                    return ChunkLoaderCartEntity.this.time_left > 0? 1 : -1;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int set_to) {
            switch(index) {
                case 0:
                    ChunkLoaderCartEntity.this.time_left = set_to * 1200;
                    break;
                case 1:
                    ChunkLoaderCartEntity.this.time_left = Math.abs(ChunkLoaderCartEntity.this.time_left) * (set_to > 0? 1 : -1);
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

    public boolean lit_last_tick;
    // This should never be zero. 1 is the minimum value here, for reasons.
    public int time_left;
    public int last_chunk_x;
    public int last_chunk_z;

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(ChunkLoaderTile.TIME_LEFT_PROPERTY, this.time_left);
        compound.putInt(ChunkLoaderTile.LAST_CHUNK_X_PROPERTY, this.last_chunk_x);
        compound.putInt(ChunkLoaderTile.LAST_CHUNK_Z_PROPERTY, this.last_chunk_z);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.time_left = compound.getInt(ChunkLoaderTile.TIME_LEFT_PROPERTY);
        this.last_chunk_x = compound.getInt(ChunkLoaderTile.LAST_CHUNK_X_PROPERTY);
        this.last_chunk_z = compound.getInt(ChunkLoaderTile.LAST_CHUNK_Z_PROPERTY);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(POWERED, false);
    }

    @Override
    public void tick() {
        super.tick();

        boolean changed_flag = false;
        if (isLit()) time_left--;

        if (!level.isClientSide()) {

            int burn_duration = ChunkLoaderTile.getBurnDuration(itemStacks.get(0).getItem());
            if (burn_duration >= 0 && Math.abs(time_left) + burn_duration <= ChunkLoaderTile.MAX_TIME) {
                changed_flag = true;

                if (time_left > 0) time_left += burn_duration;
                else time_left -= burn_duration;

                itemStacks.get(0).shrink(1);
            }

            int chunk_x = getOnPos().getX() >> 4;
            int chunk_z = getOnPos().getZ() >> 4;

            if (chunk_x != last_chunk_x || chunk_z != last_chunk_z) {
                changed_flag = true;

                forceChucksAt(last_chunk_x, last_chunk_z, false);

                last_chunk_x = chunk_x;
                last_chunk_z = chunk_z;

                lit_last_tick = !isLit();
            }

            if (lit_last_tick != isLit()) {
                changed_flag = true;

                if (isLit()) {
                    forceChucksAt(chunk_x, chunk_z, true);
                }
                else {
                    forceChucksAt(chunk_x, chunk_z, false);
                }

                setMinecartPowered(isLit());
            }

            if (changed_flag) this.setChanged();
        }

        lit_last_tick = isLit();
    }

    private void forceChucksAt(int chunk_x, int chunk_z, boolean add) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                ForgeChunkManager.forceChunk((ServerLevel) level, MMConstants.modid, this, chunk_x + i, chunk_z + j, add, true);
            }
        }
    }

    public void onRemoval() {
        if (!level.isClientSide) {

            int chunk_x = getOnPos().getX() >> 4;
            int chunk_z = getOnPos().getZ() >> 4;

            forceChucksAt(chunk_x, chunk_z, false);
        }
    }

    public boolean isLit() {
        return time_left > 1 && isEnabled();
    }

    public boolean isEnabled() {
        return time_left > 0;
    }

    public void setEnabled(boolean enabled) {
        time_left = Math.abs(time_left) * (enabled? 1 : -1);
    }

    public int getComparatorSignal() {
        float true_time_left = Math.abs(time_left) - 1;
        double log_proportion = Math.log10( ((true_time_left/ChunkLoaderTile.MAX_TIME)*9 + 1 ));
        return (int)Math.ceil(log_proportion * 15);
    }

    //@Override
    //public ItemStack getCartItem() { return new ItemStack(MMItemReferences.chunk_loader_cart); }

}
