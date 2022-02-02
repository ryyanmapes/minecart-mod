package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMItemReferences;
import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.blocks.PistonDisplayBlock;
import com.alc.moreminecarts.blocks.utility_rails.ArithmeticRailBlock;
import com.alc.moreminecarts.containers.FlagCartContainer;
import com.alc.moreminecarts.misc.FlagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;


public class FlagCartEntity extends AbstractMinecartContainer {
    public static String SELECTED_SLOT_PROPERTY = "selected_slot";
    public static String DISCLUDED_SLOTS_PROPERTY = "discluded_slots";

    private static final EntityDataAccessor<Integer> DISPLAY_TYPE = SynchedEntityData.defineId(FlagCartEntity.class, EntityDataSerializers.INT);

    public FlagCartEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public FlagCartEntity(EntityType<?> type, Level worldIn, double x, double y, double z) {
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
    protected AbstractContainerMenu createMenu(int i, Inventory inv) {
        return new FlagCartContainer(i, level, this, inv, inv.player);
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        int raw_display = 6 + getDisplayType();
        return MMReferences.piston_display_block.defaultBlockState().setValue(PistonDisplayBlock.VARIANT, raw_display);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
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
    public boolean stillValid(Player player) {
        return player.distanceToSqr((double)this.position().x + 0.5D, (double)this.position().y + 0.5D, (double)this.position().z + 0.5D) <= 64.0D;
    }

    public int selected_slot = 0;
    public int discluded_slots = 0;

    public BlockPos old_block_pos;

    public final ContainerData dataAccess = new ContainerData() {
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
                    selected_slot = set_to;
                    break;
                case 1:
                    discluded_slots = set_to;
                    selected_slot = Math.min(8-discluded_slots, selected_slot);
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
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(SELECTED_SLOT_PROPERTY, this.selected_slot);
        compound.putInt(DISCLUDED_SLOTS_PROPERTY, this.discluded_slots);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
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

    public void cycleFlag(ArithmeticRailBlock.SignalEffect effect) {
        boolean is_decrement = effect.isNegative();

        if (effect.isShift()) selected_slot = FlagUtil.getNextSelectedSlot(selected_slot, discluded_slots, is_decrement);
        else {
            if (!is_decrement && discluded_slots >= 8) return;
            else if (is_decrement && discluded_slots <= 0) return;
            discluded_slots += is_decrement ? -1 : 1;
            selected_slot = Math.min(8-discluded_slots, selected_slot);
        }

        level.playLocalSound(getX(), getY(), getZ(), SoundEvents.ITEM_FRAME_PLACE, SoundSource.BLOCKS, 0.5f, 1f, true);
        updateDisplayType();
    }

    public int getDisplayType() {
        return entityData.get(DISPLAY_TYPE);
    }

    public void updateDisplayType() {
        if (!level.isClientSide) {
            Item this_item = getItem(selected_slot).getItem();
            int full_display = 0;
            full_display += FlagUtil.getFlagColorValue( this_item );
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
                    cycleFlag((ArithmeticRailBlock.SignalEffect) new_blockstate.getValue(ArithmeticRailBlock.EFFECT));
                }
                old_block_pos = new_block_pos;
            }
        }
    }

    @Override
    public ItemStack getCartItem() { return new ItemStack(MMItemReferences.flag_cart); }

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
