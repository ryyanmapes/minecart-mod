package com.alc.moreminecarts.misc;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.CouplerEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;

public class CouplerClientFactory {

    public CouplerClientFactory() {super();}

    public static java.util.function.BiFunction<net.minecraftforge.network.PlayMessages.SpawnEntity, Level, CouplerEntity> get() {
        return CouplerClientFactory::getCouplerFromPacket;
    }

    public static CouplerEntity getCouplerFromPacket(PlayMessages.SpawnEntity packet, Level world) {

        CouplerEntity ent = new CouplerEntity(MMReferences.coupler, world);
        /*
        ent.setId(packet.getEntityId());
        ent.setUUID(packet.getUuid());

        FriendlyByteBuf buf = packet.getAdditionalData();
        ent.vehicle2_id = buf.readInt();
        ent.vehicle1_id = buf.readInt();
        */

        return ent;
    }

}
