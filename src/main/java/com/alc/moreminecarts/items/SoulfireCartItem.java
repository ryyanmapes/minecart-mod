package com.alc.moreminecarts.items;

import com.alc.moreminecarts.entities.SoulfireCartEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("moreminecarts")
public class SoulfireCartItem extends AbstractMinecartItem {

    public static final EntityType<SoulfireCartEntity> soulfire_cart = null;

    public SoulfireCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        SoulfireCartEntity minecart = new SoulfireCartEntity(soulfire_cart, world, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addFreshEntity(minecart);
    }
}
