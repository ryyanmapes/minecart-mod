package com.alc.moreminecarts.proxy;

import com.alc.moreminecarts.MoreMinecartsMod;
import com.alc.moreminecarts.containers.ChunkLoaderContainer;
import com.alc.moreminecarts.containers.FilterUnloaderContainer;
import com.alc.moreminecarts.containers.FlagCartContainer;
import com.alc.moreminecarts.containers.MinecartUnLoaderContainer;
import com.alc.moreminecarts.entities.CouplerEntity;
import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.alc.moreminecarts.tile_entities.FilterUnloaderTile;
import com.alc.moreminecarts.tile_entities.MinecartLoaderTile;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Filter;

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

        // For sending the piston pushcart inputs client -> server
        INSTANCE.registerMessage(id++,
                PistonPushcartPacket.class,
                PistonPushcartPacket::encode,
                PistonPushcartPacket::decode,
                PistonPushcartPacket::handle);

        // For sending long-range player interactions client -> server
        INSTANCE.registerMessage(id++,
                ExtendedInteractPacket.class,
                ExtendedInteractPacket::encode,
                ExtendedInteractPacket::decode,
                ExtendedInteractPacket::handle);

        // For changing the minecart loader and unloader client -> server
        INSTANCE.registerMessage(id++,
                MinecartLoaderPacket.class,
                MinecartLoaderPacket::encode,
                MinecartLoaderPacket::decode,
                MinecartLoaderPacket::handle);

        // For changing the flag cart via GUI client -> server
        INSTANCE.registerMessage(id++,
                FlagCartPacket.class,
                FlagCartPacket::encode,
                FlagCartPacket::decode,
                FlagCartPacket::handle);
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

    public static class ExtendedInteractPacket extends CUseEntityPacket {

        public ExtendedInteractPacket(){}

        @OnlyIn(Dist.CLIENT)
        public ExtendedInteractPacket(Entity p_i47098_1_, Hand p_i47098_2_, boolean p_i47098_4_) {
            super(p_i47098_1_, p_i47098_2_, p_i47098_4_);
        }

        public static void encode(ExtendedInteractPacket msg, PacketBuffer buf) {
            try {
                msg.write(buf);
            } catch (IOException e) {

            }
        }

        public static ExtendedInteractPacket decode(PacketBuffer buf) {
            ExtendedInteractPacket packet = new ExtendedInteractPacket();
            try {
                packet.read(buf);
            } catch (IOException e) {

            }
            return packet;
        }

        public static void handle(ExtendedInteractPacket msg, Supplier<NetworkEvent.Context> ctx) {

            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity sender = ctx.get().getSender();
                NetworkManager network = ctx.get().getNetworkManager();
                handleInteract(network, msg, sender);
            });
            ctx.get().setPacketHandled(true);
        }

    }

    // TAKEN FROM SERVERNETPLAYHANDLER
    // Works identically to normal interaction, except only when the distance is too far to be considered by
    // the vanilla failsafe. New max distance is 100.
    public static void handleInteract(NetworkManager network, CUseEntityPacket p_147340_1_, ServerPlayerEntity player) {
        ServerWorld serverworld = player.getLevel();
        Entity entity = p_147340_1_.getTarget(serverworld);
        player.resetLastActionTime();
        player.setShiftKeyDown(p_147340_1_.isUsingSecondaryAction());
        if (entity != null) {
            double d0 = player.distanceToSqr(entity);
            if (d0 >= 36.0D && d0 < 175.0) {
                Hand hand = p_147340_1_.getHand();
                ItemStack itemstack = hand != null ? player.getItemInHand(hand).copy() : ItemStack.EMPTY;
                Optional<ActionResultType> optional = Optional.empty();
                if (p_147340_1_.getAction() == CUseEntityPacket.Action.INTERACT) {
                    optional = Optional.of(player.interactOn(entity, hand));
                } else if (p_147340_1_.getAction() == CUseEntityPacket.Action.INTERACT_AT) {
                    if (net.minecraftforge.common.ForgeHooks.onInteractEntityAt(player, entity, p_147340_1_.getLocation(), hand) != null)
                        return;
                    optional = Optional.of(entity.interactAt(player, p_147340_1_.getLocation(), hand));
                } else if (p_147340_1_.getAction() == CUseEntityPacket.Action.ATTACK) {
                    if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof AbstractArrowEntity || entity == player) {
                        network.disconnect(new TranslationTextComponent("multiplayer.disconnect.invalid_entity_attacked"));
                        //LOGGER.warn("Player {} tried to attack an invalid entity", (Object) this.player.getName().getString());
                        return;
                    }

                    player.attack(entity);
                }

                if (optional.isPresent() && optional.get().consumesAction()) {
                    CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(player, itemstack, entity);
                    if (optional.get().shouldSwing()) {
                        player.swing(hand, true);
                    }
                }
            }
        }
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

        public static void encode(MinecartLoaderPacket msg, PacketBuffer buf) {
            buf.writeBoolean(msg.is_unloader);
            buf.writeBoolean(msg.locked_minecarts_only);
            buf.writeBoolean(msg.leave_one_item_in_stack);
            buf.writeEnum(msg.output_type);
            buf.writeBoolean(msg.redstone_output);
            buf.writeEnum(msg.filterType);
        }

        public static MinecartLoaderPacket decode(PacketBuffer buf) {
            MinecartLoaderPacket packet = new MinecartLoaderPacket();
            packet.is_unloader = buf.readBoolean();
            packet.locked_minecarts_only = buf.readBoolean();
            packet.leave_one_item_in_stack = buf.readBoolean();
            packet.output_type = buf.readEnum(MinecartLoaderTile.ComparatorOutputType.class);
            packet.redstone_output = buf.readBoolean();
            packet.filterType = buf.readEnum(FilterUnloaderTile.FilterType.class);
            return packet;
        }


        public static void handle(MinecartLoaderPacket msg, Supplier<NetworkEvent.Context> ctx) {

            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity sender = ctx.get().getSender();
                if (sender.containerMenu instanceof MinecartUnLoaderContainer) {
                    MinecartUnLoaderContainer container = (MinecartUnLoaderContainer) sender.containerMenu;
                    container.setOptions(msg.locked_minecarts_only, msg.leave_one_item_in_stack, msg.output_type, msg.redstone_output, msg.filterType);
                }
                else if (sender.containerMenu instanceof FilterUnloaderContainer) {
                    FilterUnloaderContainer container = (FilterUnloaderContainer) sender.containerMenu;
                    container.setOptions(msg.locked_minecarts_only, msg.leave_one_item_in_stack, msg.output_type, msg.redstone_output, msg.filterType);
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

        public static void encode(FlagCartPacket msg, PacketBuffer buf) {
            buf.writeBoolean(msg.is_decrement);
            buf.writeBoolean(msg.is_disclude);
        }

        public static FlagCartPacket decode(PacketBuffer buf) {
            FlagCartPacket packet = new FlagCartPacket(false, false);
            packet.is_decrement = buf.readBoolean();
            packet.is_disclude = buf.readBoolean();
            return packet;
        }


        public static void handle(FlagCartPacket msg, Supplier<NetworkEvent.Context> ctx) {

            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity sender = ctx.get().getSender();
                if (sender.containerMenu instanceof FlagCartContainer) {
                    ((FlagCartContainer)sender.containerMenu).changeSelection(msg.is_decrement, msg.is_disclude);
                }
            });
            ctx.get().setPacketHandled(true);
        }

    }
}


