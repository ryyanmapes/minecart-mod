package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.MMItemReferences;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SoulfireCartEntity extends CampfireCartEntity {

    public SoulfireCartEntity(EntityType<?> furnaceCart, World world) {
        super(furnaceCart, world);
    }

    public SoulfireCartEntity(EntityType<?> furnaceCart, World worldIn, double x, double y, double z) {
        super(furnaceCart, worldIn, x, y, z);
    }

    @Override
    public void destroy(DamageSource source) {
        this.remove();
        if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(MMItemReferences.soulfire_cart);
        }

    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.SOUL_CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, Boolean.valueOf(isMinecartPowered()));
    }

    @Override
    public double getSpeedDiv() {
        return 8;
    }

    public boolean isGoingUphill() {
        return false;
    }

    @Override
    public ItemStack getCartItem() { return new ItemStack(MMItemReferences.soulfire_cart); }

}
