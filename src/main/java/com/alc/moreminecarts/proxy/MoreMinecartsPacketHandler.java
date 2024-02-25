package com.alc.moreminecarts.proxy;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.MoreMinecartsMod;
import com.alc.moreminecarts.containers.ChunkLoaderContainer;
import com.alc.moreminecarts.containers.FilterUnloaderContainer;
import com.alc.moreminecarts.containers.FlagCartContainer;
import com.alc.moreminecarts.containers.MinecartUnLoaderContainer;
import com.alc.moreminecarts.entities.CouplerEntity;
import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.alc.moreminecarts.tile_entities.FilterUnloaderTile;
import com.alc.moreminecarts.tile_entities.MinecartLoaderTile;
import io.netty.buffer.Unpooled;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Filter;

public class MoreMinecartsPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
        .named(new ResourceLocation(MMConstants.modid, "channelname"))
        .clientAcceptedVersions(a->true)
        .serverAcceptedVersions(a->true)
        .networkProtocolVersion(() -> PROTOCOL_VERSION)
        .simpleChannel();

    public static void Init() {

        int id = 0;

        // For syncing the coupler to the server -> client
        INSTANCE.messageBuilder(CouplePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CouplePacket::encode)
                .decoder(CouplePacket::decode)
                .consumerMainThread(CouplePacket::handle)
                .add();

        // For changing the chunk loader client -> server
        INSTANCE.messageBuilder(ChunkLoaderPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ChunkLoaderPacket::encode)
                .decoder(ChunkLoaderPacket::decode)
                .consumerMainThread(ChunkLoaderPacket::handle)
                .add();

        // For sending the piston pushcart inputs client -> server
        INSTANCE.messageBuilder(PistonPushcartPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(PistonPushcartPacket::encode)
                .decoder(PistonPushcartPacket::decode)
                .consumerMainThread(PistonPushcartPacket::handle)
                .add();

        // For sending long-range player interactions client -> server
        INSTANCE.messageBuilder(ExtendedInteractPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ExtendedInteractPacket::encode)
                .decoder(ExtendedInteractPacket::decode)
                .consumerMainThread(ExtendedInteractPacket::handle)
                .add();

        // For changing the minecart loader and unloader client -> server
        INSTANCE.messageBuilder(MinecartLoaderPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(MinecartLoaderPacket::encode)
                .decoder(MinecartLoaderPacket::decode)
                .consumerMainThread(MinecartLoaderPacket::handle)
                .add();

        // For changing the flag cart via GUI client -> server
        INSTANCE.messageBuilder(FlagCartPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(FlagCartPacket::encode)
                .decoder(FlagCartPacket::decode)
                .consumerMainThread(FlagCartPacket::handle)
                .add();
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

        public static void encode(CouplePacket msg, FriendlyByteBuf buf) {
            buf.writeInt(msg.v1);
            buf.writeInt(msg.v2);
            buf.writeInt(msg.coupler_id);
        }

        public static CouplePacket decode(FriendlyByteBuf buf) {
            CouplePacket packet = new CouplePacket(0,0,0);
            packet.v1 = buf.readInt();
            packet.v2 = buf.readInt();
            packet.coupler_id = buf.readInt();
            return packet;
        }

        public void handle(Supplier<NetworkEvent.Context> ctx) {

            MoreMinecartsMod.LOGGER.log(org.apache.logging.log4j.Level.WARN, "HERE!!!");

            ctx.get().enqueueWork(() -> {

                Level world = MoreMinecartsMod.PROXY.getWorld();

                Entity ent = world.getEntity(coupler_id);
                if (ent != null && ent instanceof CouplerEntity) {
                    CouplerEntity coupler_ent = (CouplerEntity) ent;
                    coupler_ent.vehicle1_id = v1;
                    coupler_ent.vehicle2_id = v2;
                    MoreMinecartsMod.LOGGER.log(org.apache.logging.log4j.Level.WARN, "HERE2!!!");
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

        public static void encode(ChunkLoaderPacket msg, FriendlyByteBuf buf) {
            buf.writeBoolean(msg.set_enabled);
        }

        public static ChunkLoaderPacket decode(FriendlyByteBuf buf) {
            ChunkLoaderPacket packet = new ChunkLoaderPacket(false);
            packet.set_enabled = buf.readBoolean();
            return packet;
        }

        public void handle(Supplier<NetworkEvent.Context> ctx) {
            //LogManager.getLogger().info("HERE!!!");
            ctx.get().enqueueWork(() -> {
                ServerPlayer sender = ctx.get().getSender();
                if (sender.containerMenu instanceof ChunkLoaderContainer) {
                    ((ChunkLoaderContainer)sender.containerMenu).setEnabled(set_enabled);
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

        public static void encode(PistonPushcartPacket msg, FriendlyByteBuf buf) {
            buf.writeBoolean(msg.is_up_key);
            buf.writeBoolean(msg.now_down);
        }

        public static PistonPushcartPacket decode(FriendlyByteBuf buf) {
            PistonPushcartPacket packet = new PistonPushcartPacket(false, false);
            packet.is_up_key = buf.readBoolean();
            packet.now_down = buf.readBoolean();
            return packet;
        }

        public void handle(Supplier<NetworkEvent.Context> ctx) {

            ctx.get().enqueueWork(() -> {
                ServerPlayer sender = ctx.get().getSender();
                if (sender.getRootVehicle() instanceof PistonPushcartEntity) {
                    ((PistonPushcartEntity)sender.getRootVehicle()).setElevating(is_up_key, now_down);
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }

    public static enum FakeInteraction {
        INTERACTION
    }

    public static class ExtendedInteractPacket extends ServerboundInteractPacket {

        public ExtendedInteractPacket(FriendlyByteBuf p_179602_) {
            super(p_179602_);
        }

        @OnlyIn(Dist.CLIENT)
        public static ExtendedInteractPacket createExtendedInteractPacket(Entity p_179609_, boolean p_179610_, InteractionHand p_179611_) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

            buf.writeVarInt(p_179609_.getId());
            buf.writeEnum(FakeInteraction.INTERACTION);
            buf.writeEnum(p_179611_);
            buf.writeBoolean(p_179610_);

            return new ExtendedInteractPacket(buf);
        }

        public static void encode(ExtendedInteractPacket msg, FriendlyByteBuf buf) {
            msg.write(buf);
        }

        public static ExtendedInteractPacket decode(FriendlyByteBuf buf) {
            ExtendedInteractPacket interact = new ExtendedInteractPacket(buf);
            return interact;
        }

        public void handle(Supplier<NetworkEvent.Context> ctx) {

            // MoreMinecartsMod.LOGGER.log(org.apache.logging.log4j.Level.WARN, "PISTON PUSHCART INTERACT 3");
            ctx.get().enqueueWork(() -> {
                ServerPlayer sender = ctx.get().getSender();
                handleInteract(this, sender, ctx.get().getNetworkManager());
            });
            ctx.get().setPacketHandled(true);
        }

    }

    // TAKEN FROM SERVERNETPLAYHANDLER
    // Works identically to normal interaction, except only when the distance is too far to be considered by
    // the vanilla failsafe. New max distance is 100.
    public static void handleInteract(ServerboundInteractPacket p_9866_, ServerPlayer player, Connection connection) {
        // hopefully not important?
        //PacketUtils.ensureRunningOnSameThread(p_9866_, this, player.getLevel());

        ServerLevel serverlevel = player.serverLevel();
        final Entity entity = p_9866_.getTarget(serverlevel);
        player.resetLastActionTime();
        player.setShiftKeyDown(p_9866_.isUsingSecondaryAction());
        if (entity != null) {
            if (!serverlevel.getWorldBorder().isWithinBounds(entity.blockPosition())) {
                return;
            }

            double d0 = 36.0D;
            if (player.distanceToSqr(entity) >= 36.0D && player.distanceToSqr(entity) < 100) {
                MoreMinecartsMod.LOGGER.log(org.apache.logging.log4j.Level.WARN, "PISTON PUSHCART INTERACT 4");
                p_9866_.dispatch(new ServerboundInteractPacket.Handler() {
                    private void performInteraction(InteractionHand p_143679_, EntityInteraction p_143680_) {
                        ItemStack itemstack = player.getItemInHand(p_143679_).copy();
                        InteractionResult interactionresult = p_143680_.run(player, entity, p_143679_);
                        if (net.minecraftforge.common.ForgeHooks.onInteractEntityAt(player, entity, entity.position(), p_143679_) != null) return;
                        if (interactionresult.consumesAction()) {
                            CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(player, itemstack, entity);
                            if (interactionresult.shouldSwing()) {
                                player.swing(p_143679_, true);
                            }
                        }

                    }

                    public void onInteraction(InteractionHand p_143677_) {
                        this.performInteraction(p_143677_, Player::interactOn);
                    }

                    public void onInteraction(InteractionHand p_143682_, Vec3 p_143683_) {
                        this.performInteraction(p_143682_, (p_143686_, p_143687_, p_143688_) -> {
                            return p_143687_.interactAt(p_143686_, p_143683_, p_143688_);
                        });
                    }

                    public void onAttack() {
                        if (!(entity instanceof ItemEntity) && !(entity instanceof ExperienceOrb) && !(entity instanceof AbstractArrow) && entity != player) {
                            player.attack(entity);
                        } else {
                            disconnect(Component.translatable("multiplayer.disconnect.invalid_entity_attacked"), connection);
                            //ServerGamePacketListenerImpl.LOGGER.warn("Player {} tried to attack an invalid entity", (Object)ServerGamePacketListenerImpl.this.player.getName().getString());
                        }
                    }
                });
            }
        }
    }

    public static void disconnect(Component comp, Connection connection) {
        connection.send(new ClientboundDisconnectPacket(comp), new ConnectionPacketListener(comp, connection));
        connection.setReadOnly();
        connection.handleDisconnection();
    }

    public static class ConnectionPacketListener implements PacketSendListener {

        Component comp;
        Connection connection;

        public ConnectionPacketListener(Component comp, Connection connection) {
            this.comp = comp;
            this.connection = connection;
        }

        @Override
        public void onSuccess() {
            connection.disconnect(comp);
            PacketSendListener.super.onSuccess();
        }

    }

    @FunctionalInterface
    interface EntityInteraction {
        InteractionResult run(ServerPlayer p_143695_, Entity p_143696_, InteractionHand p_143697_);
    }


    public static class MinecartLoaderPacket {
        public boolean is_unloader;
        public boolean locked_minecarts_only;
        public boolean leave_one_item_in_stack;
        public boolean redstone_output;
        public MinecartLoaderTile.ComparatorOutputType output_type;
        public FilterUnloaderTile.FilterType filterType;

        public MinecartLoaderPacket(){}

        public MinecartLoaderPacket(boolean is_unloader, boolean locked_minecarts_only, boolean leave_one_item_in_stack,
                                    MinecartLoaderTile.ComparatorOutputType output_type, boolean redstone_output,
                                    FilterUnloaderTile.FilterType filterType) {
            this.is_unloader = is_unloader;
            this.locked_minecarts_only = locked_minecarts_only;
            this.leave_one_item_in_stack = leave_one_item_in_stack;
            this.output_type = output_type;
            this.redstone_output = redstone_output;
            this.filterType = filterType;
        }

        public static void encode(MinecartLoaderPacket msg, FriendlyByteBuf buf) {
            buf.writeBoolean(msg.is_unloader);
            buf.writeBoolean(msg.locked_minecarts_only);
            buf.writeBoolean(msg.leave_one_item_in_stack);
            buf.writeEnum(msg.output_type);
            buf.writeBoolean(msg.redstone_output);
            buf.writeEnum(msg.filterType);
        }

        public static MinecartLoaderPacket decode(FriendlyByteBuf buf) {
            MinecartLoaderPacket packet = new MinecartLoaderPacket();
            packet.is_unloader = buf.readBoolean();
            packet.locked_minecarts_only = buf.readBoolean();
            packet.leave_one_item_in_stack = buf.readBoolean();
            packet.output_type = buf.readEnum(MinecartLoaderTile.ComparatorOutputType.class);
            packet.redstone_output = buf.readBoolean();
            packet.filterType = buf.readEnum(FilterUnloaderTile.FilterType.class);
            return packet;
        }

        public void handle(Supplier<NetworkEvent.Context> ctx) {

            ctx.get().enqueueWork(() -> {
                ServerPlayer sender = ctx.get().getSender();
                if (sender.containerMenu instanceof MinecartUnLoaderContainer container) {
                    container.setOptions(locked_minecarts_only, leave_one_item_in_stack, output_type, redstone_output, filterType);
                }
                else if (sender.containerMenu instanceof FilterUnloaderContainer container) {
                    container.setOptions(locked_minecarts_only, leave_one_item_in_stack, output_type, redstone_output, filterType);
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }

    public static class FlagCartPacket {
        public boolean is_decrement;
        public boolean is_disclude;

        public FlagCartPacket(boolean is_decrement, boolean is_disclude) {
            this.is_decrement = is_decrement;
            this.is_disclude = is_disclude;
        }

        public static void encode(FlagCartPacket msg, FriendlyByteBuf buf) {
            buf.writeBoolean(msg.is_decrement);
            buf.writeBoolean(msg.is_disclude);
        }

        public static FlagCartPacket decode(FriendlyByteBuf buf) {
            FlagCartPacket packet = new FlagCartPacket(false, false);
            packet.is_decrement = buf.readBoolean();
            packet.is_disclude = buf.readBoolean();
            return packet;
        }


        public static void handle(FlagCartPacket msg, NetworkEvent.Context ctx) {

            ctx.enqueueWork(() -> {
                ServerPlayer sender = ctx.getSender();
                if (sender.containerMenu instanceof FlagCartContainer) {
                    ((FlagCartContainer)sender.containerMenu).changeSelection(msg.is_decrement, msg.is_disclude);
                }
            });
            ctx.setPacketHandled(true);
        }

        public void handle(Supplier<NetworkEvent.Context> ctx) {

            ctx.get().enqueueWork(() -> {
                ServerPlayer sender = ctx.get().getSender();
                if (sender.containerMenu instanceof FlagCartContainer) {
                    ((FlagCartContainer)sender.containerMenu).changeSelection(is_decrement, is_disclude);
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }
}


