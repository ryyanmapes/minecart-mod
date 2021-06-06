package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMItemReferences;
import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.blocks.OrbStasisBlock;
import com.alc.moreminecarts.tile_entities.OrbStasisTile;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;


// Pushcarts can't have entities besides players on them, so we always return canBeRidden as false,
// but we force it if it's a player
public class OrbStasisCart extends AbstractMinecartEntity {

    private static final DataParameter<Boolean> HAS_ORB = EntityDataManager.defineId(OrbStasisCart.class, DataSerializers.BOOLEAN);

    @Nullable
    public UUID owner_uuid;

    public OrbStasisCart(EntityType<?> type, World world) {
        super(type, world);
    }

    public OrbStasisCart(EntityType<?> type, World worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    @Override
    public Type getMinecartType() {
        return Type.CHEST;
    }

    @Override
    public void destroy(DamageSource source) {
        super.destroy(source);
        if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(MMItemReferences.pearl_stasis_chamber);
        }
    }

    @Override
    public void remove() {
        super.remove();
        attemptTeleport();
    }

    @Override
    public void activateMinecart(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
        if (p_96095_4_) attemptTeleport();
    }

    public ActionResultType interact(PlayerEntity playerEntity, Hand hand) {
        ActionResultType ret = super.interact(playerEntity, hand);
        if (ret.consumesAction()) return ret;
        if (getHasOrb()) return ActionResultType.PASS;

        ItemStack item_used = playerEntity.getItemInHand(hand);
        if (item_used.getItem() == Items.ENDER_PEARL) {
            if (level.isClientSide) return ActionResultType.CONSUME;
            if (owner_uuid == null) {
                addPearl(playerEntity);
                if (!playerEntity.isCreative()) item_used.shrink(1);
            }
        }
        return ActionResultType.PASS;

    }

    protected void addPearl(PlayerEntity player) {
        owner_uuid = player.getGameProfile().getId();
        setHasOrb(true);
    }

    protected void attemptTeleport() {
        if (level.isClientSide || owner_uuid == null) return;

        Entity entity = this.level.getPlayerByUUID(owner_uuid);

        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            // copied from EnderPearlEntity
            if (player.connection.getConnection().isConnected() && player.level == this.level && !player.isSleeping()) {
                net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(player,
                        this.getX(), this.getY(), this.getZ(), 5.0F);
                if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) { // Don't indent to lower patch size
                    if (this.level.random.nextFloat() < 0.05F && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                        EndermiteEntity endermiteentity = EntityType.ENDERMITE.create(this.level);
                        endermiteentity.setPlayerSpawned(true);
                        endermiteentity.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.yRot, entity.xRot);
                        this.level.addFreshEntity(endermiteentity);
                    }

                    if (entity.isPassenger()) {
                        entity.stopRiding();
                    }

                    entity.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                    entity.fallDistance = 0.0F;
                    entity.hurt(DamageSource.FALL, event.getAttackDamage());
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

        return MMReferences.pearl_stasis_chamber.defaultBlockState().setValue(OrbStasisBlock.CONTAINS_PEARL, getHasOrb())
                .setValue(OrbStasisBlock.POWERED, is_activated );
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putUUID(OrbStasisTile.PLAYER_UUID_PROPERTY, this.owner_uuid);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.owner_uuid = compound.getUUID(OrbStasisTile.PLAYER_UUID_PROPERTY);
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
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
