package com.example.examplemod.misc;

import com.example.examplemod.entities.CouplerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.fml.network.FMLPlayMessages.*;

@ObjectHolder("examplemod")
public class CouplerClientFactory {

    public static final EntityType<CouplerEntity> coupler = null;

    public CouplerClientFactory() {super();}

    public static java.util.function.BiFunction<net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity, World, CouplerEntity> get() {
        return CouplerClientFactory::getCouplerFromPacket;
    }

    public static CouplerEntity getCouplerFromPacket(SpawnEntity packet, World world) {
        CouplerEntity ent = new CouplerEntity(coupler, world);
        ent.setEntityId(packet.getEntityId());
        ent.setUniqueId(packet.getUuid());

        PacketBuffer buf = packet.getAdditionalData();
        ent.vehicle2_id = buf.readInt();
        ent.vehicle1_id = buf.readInt();

        return ent;
    }

}
