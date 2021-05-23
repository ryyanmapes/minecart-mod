package com.alc.moreminecarts.proxy;

import com.alc.moreminecarts.client.PistonPushcartDownKey;
import com.alc.moreminecarts.client.PistonPushcartUpKey;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy implements IProxy {


    @Override
    public World getWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public PlayerEntity getPlayer() {
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
