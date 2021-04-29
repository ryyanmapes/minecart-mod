package com.alc.moreminecarts.misc;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

// This allows players to break things at normal speeds while riding anything.
@ObjectHolder("moreminecarts")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = "moreminecarts")
public class BreakSpeedEventReceiver {

    @SubscribeEvent
    public static void getBreakSpeed(PlayerEvent.BreakSpeed event) {

        PlayerEntity player = event.getPlayer();
        if (player.getVehicle() != null && !player.isOnGround()) {
            // When a player is not grounded, the mining speed is always divided by 5.
            event.setNewSpeed(event.getOriginalSpeed() * 5);
        }

    }

}
