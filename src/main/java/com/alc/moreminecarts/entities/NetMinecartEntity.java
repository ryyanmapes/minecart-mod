package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMItemReferences;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;


// Pushcarts can't have entities besides players on them, so we always return canBeRidden as false,
// but we force it if it's a player
public class NetMinecartEntity extends AbstractMinecart {

    public NetMinecartEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public NetMinecartEntity(EntityType<?> type, Level worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    @Override
    public Type getMinecartType() {
        return Type.FURNACE;
    }

    @Override
    public void destroy(DamageSource source) {
        super.destroy(source);
        if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(Items.STRING);
            this.spawnAtLocation(Items.STRING);
            this.spawnAtLocation(Items.STRING);
            this.spawnAtLocation(Items.STRING);
            this.spawnAtLocation(Items.FERMENTED_SPIDER_EYE);
        }

    }

    // Special Stuff

    public int getRadius() {return 4;}

    public double getInnerRadius() {return 0.7;}

    // Reel in a random close-but-not-that-close item to the cart every so often
    @Override
    public void tick() {
        super.tick();

        if (this.level.isClientSide()) return;
        if (this.tickCount % 20 != 0) return;

        int radius = this.getRadius();
        int radSq = radius * radius;

        AABB area = new AABB(this.position().add(-radius, -radius, -radius), this.position().add(radius, radius, radius));
        List<ItemEntity> all_items = this.level.getEntitiesOfClass(ItemEntity.class, area, EntitySelector.ENTITY_STILL_ALIVE); // TODO Is this the correct method?
        List<ItemEntity> filtered_items = new ArrayList<ItemEntity>();
        for (ItemEntity ie:all_items) {
            if (!ie.position().closerThan(this.position(), getInnerRadius())) {
                filtered_items.add(ie);
            }
        }
        //ItemEntity[] filtered_items = (ItemEntity[]) possible_items.stream().filter(item -> !item.getPosition().setValueinDistance(this.getPosition(), getInnerRadius())).toArray();

        if (filtered_items.size() == 0) return;
        int rand_index = this.level.random.nextInt(filtered_items.size());

        ItemEntity selected_item = filtered_items.get(rand_index);
        double d0 = this.position().x - selected_item.position().x;
        double d1 = this.position().y - selected_item.position().y;
        double d2 = this.position().z - selected_item.position().z;
        double d3 = 0.1D;
        selected_item.setDeltaMovement(d0 * 0.1D, d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);

        this.level.playSound((Player)null, this.position().x, this.position().y, this.position().z, SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 0.8F, 0.4F / (this.level.random.nextFloat() * 0.4F + 0.8F));
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.COBWEB.defaultBlockState();
    }



    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public ItemStack getCartItem() { return new ItemStack(MMItemReferences.minecart_with_net); }
}
