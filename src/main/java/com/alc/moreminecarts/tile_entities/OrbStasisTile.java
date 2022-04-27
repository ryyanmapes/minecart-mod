package com.alc.moreminecarts.tile_entities;

import com.alc.moreminecarts.MMReferences;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;

import javax.annotation.Nullable;
import java.util.UUID;

public class OrbStasisTile extends TileEntity {
    public static String PLAYER_UUID_PROPERTY = "player_uuid";

    @Nullable
    public UUID owner_uuid;

    public OrbStasisTile() {
        super(MMReferences.pearl_stasis_chamber_te);
        owner_uuid = null;
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        if (owner_uuid != null) compound.putUUID(PLAYER_UUID_PROPERTY, owner_uuid);
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        if (compound.hasUUID(PLAYER_UUID_PROPERTY)) owner_uuid = compound.getUUID(PLAYER_UUID_PROPERTY);
        super.load(state, compound);
    }

    @Override
    public void setRemoved() {
        if (!level.isClientSide) {
            attemptTeleport();
        }
        super.setRemoved();
    }

    protected void addPearl(PlayerEntity player) {
        owner_uuid = player.getGameProfile().getId();
    }

    protected void attemptTeleport() {
        if (level.isClientSide || owner_uuid == null) return;

        Entity entity = this.level.getPlayerByUUID(owner_uuid);

        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            // copied from EnderPearlEntity
            if (player.connection.getConnection().isConnected() && player.level == this.level && !player.isSleeping()) {
                net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(player,
                        this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 1, this.getBlockPos().getZ() + 0.5, 5.0F);
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
    }

    // Returns true if there is a pearl inside.
    public boolean updateLock(boolean powered, PlayerEntity player) {

        if (owner_uuid == null && player != null) {
            addPearl(player);
        }

        if (owner_uuid != null && powered) {
            attemptTeleport();
        }

        return owner_uuid != null;
    }


    public int getComparatorSignal() {
        return owner_uuid == null ? 0 : 15;
    }
}
