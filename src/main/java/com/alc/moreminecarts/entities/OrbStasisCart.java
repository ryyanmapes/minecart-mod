package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.blocks.OrbStasisBlock;
import com.alc.moreminecarts.registry.MMBlocks;
import com.alc.moreminecarts.registry.MMItems;
import com.alc.moreminecarts.tile_entities.OrbStasisTile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;


// Pushcarts can't have entities besides players on them, so we always return canBeRidden as false,
// but we force it if it's a player
public class OrbStasisCart extends AbstractMinecart {

    private static final EntityDataAccessor<Boolean> HAS_ORB = SynchedEntityData.defineId(OrbStasisCart.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    public UUID owner_uuid;

    public OrbStasisCart(EntityType<?> type, Level world) {
        super(type, world);
    }

    public OrbStasisCart(EntityType<?> type, Level worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    @Override
    public Type getMinecartType() {
        return Type.CHEST;
    }

    @Override
    protected Item getDropItem() {
        return MMItems.MINECART_WITH_STASIS_ITEM.get();
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        attemptTeleport();
    }

    @Override
    public void activateMinecart(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
        if (p_96095_4_) attemptTeleport();
    }

    public InteractionResult interact(Player playerEntity, InteractionHand hand) {
        InteractionResult ret = super.interact(playerEntity, hand);
        if (ret.consumesAction()) return ret;
        if (getHasOrb()) return InteractionResult.PASS;

        ItemStack item_used = playerEntity.getItemInHand(hand);
        if (item_used.getItem() == Items.ENDER_PEARL) {
            if (level.isClientSide) return InteractionResult.CONSUME;
            if (owner_uuid == null) {
                addPearl(playerEntity);
                if (!playerEntity.isCreative()) item_used.shrink(1);
            }
        }
        return InteractionResult.PASS;

    }

    protected void addPearl(Player player) {
        owner_uuid = player.getGameProfile().getId();
        setHasOrb(true);
    }

    protected void attemptTeleport() {
        if (level.isClientSide || owner_uuid == null) return;

        Entity entity = this.level.getPlayerByUUID(owner_uuid);

        if (entity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) entity;
            // copied from EnderPearlEntity
            if (player.connection.getConnection().isConnected() && player.level == this.level && !player.isSleeping()) {
                EntityTeleportEvent.ChorusFruit event = new EntityTeleportEvent.ChorusFruit(player,
                        this.getX() + 0.5, this.getY() + 1, this.getZ() + 0.5);
                if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) { // Don't indent to lower patch size
                    if (this.level.random.nextFloat() < 0.05F && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                        Endermite endermiteentity = EntityType.ENDERMITE.create(this.level);
                        endermiteentity.moveTo(entity.getX(), entity.getY(), entity.getZ());
                        this.level.addFreshEntity(endermiteentity);
                    }

                    if (entity.isPassenger()) {
                        entity.stopRiding();
                    }

                    entity.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                    entity.fallDistance = 0.0F;
                    entity.hurt(DamageSource.FALL, 5.0f);
                }
            }
        }

        owner_uuid = null;
        setHasOrb(false);
    }


    @Override
    public BlockState getDefaultDisplayBlockState() {
        BlockState blockState = level.getBlockState(blockPosition());
        boolean is_activated = blockState.is(Blocks.ACTIVATOR_RAIL) && blockState.getValue(PoweredRailBlock.POWERED);

        return MMBlocks.PEARL_STASIS_CHAMBER.get().defaultBlockState().setValue(OrbStasisBlock.CONTAINS_PEARL, getHasOrb())
                .setValue(OrbStasisBlock.POWERED, is_activated );
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.owner_uuid != null) compound.putUUID(OrbStasisTile.PLAYER_UUID_PROPERTY, this.owner_uuid);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID(OrbStasisTile.PLAYER_UUID_PROPERTY)) this.owner_uuid = compound.getUUID(OrbStasisTile.PLAYER_UUID_PROPERTY);
        setHasOrb(owner_uuid != null);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_ORB, false);
    }

    protected boolean getHasOrb() {
        return this.entityData.get(HAS_ORB);
    }

    protected void setHasOrb(boolean powered) {
        this.entityData.set(HAS_ORB, powered);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    //@Override
    //public ItemStack getCartItem() { return new ItemStack(MMItemReferences.minecart_with_stasis); }
}
