package com.alc.moreminecarts.proxy;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ServerProxy implements IProxy{
    @Override
    public Level getWorld() {
        return null;
    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public boolean isHoldingJump() {
        return false;
    }

    @Override
    public boolean isHoldingRun() {
        return false;
    }

}
