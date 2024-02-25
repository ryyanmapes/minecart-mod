package com.alc.moreminecarts.client;

import com.alc.moreminecarts.MoreMinecartsMod;
import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.alc.moreminecarts.proxy.MoreMinecartsPacketHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PistonPushcartUpKey extends KeyMapping {


    public PistonPushcartUpKey(String description, int default_key, String category) {
        super(description, default_key, category);
    }

    @Override
    public void setDown(boolean pressed) {
        super.setDown(pressed);

        if (Minecraft.getInstance().getConnection() == null) return;

        Player player = MoreMinecartsMod.PROXY.getPlayer();
        if (player.getRootVehicle() instanceof PistonPushcartEntity) {
            MoreMinecartsPacketHandler.PistonPushcartPacket packet = new MoreMinecartsPacketHandler.PistonPushcartPacket(true, pressed);
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(packet);
        }

    }
}
