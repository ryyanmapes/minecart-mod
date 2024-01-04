package com.alc.moreminecarts.entities;

import com.alc.moreminecarts.registry.MMItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class IronPushcartEntity extends AbstractPushcart {

    public IronPushcartEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public IronPushcartEntity(EntityType<?> type, Level worldIn, double x, double y, double z) {
        super(type, worldIn, x, y, z);
    }

    public double getControlSpeed() { return 100; }
    public double getUphillSpeed() { return 5.5; }
    public double getBrakeSpeed() { return 0; }

    public Item getDropItem() {
        return MMItems.IRON_PUSHCART_ITEM.get();
    }

    //@Override
    //public ItemStack getCartItem() { return new ItemStack(MMItemReferences.iron_pushcart); }
}
