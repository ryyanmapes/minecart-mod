package com.alc.moreminecarts.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

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


}
