package com.alc.moreminecarts.tile_entities;

import com.alc.moreminecarts.registry.MMTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityTeleportEvent;

import javax.annotation.Nullable;
import java.util.UUID;

public class OrbStasisTile extends BlockEntity {
    public static String PLAYER_UUID_PROPERTY = "player_uuid";

    @Nullable
    public UUID owner_uuid;

    public OrbStasisTile(BlockPos pos, BlockState state) {
        super(MMTileEntities.PEARL_STASIS_CHAMBER_TILE_ENTITY.get(), pos, state);
        owner_uuid = null;
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.putUUID(PLAYER_UUID_PROPERTY, owner_uuid);
    }

    @Override
    public void load(CompoundTag compound) {
        if (compound.hasUUID(PLAYER_UUID_PROPERTY)) owner_uuid = compound.getUUID(PLAYER_UUID_PROPERTY);
        else owner_uuid = null;

        super.load(compound);
    }

    @Override
    public void setRemoved() {
        if (!level.isClientSide) {
            attemptTeleport();
        }
        super.setRemoved();
    }

    protected void addPearl(Player player) {
        owner_uuid = player.getGameProfile().getId();
    }

    protected void attemptTeleport() {
        if (level.isClientSide || owner_uuid == null) return;

        Entity entity = this.level.getPlayerByUUID(owner_uuid);

        if (entity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) entity;
            // copied from EnderPearlEntity
            if (player.connection.getConnection().isConnected() && player.level() == this.level && !player.isSleeping()) {
                EntityTeleportEvent.ChorusFruit event = new EntityTeleportEvent.ChorusFruit(player,
                        this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 1, this.getBlockPos().getZ() + 0.5);
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
                    entity.hurt(this.level.damageSources().fall(), 5.0f);
                }
            }
        }

        owner_uuid = null;
    }

    // Returns true if there is a pearl inside.
    public boolean updateLock(boolean powered, Player player) {

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
