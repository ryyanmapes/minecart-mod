package com.example.examplemod.items;

import com.example.examplemod.entities.CampfireCartEntity;
import com.example.examplemod.entities.IronPushcartEntity;
import com.example.examplemod.entities.SoulfireCartEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("examplemod")
public class SoulfireCartItem extends AbstractMinecartItem {

    public static final EntityType<SoulfireCartEntity> soulfire_cart = null;

    public SoulfireCartItem(Properties builder) {
        super(builder);
    }

    @Override
    void createMinecart(ItemStack stack, World world, double posX, double posY, double posZ) {

        SoulfireCartEntity minecart = new SoulfireCartEntity(soulfire_cart, world, posX, posY, posZ);
        if (stack.hasDisplayName()) {
            minecart.setCustomName(stack.getDisplayName());
        }
        world.addEntity(minecart);
    }
}
