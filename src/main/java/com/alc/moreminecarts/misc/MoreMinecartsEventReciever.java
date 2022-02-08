package com.alc.moreminecarts.misc;


import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.MMItemReferences;
import com.alc.moreminecarts.MoreMinecartsMod;
import com.alc.moreminecarts.entities.HSMinecartEntities;
import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.alc.moreminecarts.items.CouplerItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.slf4j.Log4jLogger;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = "moreminecarts")
public class MoreMinecartsEventReciever {

    @SubscribeEvent
    public static void onInteractEntity(PlayerInteractEvent.EntityInteract event) {

        if (event.getEntity() instanceof PistonPushcartEntity) {
            MoreMinecartsMod.LOGGER.log(org.apache.logging.log4j.Level.WARN, "piston pushcart interact");
        }

        InteractionHand hand = event.getHand();
        Player player = event.getPlayer();
        ItemStack using = player.getItemInHand(hand);

        InteractionHand other_hand = hand == InteractionHand.MAIN_HAND? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack using_secondary = player.getItemInHand(other_hand);

        Entity entity = event.getTarget();

        // We check both hands, but only use one, since this function gets called once for each hand.
        if (using.getItem() == MMItemReferences.coupler || using_secondary.getItem() == MMItemReferences.coupler) {
            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);
            if (event.getWorld().isClientSide()) return;

            if (using.getItem() == MMItemReferences.coupler) {
                if (entity instanceof AbstractMinecart
                    || entity instanceof Boat
                    || entity instanceof Mob
                    || entity instanceof EnderDragon){

                    Level world = event.getWorld();
                    player.playSound(SoundEvents.CHAIN_PLACE, 0.9F, 1.0F);
                    CouplerItem.hookIn(player, world, using, entity);
                }
                else {
                    CouplerItem.clearCoupler(using);
                }
            }
        }

        if (using.getItem() == MMItemReferences.high_speed_upgrade || using_secondary.getItem() == MMItemReferences.high_speed_upgrade) {
            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);

            if (event.getWorld().isClientSide()) return;

            if (using.getItem() == MMItemReferences.high_speed_upgrade && entity instanceof AbstractMinecart
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

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        if (MMConstants.CONFIG_GLASS_CACTUS_SPAWNS.get() == 0) return;

        if (event.getCategory() == Biome.BiomeCategory.DESERT || event.getCategory() == Biome.BiomeCategory.MESA) {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MoreMinecartsMod.GLASS_CACTUS_PLACER);
        }
    }

}
