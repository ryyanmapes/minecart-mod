package com.alc.moreminecarts.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IProxy {
    public World getWorld();
    public PlayerEntity getPlayer();
    public boolean isHoldingJump();
    public boolean isHoldingRun();
}
