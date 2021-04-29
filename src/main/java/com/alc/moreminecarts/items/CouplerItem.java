package com.alc.moreminecarts.items;

import com.alc.moreminecarts.entities.CouplerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.UUID;

@ObjectHolder("moreminecarts")
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

        CompoundNBT tag = stack.getOrCreateTag();
        if (!tag.hasUUID(TAG_COUPLED_UUID_1) && tag.hasUUID(TAG_COUPLED_UUID_2) && entityIn instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityIn;
            player.playSound(SoundEvents.CHAIN_PLACE, 0.7F, 1.0F);
            if (!player.abilities.instabuild) {
                stack.shrink(1);
            }
            clearCoupler(stack);
            return;
        }

        if (! (worldIn instanceof  ServerWorld)) return;

        ServerWorld world = (ServerWorld)worldIn;

        if (tag.hasUUID(TAG_COUPLED_UUID_1) && tag.hasUUID(TAG_COUPLED_UUID_2)){
            UUID uuid1 = tag.getUUID(TAG_COUPLED_UUID_1);
            Entity ent1 = world.getEntity(uuid1);
            UUID uuid2 = tag.getUUID(TAG_COUPLED_UUID_2);
            Entity ent2 = world.getEntity(uuid2);

            if (ent1 != null && ent2 != null && ent1 != ent2) {

                double distance = ent1.distanceTo(ent2);
                if (distance < 3) {

                    Vector3d center_pos = new Vector3d(
                            (ent1.position().x + ent2.position().x)/2,
                            (ent1.position().y + ent2.position().y)/2,
                            (ent1.position().z + ent2.position().z)/2);

                    List<CouplerEntity> list = worldIn.getEntities(coupler,
                            new AxisAlignedBB(center_pos.x + 0.5, center_pos.y + 0.5, center_pos.z + 0.5,
                                            center_pos.x - 0.5, center_pos.y - 0.5, center_pos.z - 0.5), (entity) -> true);

                    boolean is_duplicate = false;
                    for (CouplerEntity ent : list) {
                        if ((ent.getFirstVehicle() == ent1 && ent.getSecondVehicle() == ent2)
                            || (ent.getSecondVehicle() == ent1 && ent.getFirstVehicle() == ent2)) {
                            is_duplicate = true;
                            break;
                        }
                    }

                    if (!is_duplicate) {
                        CouplerEntity coupler_ent = new CouplerEntity(coupler, worldIn, ent1, ent2);
                        worldIn.addFreshEntity(coupler_ent);
                        tag.remove(TAG_COUPLED_UUID_1);
                        return;
                    }
                }
            }

            clearCoupler(stack);
        }

    }

    public static void hookIn(PlayerEntity player, World worldIn, ItemStack used, Entity vehicle) {
        CompoundNBT tag = used.getOrCreateTag();
        //player.playSound(SoundEvents.BLOCK_CHAIN_PLACE, 0.7F, 1.0F);
        if (tag.hasUUID(TAG_COUPLED_UUID_2)){}
        if (tag.hasUUID(TAG_COUPLED_UUID_1)) {
            UUID uuid = vehicle.getUUID();
            tag.putUUID(TAG_COUPLED_UUID_2, uuid);
        }
        else {
            UUID uuid = vehicle.getUUID();
            tag.putUUID(TAG_COUPLED_UUID_1, uuid);
            tag.putInt("CustomModelData", 1);
        }
    }

    public static void clearCoupler(ItemStack used) {
        CompoundNBT tag = used.getOrCreateTag();
        tag.remove(TAG_COUPLED_UUID_1);
        tag.remove(TAG_COUPLED_UUID_2);
        tag.putInt("CustomModelData", 0);
    }
}
