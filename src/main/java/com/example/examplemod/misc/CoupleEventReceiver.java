package com.example.examplemod.misc;


import com.example.examplemod.ExampleMod;
import com.example.examplemod.entities.CouplerEntity;
import com.example.examplemod.items.CouplerItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ObjectHolder;
import org.lwjgl.system.CallbackI;

@ObjectHolder("examplemod")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = "examplemod")
public class CoupleEventReceiver {

    public static final Item coupler = null;

    @SubscribeEvent
    public static void onInteractEntity(PlayerInteractEvent.EntityInteract event) {



        Hand hand = event.getHand();
        PlayerEntity player = event.getPlayer();
        ItemStack using = player.getHeldItem(hand);

        Hand other_hand = hand == Hand.MAIN_HAND? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack using_secondary = player.getHeldItem(other_hand);

        Entity entity = event.getTarget();

        if (using.getItem() == coupler || using_secondary.getItem() == coupler) {
            event.setCanceled(true);
            if (event.getWorld().isRemote()) return;

            if (using.getItem() == coupler) {
                if (entity instanceof AbstractMinecartEntity
                    || entity instanceof BoatEntity
                    || entity instanceof MobEntity
                    || entity instanceof EnderDragonEntity
                    || entity instanceof ArmorStandEntity){
                    World world = event.getWorld();
                    player.playSound(SoundEvents.BLOCK_CHAIN_PLACE, 0.9F, 1.0F);
                    CouplerItem.hookIn(player, world, using, entity);
                }
                else {
                    CouplerItem.clearCoupler(using);
                }
            }
        }

    }

}
