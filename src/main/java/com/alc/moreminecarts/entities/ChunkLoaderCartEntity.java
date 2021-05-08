package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MoreMinecartsConstants;
import com.alc.moreminecarts.MoreMinecartsMod;
import com.alc.moreminecarts.blocks.ChunkLoaderBlock;
import com.alc.moreminecarts.containers.ChunkLoaderContainer;
import com.alc.moreminecarts.tile_entities.ChunkLoaderTile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIntArray;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;


// Pushcarts can't have entities besides players on them, so we always return canBeRidden as false,
// but we force it if it's a player
@ObjectHolder("moreminecarts")
public class ChunkLoaderCartEntity extends ContainerMinecartEntity {
    private static final DataParameter<Boolean> POWERED = EntityDataManager.defineId(ChunkLoaderCartEntity.class, DataSerializers.BOOLEAN);

    public static final Item chunk_loader = null;

    public ChunkLoaderCartEntity(EntityType<?> type, World world) {
        super(type, world);
        lit_last_tick = false;
        time_left = -1;
        last_chunk_x = getOnPos().getX() >> 4;
        last_chunk_z = getOnPos().getZ() >> 4;
    }

    public ChunkLoaderCartEntity(EntityType<?> type, World worldIn, double x, double y, double z) {
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
            this.spawnAtLocation(chunk_loader);
            ChunkLoaderTile.dropExtras(level, time_left, getOnPos());
        }
        onRemoval();

    }

    @Override
    public void remove(boolean keepData) {
        super.remove(keepData);
        onRemoval();
    }

    @Override
    protected Container createMenu(int i, PlayerInventory inv) {
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
        return MoreMinecartsMod.chunk_loader.defaultBlockState().setValue(ChunkLoaderBlock.POWERED, Boolean.valueOf(isMinecartPowered()));
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return input_itemstack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return input_itemstack;
    }

    @Override
    public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
        return input_itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return input_itemstack;
    }

    @Override
    public void setItem(int slot, ItemStack to_set) {
        input_itemstack = to_set;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return player.distanceToSqr((double)this.position().x + 0.5D, (double)this.position().y + 0.5D, (double)this.position().z + 0.5D) <= 64.0D;
    }

    // Mostly copied from ChunkLoaderBlock

    protected ItemStack input_itemstack = ItemStack.EMPTY;
    public final IIntArray dataAccess = new IIntArray() {
        @Override
        public int get(int index) {
            switch(index) {
                case 0:
                    return time_left;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int set_to) {
            switch(index) {
                case 0:
                    time_left = set_to;
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public boolean lit_last_tick;
    // This should never be zero. 1 is the minimum value here, for reasons.
    public int time_left;
    public int last_chunk_x;
    public int last_chunk_z;

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(ChunkLoaderTile.TIME_LEFT_PROPERTY, this.time_left);
        compound.putInt(ChunkLoaderTile.LAST_CHUNK_X_PROPERTY, this.last_chunk_x);
        compound.putInt(ChunkLoaderTile.LAST_CHUNK_Z_PROPERTY, this.last_chunk_z);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
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

        if (isLit()) time_left--;

        if (!level.isClientSide) {

            int burn_duration = ChunkLoaderTile.getBurnDuration(input_itemstack.getItem());
            if (burn_duration >= 0 && Math.abs(time_left) + burn_duration <= ChunkLoaderTile.MAX_TIME_LEFT) {

                if (time_left > 0) time_left += burn_duration;
                else time_left -= burn_duration;

                input_itemstack.shrink(1);
            }

            int chunk_x = getOnPos().getX() >> 4;
            int chunk_z = getOnPos().getZ() >> 4;

            if (chunk_x != last_chunk_x || chunk_z != last_chunk_z) {
                forceChucksAt(last_chunk_x, last_chunk_z, false);

                last_chunk_x = chunk_x;
                last_chunk_z = chunk_z;

                lit_last_tick = !isLit();
            }

            if (lit_last_tick != isLit()) {
                if (isLit()) {
                    forceChucksAt(chunk_x, chunk_z, true);
                }
                else {
                    forceChucksAt(chunk_x, chunk_z, false);
                }
                setMinecartPowered(isLit());
            }

            this.setChanged();
        }

        lit_last_tick = isLit();
    }

    private void forceChucksAt(int chunk_x, int chunk_z, boolean add) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                ForgeChunkManager.forceChunk((ServerWorld) level, MoreMinecartsConstants.modid, this, chunk_x + i, chunk_z + j, add, true);
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



}
