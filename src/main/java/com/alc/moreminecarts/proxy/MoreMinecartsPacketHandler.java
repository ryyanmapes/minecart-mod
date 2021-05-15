package com.alc.moreminecarts.proxy;

import com.alc.moreminecarts.MoreMinecartsMod;
import com.alc.moreminecarts.containers.ChunkLoaderContainer;
import com.alc.moreminecarts.entities.CouplerEntity;
import com.alc.moreminecarts.entities.PistonPushcartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class MoreMinecartsPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("moreminecarts", "example"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void Init() {
        int id = 0;

        // For syncing the coupler to the server -> client
        INSTANCE.registerMessage(id++,
                CouplePacket.class,
                CouplePacket::encode,
                CouplePacket::decode,
                CouplePacket::handle);

        // For changing the chunk loader client -> server
        INSTANCE.registerMessage(id++,
                ChunkLoaderPacket.class,
                ChunkLoaderPacket::encode,
                ChunkLoaderPacket::decode,
                ChunkLoaderPacket::handle);

        // For changing the piston pushcart client -> server
        INSTANCE.registerMessage(id++,
                ChunkLoaderPacket.class,
                ChunkLoaderPacket::encode,
                ChunkLoaderPacket::decode,
                ChunkLoaderPacket::handle);
    }

    // Currently unused.
    public static class CouplePacket {
        public int coupler_id;
        public int v1;
        public int v2;

        public CouplePacket(int coupler_id, int v1, int v2) {
            this.coupler_id = coupler_id;
            this.v1 = v1;
            this.v2 = v2;
        }

        public static void encode(CouplePacket msg,  PacketBuffer buf) {
            buf.writeInt(msg.v1);
            buf.writeInt(msg.v2);
            buf.writeInt(msg.coupler_id);
        }

        public static CouplePacket decode(PacketBuffer buf) {
            CouplePacket packet = new CouplePacket(0,0,0);
            packet.v1 = buf.readInt();
            packet.v2 = buf.readInt();
            packet.coupler_id = buf.readInt();
            return packet;
        }


        public static void handle(CouplePacket msg, Supplier<NetworkEvent.Context> ctx) {
            //LogManager.getLogger().info("HERE!!!");
            ctx.get().enqueueWork(() -> {

                World world = MoreMinecartsMod.PROXY.getWorld();

                Entity ent = world.getEntity(msg.coupler_id);
                if (ent != null && ent instanceof CouplerEntity) {
                    CouplerEntity coupler_ent = (CouplerEntity) ent;
                    coupler_ent.vehicle1_id = msg.v1;
                    coupler_ent.vehicle2_id = msg.v2;
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

    public static class ChunkLoaderPacket {
        public boolean set_enabled;

        public ChunkLoaderPacket(boolean set_enabled) {
            this.set_enabled = set_enabled;
        }

        public static void encode(ChunkLoaderPacket msg, PacketBuffer buf) {
            buf.writeBoolean(msg.set_enabled);
        }

        public static ChunkLoaderPacket decode(PacketBuffer buf) {
            ChunkLoaderPacket packet = new ChunkLoaderPacket(false);
            packet.set_enabled = buf.readBoolean();
            return packet;
        }


        public static void handle(ChunkLoaderPacket msg, Supplier<NetworkEvent.Context> ctx) {

            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity sender = ctx.get().getSender();
                if (sender.containerMenu instanceof ChunkLoaderContainer) {
                    ((ChunkLoaderContainer)sender.containerMenu).setEnabled(msg.set_enabled);
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }

    public static class PistonPushcartPacket {
        public boolean is_up_key;
        public boolean now_down;

        public PistonPushcartPacket(boolean is_up_key, boolean now_down) {
            this.is_up_key = is_up_key;
            this.now_down = now_down;
        }

        public static void encode(PistonPushcartPacket msg, PacketBuffer buf) {
            buf.writeBoolean(msg.is_up_key);
            buf.writeBoolean(msg.now_down);
        }

        public static PistonPushcartPacket decode(PacketBuffer buf) {
            PistonPushcartPacket packet = new PistonPushcartPacket(false, false);
            packet.is_up_key = buf.readBoolean();
            packet.now_down = buf.readBoolean();
            return packet;
        }


        public static void handle(PistonPushcartPacket msg, Supplier<NetworkEvent.Context> ctx) {

            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity sender = ctx.get().getSender();
                if (sender.getRootVehicle() instanceof PistonPushcartEntity) {
                    ((PistonPushcartEntity)sender.getRootVehicle()).setElevating(msg.is_up_key, msg.now_down);
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }
}
