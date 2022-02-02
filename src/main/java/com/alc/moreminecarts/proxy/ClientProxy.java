package com.alc.moreminecarts.proxy;

import com.alc.moreminecarts.client.PistonPushcartDownKey;
import com.alc.moreminecarts.client.PistonPushcartUpKey;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.ClientRegistry;

public class ClientProxy implements IProxy {


    @Override
    public Level getWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public Player getPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public boolean isHoldingJump() {
        return Minecraft.getInstance().player.input.jumping;
    }

    @Override
    public boolean isHoldingRun() {
        return Minecraft.getInstance().player.input.shiftKeyDown;
    }

    public void setupKeybindings() {
        // Jump key
        ClientRegistry.registerKeyBinding(new PistonPushcartUpKey("Piston Pushcart Up", 32, "More Minecarts and Rails"));
        // Left control key
        ClientRegistry.registerKeyBinding(new PistonPushcartDownKey("Piston Pushcart Down", 341, "More Minecarts and Rails"));
    }


}
