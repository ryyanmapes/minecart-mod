package com.alc.moreminecarts.client;

import com.alc.moreminecarts.MoreMinecartsMod;
import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.alc.moreminecarts.proxy.MoreMinecartsPacketHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;

public class PistonPushcartDownKey extends KeyBinding {


    public PistonPushcartDownKey(String description, int default_key, String category) {
        super(description, default_key, category);
    }

    @Override
    public void setDown(boolean pressed) {
        MoreMinecartsPacketHandler.INSTANCE.sendToServer(new MoreMinecartsPacketHandler.PistonPushcartPacket(false, pressed));

        PlayerEntity player = MoreMinecartsMod.PROXY.getPlayer();
        if (player.getRootVehicle() instanceof PistonPushcartEntity) {
            ((PistonPushcartEntity) player.getRootVehicle()).setElevating(false, pressed);
        }

        super.setDown(pressed);
    }
}
