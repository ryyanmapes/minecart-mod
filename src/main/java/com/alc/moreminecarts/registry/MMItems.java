package com.alc.moreminecarts.registry;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.MoreMinecartsMod;
import com.alc.moreminecarts.items.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MMItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MMConstants.modid);

    public static Item.Properties modItem() {
        return new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB);
    }

    // Rail Items
    public static final RegistryObject<Item> RAIL_TURN_ITEM = ITEMS.register("rail_turn", () -> new BlockItem(MMBlocks.RAIL_TURN.get(), modItem()));
    public static final RegistryObject<Item> PARALLEL_RAIL_ITEM = ITEMS.register("parallel_rail", () -> new BlockItem(MMBlocks.PARALLEL_RAIL_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> CROSS_RAIL_ITEM = ITEMS.register("cross_rail", () -> new BlockItem(MMBlocks.CROSS_RAIL_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> PROJECTOR_RAIL_ITEM = ITEMS.register("projector_rail", () -> new BlockItem(MMBlocks.PROJECTOR_RAIL.get(), modItem()));
    public static final RegistryObject<Item> WOODEN_RAIL_ITEM = ITEMS.register("wooden_rail", () -> new BlockItem(MMBlocks.WOODEN_RAIL_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> WOODEN_RAIL_TURN_ITEM = ITEMS.register("wooden_rail_turn", () -> new BlockItem(MMBlocks.WOODEN_RAIL_TURN.get(), modItem()));
    public static final RegistryObject<Item> WOODEN_PARALLEL_RAIL_ITEM = ITEMS.register("wooden_parallel_rail", () -> new BlockItem(MMBlocks.WOODEN_PARALLEL_RAIL_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> WOODEN_CROSS_RAIL_ITEM = ITEMS.register("wooden_cross_rail", () -> new BlockItem(MMBlocks.WOODEN_CROSS_RAIL_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> WOODEN_PROJECTOR_RAIL_ITEM = ITEMS.register("wooden_projector_rail", () -> new BlockItem(MMBlocks.WOODEN_PROJECTOR_RAIL.get(), modItem()));
    public static final RegistryObject<Item> MAGLEV_RAIL_ITEM = ITEMS.register("maglev_rail", () -> new BlockItem(MMBlocks.MAGLEV_RAIL_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> MAGLEV_RAIL_TURN_ITEM = ITEMS.register("maglev_rail_turn", () -> new BlockItem(MMBlocks.MAGLEV_RAIL_TURN.get(), modItem()));
    public static final RegistryObject<Item> MAGLEV_PARALLEL_RAIL_ITEM = ITEMS.register("maglev_parallel_rail", () -> new BlockItem(MMBlocks.MAGLEV_PARALLEL_RAIL_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> MAGLEV_CROSS_RAIL = ITEMS.register("maglev_cross_rail", () -> new BlockItem(MMBlocks.MAGLEV_CROSS_RAIL_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> MAGLEV_PROJECTOR_RAIL_ITEM = ITEMS.register("maglev_projector_rail", () -> new BlockItem(MMBlocks.MAGLEV_PROJECTOR_RAIL.get(), modItem()));
    public static final RegistryObject<Item> MAGLEV_POWERED_RAIL_ITEM = ITEMS.register("maglev_powered_rail", () -> new BlockItem(MMBlocks.MAGLEV_POWERED_RAIL_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> LIGHTSPEED_RAIL_ITEM = ITEMS.register("lightspeed_rail", () -> new BlockItem(MMBlocks.LIGHTSPEED_RAIL_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> LIGHTSPEED_CROSS_RAIL_ITEM = ITEMS.register("lightspeed_cross_rail", () -> new BlockItem(MMBlocks.LIGHTSPEED_CROSS_RAIL_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> LIGHTSPEED_POWERED_RAIL_ITEM = ITEMS.register("lightspeed_powered_rail", () -> new BlockItem(MMBlocks.LIGHTSPEED_POWERED_RAIL_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> BIOLUMINESCENT_RAIL_ITEM = ITEMS.register("bioluminescent_rail", () -> new BlockItem(MMBlocks.BIOLUMINESCENT_RAIL_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> LOCKING_RAIL_ITEM = ITEMS.register("locking_rail", () -> new BlockItem(MMBlocks.LOCKING_RAIL_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> POWERED_LOCKING_RAIL_ITEM = ITEMS.register("powered_locking_rail", () -> new BlockItem(MMBlocks.POWERED_LOCKING_RAIL_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> PISTON_LIFTER_RAIL_ITEM = ITEMS.register("piston_lifter_rail", () -> new BlockItem(MMBlocks.PISTON_LIFTER_RAIL.get(), modItem()));
    public static final RegistryObject<Item> ARITHMETIC_RAIL_ITEM = ITEMS.register("arithmetic_rail", () -> new BlockItem(MMBlocks.ARITHMETIC_RAIL.get(), modItem()));

    // Minecart Items
    public static final RegistryObject<Item> MINECART_WITH_NET_ITEM = ITEMS.register("minecart_with_net", () -> new MinecartWithNetItem(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> MINECART_WITH_CHUNK_LOADER_ITEM = ITEMS.register("minecart_with_chunk_loader", () -> new ChunkLoaderCartItem(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> MINECART_WITH_STASIS_ITEM = ITEMS.register("pearl_stasis_minecart", () -> new OrbStasisCartItem(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> FLAG_CART_ITEM = ITEMS.register("flag_cart", () -> new FlagCartItem(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> CAMPFIRE_CART_ITEM = ITEMS.register("campfire_cart", () -> new CampfireCartItem(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> SOULFIRE_CART_ITEM = ITEMS.register("soulfire_cart", () -> new SoulfireCartItem(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));
    private static RegistryObject<Item> ENDFIRE_CART_ITEM;
    public static final RegistryObject<Item> WOODEN_PUSHCART_ITEM = ITEMS.register("wooden_pushcart", () -> new WoodenPushcartItem(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> IRON_PUSHCART_ITEM = ITEMS.register("iron_pushcart", () -> new IronPushcartItem(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> PISTON_PUSHCART_ITEM = ITEMS.register("piston_pushcart", () -> new PistonPushcartItem(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> STICKY_PISTON_PUSHCART_ITEM = ITEMS.register("sticky_piston_pushcart", () -> new StickyPistonPushcartItem(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> TANK_CART_ITEM = ITEMS.register("tank_cart", () -> new TankCartItem(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> BATTERY_CART_ITEM = ITEMS.register("battery_cart", () -> new BatteryCartItem(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));

    // Block Items
    public static final RegistryObject<Item> SILICA_STEEL_BLOCK_ITEM = ITEMS.register("silica_steel_block", () -> new BlockItem(MMBlocks.SILICA_STEEL_BLOCK.get(), new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> CHUNKRODITE_BLOCK_ITEM = ITEMS.register("chunkrodite_block", () -> new BlockItem(MMBlocks.CHUNKRODITE_BLOCK.get(), new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> CORRUGATED_SILICA_STEEL_ITEM = ITEMS.register("corrugated_silica_steel", () -> new BlockItem(MMBlocks.CORRUGATED_SILICA_STEEL.get(), new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> SILICA_STEEL_PILLAR_ITEM = ITEMS.register("silica_steel_pillar", () -> new BlockItem(MMBlocks.SILICA_STEEL_PILLAR.get(), new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> ORGANIC_GLASS_ITEM = ITEMS.register("organic_glass", () -> new BlockItem(MMBlocks.ORGANIC_GLASS.get(), new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> ORGANIC_GLASS_PANE_ITEM = ITEMS.register("organic_glass_pane", () -> new BlockItem(MMBlocks.ORGANIC_GLASS_PANE.get(), new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> CHISELED_ORGANIC_GLASS_ITEM = ITEMS.register("chiseled_organic_glass", () -> new BlockItem(MMBlocks.CHISELED_ORGANIC_GLASS.get(), new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> CHISELED_ORGANIC_GLASS_PANE_ITEM = ITEMS.register("chiseled_organic_glass_pane", () -> new BlockItem(MMBlocks.CHISELED_ORGANIC_GLASS_PANE.get(), new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> GLASS_CACTUS_ITEM = ITEMS.register("glass_cactus", () -> new GlassCactusItem(MMBlocks.GLASS_CACTUS.get(), new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> HOLO_SCAFFOLD_GENERATOR_ITEM = ITEMS.register("holo_scaffold_generator", () -> new BlockItem(MMBlocks.HOLO_SCAFFOLD_GENERATOR.get(), new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> CHUNK_LOADER_ITEM = ITEMS.register("chunk_loader", () -> new BlockItem(MMBlocks.CHUNK_LOADER_BLOCK.get(), new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> MINECART_LOADER_ITEM = ITEMS.register("minecart_loader", () -> new BlockItem(MMBlocks.MINECART_LOADER_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> MINECART_UNLOADER_ITEM = ITEMS.register("minecart_unloader", () -> new BlockItem(MMBlocks.MINECART_UNLOADER_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> FILTER_UNLOADER_ITEM = ITEMS.register("filter_unloader", () -> new BlockItem(MMBlocks.FILTER_UNLOADER_BLOCK.get(), modItem()));
    public static final RegistryObject<Item> PEARL_STASIS_CHAMBER_ITEM = ITEMS.register("pearl_stasis_chamber", () -> new BlockItem(MMBlocks.PEARL_STASIS_CHAMBER.get(), new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));

    // Misc Items
    public static final RegistryObject<Item> COUPLER_ITEM = ITEMS.register("coupler", () -> new CouplerItem(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> HIGH_SPEED_UPGRADE_ITEM = ITEMS.register("high_speed_upgrade", () -> new Item(modItem()));
    public static final RegistryObject<HoloRemoteItem> HOLO_REMOTE_ITEM = ITEMS.register("holo_remote", () -> new HoloRemoteItem(HoloRemoteItem.HoloRemoteType.regular, new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<HoloRemoteItem> BACKWARDS_HOLO_REMOTE_ITEM = ITEMS.register("backwards_holo_remote", () -> new HoloRemoteItem(HoloRemoteItem.HoloRemoteType.backwards, new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<HoloRemoteItem> SIMPLE_HOLO_REMOTE_ITEM = ITEMS.register("simple_holo_remote", () -> new HoloRemoteItem(HoloRemoteItem.HoloRemoteType.simple, new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<HoloRemoteItem> BROKEN_HOLO_REMOTE_ITEM = ITEMS.register("broken_holo_remote", () -> new HoloRemoteItem(HoloRemoteItem.HoloRemoteType.broken, new Item.Properties().tab(MoreMinecartsMod.CREATIVE_TAB)));

    // Rail Signal Items
    public static final BiMap<DyeColor, RegistryObject<Item>> RAIL_SIGNALS = HashBiMap.create();
    static {
        for (DyeColor color : DyeColor.values()) {
            RegistryObject<Item> entry = ITEMS.register("rail_signal_" + color.getName(), () -> new Item(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));
            RAIL_SIGNALS.put(color, entry);
        }
    }

    // Color Detector Rail Items
    static {
        for (DyeColor color : DyeColor.values()) {
            ITEMS.register("color_detector_rail_" + color.getName(), () -> new BlockItem(MMBlocks.COLOR_DETECTOR_RAILS.get(color).get(), modItem()));
        }
    }

    // Material Items
    public static final RegistryObject<Item> LEVITATION_POWDER = ITEMS.register("levitation_powder", () -> new Item(new Item.Properties().stacksTo(64).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> SILICA_STEEL_MIX = ITEMS.register("silica_steel_mix", () -> new Item(new Item.Properties().stacksTo(64).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> SILICA_STEEL = ITEMS.register("silica_steel", () -> new Item(new Item.Properties().stacksTo(64).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> CHUNKRODITE = ITEMS.register("chunkrodite", () -> new Item(new Item.Properties().stacksTo(64).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> HARD_LIGHT_LENS = ITEMS.register("hard_light_lens", () -> new Item(new Item.Properties().stacksTo(64).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> GLASS_SPINES = ITEMS.register("glass_spines", () -> new Item(new Item.Properties().stacksTo(64).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> TRANSPORT_TANK = ITEMS.register("transport_tank", () -> new Item(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> TRANSPORT_BATTERY = ITEMS.register("transport_battery", () -> new Item(new Item.Properties().stacksTo(1).tab(MoreMinecartsMod.CREATIVE_TAB)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
