package com.alc.moreminecarts.misc;


import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// This allows players to break things at normal speeds while riding anything.
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = "moreminecarts")
public class BreakSpeedEventReceiver {

    @SubscribeEvent
    public static void getBreakSpeed(PlayerEvent.BreakSpeed event) {

        Player player = event.getEntity();
        if (player.getVehicle() != null && !player.isOnGround()) {
            // When a player is not grounded, the mining speed is always divided by 5.
            event.setNewSpeed(event.getOriginalSpeed() * 5);
        }

    }

}
