package com.example.examplemod.items;

import com.example.examplemod.entities.CampfireCartEntity;
import com.example.examplemod.entities.IronPushcartEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("examplemod")
public class CampfireCartItem extends AbstractMinecartItem {

    public static final EntityType<IronPushcartEntity> campfire_cart = null;

    public CampfireCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        CampfireCartEntity minecart = new CampfireCartEntity(campfire_cart, world, posX, posY, posZ);
        if (stack.hasDisplayName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addEntity(minecart);
    }
}
