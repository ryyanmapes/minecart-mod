package com.alc.moreminecarts.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;


// Pushcarts can't have entities besides players on them, so we always return canBeRidden as false,
// but we force it if it's a player
public class NetMinecartEntity extends AbstractMinecartEntity {

    public NetMinecartEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public NetMinecartEntity(EntityType<?> type, World worldIn, double x, double y, double z) {
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

        AxisAlignedBB area = new AxisAlignedBB(this.position().add(-radius, -radius, -radius), this.position().add(radius, radius, radius));
        List<ItemEntity> all_items = this.level.getLoadedEntitiesOfClass(ItemEntity.class, area, EntityPredicates.ENTITY_STILL_ALIVE); // TODO Is this the correct method?
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

        this.level.playSound((PlayerEntity)null, this.position().x, this.position().y, this.position().z, SoundEvents.FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 0.8F, 0.4F / (this.level.random.nextFloat() * 0.4F + 0.8F));
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.COBWEB.defaultBlockState();
    }



    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
