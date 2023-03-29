package com.alc.moreminecarts.misc;


import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.MoreMinecartsMod;
import com.alc.moreminecarts.entities.HSMinecartEntities;
import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.alc.moreminecarts.items.CouplerItem;
import com.alc.moreminecarts.registry.MMItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = "moreminecarts")
public class MMEventReciever {

    @SubscribeEvent
    public static void onInteractEntity(PlayerInteractEvent.EntityInteract event) {

        if (event.getTarget() instanceof PistonPushcartEntity) {
            MoreMinecartsMod.LOGGER.log(org.apache.logging.log4j.Level.WARN, "piston pushcart interact");
        }

        InteractionHand hand = event.getHand();
        Player player = event.getEntity();
        ItemStack using = player.getItemInHand(hand);

        InteractionHand other_hand = hand == InteractionHand.MAIN_HAND? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack using_secondary = player.getItemInHand(other_hand);

        Entity entity = event.getTarget();

        Item couplerItem = MMItems.COUPLER_ITEM.get();

        // We check both hands, but only use one, since this function gets called once for each hand.
        if (using.getItem() == couplerItem || using_secondary.getItem() == couplerItem) {
            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);
            if (event.getLevel().isClientSide()) return;

            if (using.getItem() == couplerItem) {
                if (entity instanceof AbstractMinecart
                    || entity instanceof Boat
                    || entity instanceof Mob
                    || entity instanceof EnderDragon){

                    Level world = event.getLevel();
                    player.playSound(SoundEvents.CHAIN_PLACE, 0.9F, 1.0F);
                    CouplerItem.hookIn(player, world, using, entity);
                }
                else {
                    CouplerItem.clearCoupler(using);
                }
            }
        }

        Item hsUpgradeItem = MMItems.HIGH_SPEED_UPGRADE_ITEM.get();


        if (using.getItem() == hsUpgradeItem || using_secondary.getItem() == hsUpgradeItem) {
            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);

            if (event.getLevel().isClientSide()) return;

            if (using.getItem() == hsUpgradeItem && entity instanceof AbstractMinecart
                && !(entity instanceof HSMinecartEntities.IHSCart)) {
                boolean success = HSMinecartEntities.upgradeMinecart((AbstractMinecart) entity);
                if (!player.isCreative() && success) using.shrink(1);
            }
        }

        // To prevent entering a high speed cart immediately after upgrading it.
        if ( (event.getTarget() instanceof HSMinecartEntities.HSMinecart || event.getTarget() instanceof HSMinecartEntities.HSPushcart)
                && event.getTarget().tickCount < 10) {
            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);
            return;
        }

    }

}
