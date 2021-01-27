package com.alc.moreminecarts.entities;

import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


// Pushcarts can't have entities besides players on them, so we always return canBeRidden as false,
// but we force it if it's a player
public class MinecartWithNet extends AbstractMinecartEntity {

    public MinecartWithNet(EntityType<?> type, World world) {
        super(type, world);
    }

    public MinecartWithNet(EntityType<?> type, World worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    @Override
    public Type getMinecartType() {
        return Type.FURNACE;
    }

    @Override
    public void killMinecart(DamageSource source) {
        super.killMinecart(source);
        if (!source.isExplosion() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.entityDropItem(Items.STRING);
            this.entityDropItem(Items.STRING);
            this.entityDropItem(Items.STRING);
            this.entityDropItem(Items.STRING);
            this.entityDropItem(Items.FERMENTED_SPIDER_EYE);
        }

    }


    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


    // Special Stuff

    public int getRadius() {return 4;}

    public double getInnerRadius() {return 0.7;}

    // Reel in a random close-but-not-that-close item to the cart every so often
    @Override
    public void tick() {
        super.tick();

        if (this.world.isRemote) return;
        if (this.ticksExisted % 20 != 0) return;

        int radius = this.getRadius();
        int radSq = radius * radius;

        AxisAlignedBB area = new AxisAlignedBB(this.getPosition().add(-radius, -radius, -radius), this.getPosition().add(radius, radius, radius));
        List<ItemEntity> all_items = this.world.getEntitiesWithinAABB(ItemEntity.class, area, EntityPredicates.IS_ALIVE);
        List<ItemEntity> filtered_items = new ArrayList<ItemEntity>();
        for (ItemEntity ie:all_items) {
            if (!ie.getPosition().withinDistance(this.getPosition(), getInnerRadius())) {
                filtered_items.add(ie);
            }
        }
        //ItemEntity[] filtered_items = (ItemEntity[]) possible_items.stream().filter(item -> !item.getPosition().withinDistance(this.getPosition(), getInnerRadius())).toArray();

        if (filtered_items.size() == 0) return;
        int rand_index = this.world.rand.nextInt(filtered_items.size());

        ItemEntity selected_item = filtered_items.get(rand_index);
        double d0 = this.getPosX() - selected_item.getPosX();
        double d1 = this.getPosY() - selected_item.getPosY();
        double d2 = this.getPosZ() - selected_item.getPosZ();
        double d3 = 0.1D;
        selected_item.setMotion(d0 * 0.1D, d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);

        this.world.playSound((PlayerEntity)null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 0.8F, 0.4F / (this.world.rand.nextFloat() * 0.4F + 0.8F));
    }

    public BlockState getDefaultDisplayTile() {
        return Blocks.COBWEB.getDefaultState();
    }
}
