package com.alc.moreminecarts.misc;

import com.alc.moreminecarts.entities.CouplerEntity;
import com.alc.moreminecarts.registry.MMEntities;
import net.minecraft.world.level.Level;

public class CouplerClientFactory {

    public CouplerClientFactory() {super();}

    public static java.util.function.BiFunction<net.minecraftforge.network.packets.SpawnEntity, Level, CouplerEntity> get() {
        return CouplerClientFactory::getCouplerFromPacket;
    }

    public static CouplerEntity getCouplerFromPacket(net.minecraftforge.network.packets.SpawnEntity packet, Level world) {

        CouplerEntity ent = new CouplerEntity(MMEntities.COUPLER_ENTITY.get(), world);
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
