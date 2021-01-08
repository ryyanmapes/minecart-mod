package com.example.examplemod.items;

import com.example.examplemod.entities.CouplerEntity;
import com.example.examplemod.entities.IronPushcartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.Vector;

@ObjectHolder("examplemod")
public class CouplerItem extends Item {
    public static final EntityType<CouplerEntity> coupler = null;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String TAG_COUPLED_UUID_1 = "coupled_UUID_1";
    private static final String TAG_COUPLED_UUID_2 = "coupled_UUID_2";

    public CouplerItem(Properties properties) {
        super(properties);
    }


    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {

        if (! ( worldIn instanceof ServerWorld) ) return;
        ServerWorld world = (ServerWorld)worldIn;

        CompoundNBT tag = stack.getOrCreateTag();
        if (tag.hasUniqueId(TAG_COUPLED_UUID_1) && tag.hasUniqueId(TAG_COUPLED_UUID_2)){
            UUID uuid1 = tag.getUniqueId(TAG_COUPLED_UUID_1);
            Entity ent1 = world.getEntityByUuid(uuid1);
            UUID uuid2 = tag.getUniqueId(TAG_COUPLED_UUID_2);
            Entity ent2 = world.getEntityByUuid(uuid2);

            if (ent1 != null && ent2 != null && ent1 != ent2) {

                double distance = ent1.getDistance(ent2);
                if (distance < 5) {
                    CouplerEntity coupler_ent = new CouplerEntity(coupler, worldIn, ent1, ent2);
                    worldIn.addEntity(coupler_ent);
                }
            }

            clearCoupler(stack);
        }

    }

    public static void hookIn(World worldIn, ItemStack used, Entity vehicle) {
        CompoundNBT tag = used.getOrCreateTag();
        if (tag.hasUniqueId(TAG_COUPLED_UUID_2)){}
        if (tag.hasUniqueId(TAG_COUPLED_UUID_1)) {
            UUID uuid = vehicle.getUniqueID();
            tag.putUniqueId(TAG_COUPLED_UUID_2, uuid);
        }
        else {
            UUID uuid = vehicle.getUniqueID();
            tag.putUniqueId(TAG_COUPLED_UUID_1, uuid);
        }
    }

    public static void clearCoupler(ItemStack used) {
        CompoundNBT tag = used.getOrCreateTag();
        tag.remove(TAG_COUPLED_UUID_1);
        tag.remove(TAG_COUPLED_UUID_2);
    }
}
