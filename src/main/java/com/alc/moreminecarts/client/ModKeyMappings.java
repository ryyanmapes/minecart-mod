package com.alc.moreminecarts.client;

import com.alc.moreminecarts.MMConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MMConstants.modid, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModKeyMappings {

	// Jump key
	public static final KeyMapping PISTON_PUSHCART_UP = new PistonPushcartUpKey("Piston Pushcart Up", 32, "More Minecarts and Rails");

	// Left control key
	public static final KeyMapping PISTON_PUSHCART_DOWN = new PistonPushcartDownKey("Piston Pushcart Down", 341, "More Minecarts and Rails");

	@SubscribeEvent
	public void setupKeybindings(RegisterKeyMappingsEvent event) {
		event.register(PISTON_PUSHCART_UP);
		event.register(PISTON_PUSHCART_DOWN);
	}
}
