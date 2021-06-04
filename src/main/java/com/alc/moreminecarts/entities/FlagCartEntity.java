package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.blocks.FlagDisplayBlock;
import com.alc.moreminecarts.blocks.utility_rails.ArithmeticRailBlock;
import com.alc.moreminecarts.containers.FlagCartContainer;
import com.alc.moreminecarts.misc.FlagUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIntArray;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;


public class FlagCartEntity extends ContainerMinecartEntity {
    public static String SELECTED_SLOT_PROPERTY = "selected_slot";
    public static String DISCLUDED_SLOTS_PROPERTY = "discluded_slots";

    private static final DataParameter<Integer> DISPLAY_TYPE = EntityDataManager.defineId(FlagCartEntity.class, DataSerializers.INT);

    public FlagCartEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public FlagCartEntity(EntityType<?> type, World worldIn, double x, double y, double z) {
        super(type, x, y, z, worldIn);
    }

    @Override
    public Type getMinecartType() {
        return Type.CHEST;
    }

    @Override
    public void destroy(DamageSource source) {
        super.destroy(source);
        if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(Items.LOOM);
            this.spawnAtLocation(Items.COMPARATOR);
        }
    }

    @Override
    protected Container createMenu(int i, PlayerInventory inv) {
        return new FlagCartContainer(i, level, this, inv, inv.player);
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        int raw_display = getDisplayType();
        int main_variant = raw_display & 31;
        int next_variant = (raw_display & 992) >> 5;
        int last_variant = (raw_display & 31744) >> 10;
        return MMReferences.flag_display_block.defaultBlockState()
                .setValue(FlagDisplayBlock.MAIN, main_variant)
                .setValue(FlagDisplayBlock.NEXT, next_variant)
                .setValue(FlagDisplayBlock.LAST, last_variant);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void activateMinecart(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
        if (!level.isClientSide) selected_slot = 0;
        updateDisplayType();
    }

    // Container stuff

    public int getContainerSize() {
        return 9;
    }

    public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
        ItemStack ret = super.removeItem(p_70298_1_, p_70298_2_);
        updateDisplayType();
        return ret;
    }

    public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
        super.setItem(p_70299_1_, p_70299_2_);
        updateDisplayType();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return player.distanceToSqr((double)this.position().x + 0.5D, (double)this.position().y + 0.5D, (double)this.position().z + 0.5D) <= 64.0D;
    }

    public int selected_slot = 0;
    public int discluded_slots = 0;

    public BlockPos old_block_pos;

    // See ChunkLoaderBlock for an explanation of this monstrosity.
    public final IIntArray dataAccess = new IIntArray() {
        @Override
        public int get(int index) {
            switch(index) {
                case 0:
                    return selected_slot;
                case 1:
                    return discluded_slots;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int set_to) {
            switch(index) {
                case 0:
                    selected_slot = (byte)set_to;
                    break;
                case 1:
                    discluded_slots = (byte)set_to;
                    selected_slot = (byte)Math.min(8-discluded_slots, selected_slot);
                    break;
                default:
                    break;
            }
            updateDisplayType();
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(SELECTED_SLOT_PROPERTY, this.selected_slot);
        compound.putInt(DISCLUDED_SLOTS_PROPERTY, this.discluded_slots);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.selected_slot = compound.getInt(SELECTED_SLOT_PROPERTY);
        this.discluded_slots = compound.getInt(DISCLUDED_SLOTS_PROPERTY);
        updateDisplayType();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DISPLAY_TYPE, 0);
    }

    public Item getSelectedFlag() {
        return getItem(selected_slot).getItem();
    }

    public void cycleFlag(boolean is_minus) {
        selected_slot = FlagUtil.getNextSelectedSlot(selected_slot, discluded_slots, is_minus);

        level.playLocalSound(getX(), getY(), getZ(), SoundEvents.ITEM_FRAME_PLACE, SoundCategory.BLOCKS, 0.5f, 1f, false);
        updateDisplayType();
    }

    public int getDisplayType() {
        return entityData.get(DISPLAY_TYPE);
    }

    public void updateDisplayType() {
        if (!level.isClientSide) {
            Item this_item = getItem(selected_slot).getItem();
            Item next_item = getItem( FlagUtil.getNextSelectedSlot(selected_slot, discluded_slots, false) ).getItem();
            Item last_item = getItem( FlagUtil.getNextSelectedSlot(selected_slot, discluded_slots, true) ).getItem();
            int full_display = 0;
            full_display += FlagUtil.getFlagColorValue( this_item );
            full_display += FlagUtil.getFlagColorValue( next_item ) << 5;
            full_display += FlagUtil.getFlagColorValue( last_item ) << 10;
            entityData.set(DISPLAY_TYPE, full_display);
        }
    }

    @Override
    public void tick() {
        if (old_block_pos == null) old_block_pos = blockPosition();

        super.tick();

        if (!level.isClientSide) {
            BlockPos new_block_pos = blockPosition();
            if (!old_block_pos.equals(new_block_pos)) {
                BlockState new_blockstate = level.getBlockState(new_block_pos);
                if (new_blockstate.getBlock() == MMReferences.arithmetic_rail && new_blockstate.getValue(ArithmeticRailBlock.POWERED)) {
                    cycleFlag(new_blockstate.getValue(ArithmeticRailBlock.INVERTED));
                }
                old_block_pos = new_block_pos;
            }
        }
    }

    /*
    public int getComparatorSignal() {
        byte acc = 0;

        for (int i = 0; i < 8-discluded_slots; i++) {
            if (!getItem(i).isEmpty()) acc += 1;
        }

        return (int) Math.floor(((float)acc/discluded_slots) * 15);
    }
    Probably not necessary
    */
}
