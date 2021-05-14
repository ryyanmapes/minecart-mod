package com.alc.moreminecarts.misc;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.CouplerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity;

public class CouplerClientFactory {

    public CouplerClientFactory() {super();}

    public static java.util.function.BiFunction<net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity, World, CouplerEntity> get() {
        return CouplerClientFactory::getCouplerFromPacket;
    }

    public static CouplerEntity getCouplerFromPacket(SpawnEntity packet, World world) {
        CouplerEntity ent = new CouplerEntity(MMReferences.coupler, world);
        ent.setId(packet.getEntityId());
        ent.setUUID(packet.getUuid());

        PacketBuffer buf = packet.getAdditionalData();
        ent.vehicle2_id = buf.readInt();
        ent.vehicle1_id = buf.readInt();

        return ent;
    }

}
