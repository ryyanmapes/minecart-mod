package com.alc.moreminecarts.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ServerProxy implements IProxy{
    @Override
    public World getWorld() {
        return null;
    }

    @Override
    public PlayerEntity getPlayer() {
        return null;
    }
}
