package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.registry.MMItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SoulfireCartEntity extends CampfireCartEntity {

    public SoulfireCartEntity(EntityType<?> furnaceCart, Level world) {
        super(furnaceCart, world);
    }

    public SoulfireCartEntity(EntityType<?> furnaceCart, Level worldIn, double x, double y, double z) {
        super(furnaceCart, worldIn, x, y, z);
    }

    public Item getDropItem() {
        return MMItems.SOULFIRE_CART_ITEM.get();
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

    //@Override
    //public ItemStack getCartItem() { return new ItemStack(MMItemReferences.soulfire_cart); }

}
