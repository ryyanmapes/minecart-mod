package com.alc.moreminecarts.proxy;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IProxy {
    public Level getWorld();
    public Player getPlayer();
    public boolean isHoldingJump();
    public boolean isHoldingRun();
}
