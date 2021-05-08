package com.alc.moreminecarts;

import com.alc.moreminecarts.blocks.*;
import com.alc.moreminecarts.containers.ChunkLoaderContainer;
import com.alc.moreminecarts.entities.*;
import com.alc.moreminecarts.entities.HSMinecartEntities.*;
import com.alc.moreminecarts.guis.ChunkLoaderScreen;
import com.alc.moreminecarts.items.*;
import com.alc.moreminecarts.misc.CouplerClientFactory;
import com.alc.moreminecarts.misc.MoreMinecartsPacketHandler;
import com.alc.moreminecarts.renderers.*;
import com.alc.moreminecarts.tile_entities.ChunkLoaderTile;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraft.block.AbstractBlock.Properties.of;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("moreminecarts")
@ObjectHolder("moreminecarts")
public class MoreMinecartsMod
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String MODID = "moreminecarts";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

    // Entities
    private static final RegistryObject<EntityType<MinecartWithNet>> MINECART_WITH_NET_ENTITY = ENTITIES.register("minecart_with_net", () -> EntityType.Builder.<MinecartWithNet>of(MinecartWithNet::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("minecart_with_net"));
    private static final RegistryObject<EntityType<ChunkLoaderCartEntity>> CHUNK_LOADER_CART = ENTITIES.register("minecart_with_chunk_loader", () -> EntityType.Builder.<ChunkLoaderCartEntity>of(ChunkLoaderCartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("minecart_with_chunk_loader"));
    private static final RegistryObject<EntityType<CampfireCartEntity>> CAMPFIRE_CART_ENTITY = ENTITIES.register("campfire_cart", () -> EntityType.Builder.<CampfireCartEntity>of(CampfireCartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("campfire_cart"));
    private static final RegistryObject<EntityType<SoulfireCartEntity>> SOULFIRE_CART_ENTITY = ENTITIES.register("soulfire_cart", () -> EntityType.Builder.<SoulfireCartEntity>of(SoulfireCartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("soulfire_cart"));
    private static final RegistryObject<EntityType<WoodenPushcartEntity>> WOODEN_PUSHCART_ENTITY = ENTITIES.register("wooden_pushcart", () -> EntityType.Builder.<WoodenPushcartEntity>of(WoodenPushcartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("wooden_pushcart"));
    private static final RegistryObject<EntityType<IronPushcartEntity>> IRON_PUSHCART_ENTITY = ENTITIES.register("iron_pushcart", () -> EntityType.Builder.<IronPushcartEntity>of(IronPushcartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("iron_pushcart"));
    private static final RegistryObject<EntityType<CouplerEntity>> COUPLER_ENTITY = ENTITIES.register("coupler", () -> EntityType.Builder.<CouplerEntity>of(CouplerEntity::new, EntityClassification.MISC ).sized(0.3F, 0.3F).noSummon().setCustomClientFactory(CouplerClientFactory.get()).build("coupler"));

    public static final EntityType<WoodenPushcartEntity> minecart_with_net = null;
    public static final EntityType<WoodenPushcartEntity> minecart_with_chunk_loader = null;
    public static final EntityType<CampfireCartEntity> campfire_cart = null;
    public static final EntityType<CampfireCartEntity> soulfire_cart = null;
    public static final EntityType<WoodenPushcartEntity> wooden_pushcart = null;
    public static final EntityType<IronPushcartEntity> iron_pushcart = null;
    public static final EntityType<CouplerEntity> coupler = null;

    // High Speed Carts
    private static final RegistryObject<EntityType<HSMinecart>> HS_CART_ENTITY = ENTITIES.register("high_speed_minecart", () -> EntityType.Builder.<HSMinecart>of(HSMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_minecart"));
    private static final RegistryObject<EntityType<HSChestMinecart>> HS_CHEST_CART_ENTITY = ENTITIES.register("high_speed_chest_minecart", () -> EntityType.Builder.<HSChestMinecart>of(HSChestMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_chest_minecart"));
    private static final RegistryObject<EntityType<HSTNTMinecart>> HS_TNT_CART_ENTITY = ENTITIES.register("high_speed_tnt_minecart", () -> EntityType.Builder.<HSTNTMinecart>of(HSTNTMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_tnt_minecart"));
    private static final RegistryObject<EntityType<HSCommandBlockMinecart>> HS_COMMAND_BLOCK_CART_ENTITY = ENTITIES.register("high_speed_command_block_minecart", () -> EntityType.Builder.<HSCommandBlockMinecart>of(HSCommandBlockMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_command_block_minecart"));
    private static final RegistryObject<EntityType<HSHopperMinecart>> HS_HOPPER_CART_ENTITY = ENTITIES.register("high_speed_hopper_minecart", () -> EntityType.Builder.<HSHopperMinecart>of(HSHopperMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_hopper_minecart"));
    private static final RegistryObject<EntityType<HighSpeedSpawnerMinecart>> HS_SPAWNER_CART_ENTITY = ENTITIES.register("high_speed_spawner_minecart", () -> EntityType.Builder.<HighSpeedSpawnerMinecart>of(HighSpeedSpawnerMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_spawner_minecart"));

    public static final EntityType<HSMinecart> high_speed_minecart = null;
    public static final EntityType<HSChestMinecart> high_speed_chest_minecart = null;
    public static final EntityType<HSTNTMinecart> high_speed_tnt_minecart = null;
    public static final EntityType<HSCommandBlockMinecart> high_speed_command_block_minecart = null;
    public static final EntityType<HSHopperMinecart> high_speed_hopper_minecart = null;
    public static final EntityType<HighSpeedSpawnerMinecart> high_speed_spawner_minecart = null;

    // Blocks
    private static final RegistryObject<Block> RAIL_TURN = BLOCKS.register("rail_turn", () -> new RailTurn(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> PARALLEL_RAIL_BLOCK = BLOCKS.register("parallel_rail", () -> new ParallelRail(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> WOODEN_RAIL_BLOCK = BLOCKS.register("wooden_rail", () -> new WoodenRail(of(Material.WOOD, MaterialColor.WOOD).noCollission().strength(0.7F).sound(SoundType.BAMBOO)));
    private static final RegistryObject<Block> WOODEN_RAIL_TURN = BLOCKS.register("wooden_rail_turn", () -> new WoodenRailTurn(of(Material.WOOD, MaterialColor.WOOD).noCollission().strength(0.7F).sound(SoundType.BAMBOO)));
    private static final RegistryObject<Block> WOODEN_PARALLEL_RAIL_BLOCK = BLOCKS.register("wooden_parallel_rail", () -> new WoodenParallelRail(of(Material.WOOD, MaterialColor.WOOD).noCollission().strength(0.7F).sound(SoundType.BAMBOO)));
    private static final RegistryObject<Block> MAGLEV_RAIL_BLOCK = BLOCKS.register("maglev_rail", () -> new MaglevRail(of(Material.HEAVY_METAL, MaterialColor.COLOR_BLUE).noCollission().strength(0.7F).sound(SoundType.GILDED_BLACKSTONE)));
    private static final RegistryObject<Block> MAGLEV_RAIL_TURN = BLOCKS.register("maglev_rail_turn", () -> new MaglevRailTurn(of(Material.HEAVY_METAL, MaterialColor.COLOR_BLUE).noCollission().strength(0.7F).sound(SoundType.GILDED_BLACKSTONE)));
    private static final RegistryObject<Block> MAGLEV_PARALLEL_RAIL_BLOCK = BLOCKS.register("maglev_parallel_rail", () -> new MaglevParallelRail(of(Material.HEAVY_METAL, MaterialColor.COLOR_BLUE).noCollission().strength(0.7F).sound(SoundType.GILDED_BLACKSTONE)));
    private static final RegistryObject<Block> MAGLEV_POWERED_RAIL_BLOCK = BLOCKS.register("maglev_powered_rail", () -> new PoweredMaglevRail(of(Material.HEAVY_METAL, MaterialColor.COLOR_BLUE).noCollission().strength(0.7F).sound(SoundType.GILDED_BLACKSTONE)));
    private static final RegistryObject<Block> BIOLUMINESCENT_RAIL_BLOCK = BLOCKS.register("bioluminescent_rail", () -> new WoodenRail(of(Material.WOOD, MaterialColor.WOOD).noCollission().strength(0.7F).sound(SoundType.BAMBOO).lightLevel((state)->10)));
    private static final RegistryObject<Block> CHUNK_LOADER_BLOCK = BLOCKS.register("chunk_loader", () -> new ChunkLoaderBlock(of(Material.METAL, MaterialColor.COLOR_GREEN).strength(5f).harvestTool(ToolType.PICKAXE).noOcclusion().lightLevel((state)->12)));
    private static final RegistryObject<Block> SILICA_STEEL_BLOCK = BLOCKS.register("silica_steel_block", () -> new Block(of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3f,3f).harvestTool(ToolType.PICKAXE)));
    private static final RegistryObject<Block> CHUNKRODITE_BLOCK = BLOCKS.register("chunkrodite_block", () -> new Block(of(Material.STONE, MaterialColor.COLOR_BLACK).strength(3f, 3f).harvestTool(ToolType.PICKAXE)));


    public static final Block rail_turn = null;
    public static final Block parallel_rail = null;
    public static final Block wooden_rail = null;
    public static final Block wooden_rail_turn = null;
    public static final Block wooden_parallel_rail = null;
    public static final Block maglev_rail = null;
    public static final Block maglev_rail_turn = null;
    public static final Block maglev_parallel_rail = null;
    public static final Block maglev_powered_rail = null;
    public static final Block bioluminescent_rail = null;
    public static final Block chunk_loader = null;
    public static final Block silica_steel_block = null;

    // Buildable Items
    private static final RegistryObject<Item> RAIL_TURN_ITEM = ITEMS.register("rail_turn", () -> new BlockItem(rail_turn, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> PARALLEL_RAIL_ITEM = ITEMS.register("parallel_rail", () -> new BlockItem(parallel_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> WOODEN_RAIL_ITEM = ITEMS.register("wooden_rail", () -> new BlockItem(wooden_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> WOODEN_RAIL_TURN_ITEM = ITEMS.register("wooden_rail_turn", () -> new BlockItem(wooden_rail_turn, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> WOODEN_PARALLEL_RAIL_ITEM = ITEMS.register("wooden_parallel_rail", () -> new BlockItem(wooden_parallel_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> MAGLEV_RAIL_ITEM = ITEMS.register("maglev_rail", () -> new BlockItem(maglev_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> MAGLEV_RAIL_TURN_ITEM = ITEMS.register("maglev_rail_turn", () -> new BlockItem(maglev_rail_turn, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> MAGLEV_PARALLEL_RAIL_ITEM = ITEMS.register("maglev_parallel_rail", () -> new BlockItem(maglev_parallel_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> MAGLEV_POWERED_RAIL_ITEM = ITEMS.register("maglev_powered_rail", () -> new BlockItem(maglev_powered_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> BIOLUMINESCENT_RAIL_ITEM = ITEMS.register("bioluminescent_rail", () -> new BlockItem(bioluminescent_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> MINECART_WITH_NET_ITEM = ITEMS.register("minecart_with_net", () -> new MinecartWithNetItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> MINECART_WITH_CHUNK_LOADER_ITEM = ITEMS.register("minecart_with_chunk_loader", () -> new ChunkLoaderCartItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> CAMPFIRE_CART_ITEM = ITEMS.register("campfire_cart", () -> new CampfireCartItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> SOULFIRE_CART_ITEM = ITEMS.register("soulfire_cart", () -> new SoulfireCartItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> WOODEN_PUSHCART_ITEM = ITEMS.register("wooden_pushcart", () -> new WoodenPushcartItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> IRON_PUSHCART_ITEM = ITEMS.register("iron_pushcart", () -> new IronPushcartItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COUPLER_ITEM = ITEMS.register("coupler", () -> new CouplerItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> CHUNK_LOADER_ITEM = ITEMS.register("chunk_loader", () -> new BlockItem(chunk_loader, new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
    private static final RegistryObject<Item> SILICA_STEEL_BLOCK_ITEM = ITEMS.register("silica_steel_block", () -> new BlockItem(silica_steel_block, new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
    private static final RegistryObject<Item> CHUNKRODITE_BLOCK_ITEM = ITEMS.register("chunkrodite_block", () -> new BlockItem(silica_steel_block, new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
    private static final RegistryObject<Item> HIGH_SPEED_UPGRADE_ITEM = ITEMS.register("high_speed_upgrade", () -> new Item(new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));

    // Rail Signal Items
    private static final RegistryObject<Item> RAIL_SIGNAL_WHITE = ITEMS.register("rail_signal_white", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_ORANGE = ITEMS.register("rail_signal_orange", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_MAGENTA = ITEMS.register("rail_signal_magenta", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_LIGHT_BLUE = ITEMS.register("rail_signal_light_blue", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_YELLOW = ITEMS.register("rail_signal_yellow", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_LIME = ITEMS.register("rail_signal_lime", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_PINK = ITEMS.register("rail_signal_pink", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_GRAY = ITEMS.register("rail_signal_gray", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_LIGHT_GRAY = ITEMS.register("rail_signal_light_gray", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_CYAN = ITEMS.register("rail_signal_cyan", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_PURPLE = ITEMS.register("rail_signal_purple", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_BLUE = ITEMS.register("rail_signal_blue", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_BROWN = ITEMS.register("rail_signal_brown", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_GREEN = ITEMS.register("rail_signal_green", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_RED = ITEMS.register("rail_signal_red", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_BLACK = ITEMS.register("rail_signal_black", () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));

    public static final Item rail_signal_white = null;
    public static final Item rail_signal_orange = null;
    public static final Item rail_signal_magenta = null;
    public static final Item rail_signal_light_blue = null;
    public static final Item rail_signal_yellow = null;
    public static final Item rail_signal_lime = null;
    public static final Item rail_signal_pink = null;
    public static final Item rail_signal_gray = null;
    public static final Item rail_signal_light_grey = null;
    public static final Item rail_signal_cyan = null;
    public static final Item rail_signal_purple = null;
    public static final Item rail_signal_blue = null;
    public static final Item rail_signal_brown = null;
    public static final Item rail_signal_green = null;
    public static final Item rail_signal_red = null;
    public static final Item rail_signal_black = null;

    // Color Detector Rail Blocks
    private static final RegistryObject<Block> DETECTOR_RAIL_WHITE = BLOCKS.register("color_detector_rail_white", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_white));
    private static final RegistryObject<Block> DETECTOR_RAIL_ORANGE = BLOCKS.register("color_detector_rail_orange", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_orange));
    private static final RegistryObject<Block> DETECTOR_RAIL_MAGENTA = BLOCKS.register("color_detector_rail_magenta", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_magenta));
    private static final RegistryObject<Block> DETECTOR_RAIL_LIGHT_BLUE = BLOCKS.register("color_detector_rail_light_blue", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_light_blue));
    private static final RegistryObject<Block> DETECTOR_RAIL_YELLOW = BLOCKS.register("color_detector_rail_yellow", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_yellow));
    private static final RegistryObject<Block> DETECTOR_RAIL_LIME = BLOCKS.register("color_detector_rail_lime", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_lime));
    private static final RegistryObject<Block> DETECTOR_RAIL_PINK = BLOCKS.register("color_detector_rail_pink", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_pink));
    private static final RegistryObject<Block> DETECTOR_RAIL_GRAY = BLOCKS.register("color_detector_rail_gray", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_gray));
    private static final RegistryObject<Block> DETECTOR_RAIL_LIGHT_GRAY = BLOCKS.register("color_detector_rail_light_gray", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_light_grey));
    private static final RegistryObject<Block> DETECTOR_RAIL_CYAN = BLOCKS.register("color_detector_rail_cyan", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_cyan));
    private static final RegistryObject<Block> DETECTOR_RAIL_PURPLE = BLOCKS.register("color_detector_rail_purple", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_purple));
    private static final RegistryObject<Block> DETECTOR_RAIL_BLUE = BLOCKS.register("color_detector_rail_blue", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_blue));
    private static final RegistryObject<Block> DETECTOR_RAIL_BROWN = BLOCKS.register("color_detector_rail_brown", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_brown));
    private static final RegistryObject<Block> DETECTOR_RAIL_GREEN = BLOCKS.register("color_detector_rail_green", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_green));
    private static final RegistryObject<Block> DETECTOR_RAIL_RED = BLOCKS.register("color_detector_rail_red", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_red));
    private static final RegistryObject<Block> DETECTOR_RAIL_BLACK = BLOCKS.register("color_detector_rail_black", () -> new ColorDetectorRailBlock(of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), ()->rail_signal_black));

    public static final Block color_detector_rail_white = null;
    public static final Block color_detector_rail_orange = null;
    public static final Block color_detector_rail_magenta = null;
    public static final Block color_detector_rail_light_blue = null;
    public static final Block color_detector_rail_yellow = null;
    public static final Block color_detector_rail_lime = null;
    public static final Block color_detector_rail_pink = null;
    public static final Block color_detector_rail_gray = null;
    public static final Block color_detector_rail_light_gray = null;
    public static final Block color_detector_rail_cyan = null;
    public static final Block color_detector_rail_purple = null;
    public static final Block color_detector_rail_blue = null;
    public static final Block color_detector_rail_brown = null;
    public static final Block color_detector_rail_green = null;
    public static final Block color_detector_rail_red = null;
    public static final Block color_detector_rail_black = null;

    // Color Detector Rail Items
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_WHITE = ITEMS.register("color_detector_rail_white", () -> new BlockItem(color_detector_rail_white, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_ORANGE = ITEMS.register("color_detector_rail_orange", () -> new BlockItem(color_detector_rail_orange, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_MAGENTA = ITEMS.register("color_detector_rail_magenta", () -> new BlockItem(color_detector_rail_magenta, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_LIGHT_BLUE = ITEMS.register("color_detector_rail_light_blue", () -> new BlockItem(color_detector_rail_light_blue, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_YELLOW = ITEMS.register("color_detector_rail_yellow", () -> new BlockItem(color_detector_rail_yellow, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_LIME = ITEMS.register("color_detector_rail_lime", () -> new BlockItem(color_detector_rail_lime, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_PINK = ITEMS.register("color_detector_rail_pink", () -> new BlockItem(color_detector_rail_pink, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_GRAY = ITEMS.register("color_detector_rail_gray", () -> new BlockItem(color_detector_rail_gray, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_LIGHT_GRAY = ITEMS.register("color_detector_rail_light_gray", () -> new BlockItem(color_detector_rail_light_gray, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_CYAN = ITEMS.register("color_detector_rail_cyan", () -> new BlockItem(color_detector_rail_cyan, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_PURPLE = ITEMS.register("color_detector_rail_purple", () -> new BlockItem(color_detector_rail_purple, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_BLUE = ITEMS.register("color_detector_rail_blue", () -> new BlockItem(color_detector_rail_blue, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_BROWN = ITEMS.register("color_detector_rail_brown", () -> new BlockItem(color_detector_rail_brown, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_GREEN = ITEMS.register("color_detector_rail_green", () -> new BlockItem(color_detector_rail_green, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_RED = ITEMS.register("color_detector_rail_red", () -> new BlockItem(color_detector_rail_red, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_BLACK = ITEMS.register("color_detector_rail_black", () -> new BlockItem(color_detector_rail_black, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));

    // Material Items
    private static final RegistryObject<Item> LEVITATION_POWDER = ITEMS.register("levitation_powder", () -> new Item(new Item.Properties().stacksTo(64).tab(ItemGroup.TAB_MATERIALS)));
    private static final RegistryObject<Item> SILICA_STEEL_MIX = ITEMS.register("silica_steel_mix", () -> new Item(new Item.Properties().stacksTo(64).tab(ItemGroup.TAB_MATERIALS)));
    private static final RegistryObject<Item> SILICA_STEEL = ITEMS.register("silica_steel", () -> new Item(new Item.Properties().stacksTo(64).tab(ItemGroup.TAB_MATERIALS)));
    private static final RegistryObject<Item> CHUNKRODITE = ITEMS.register("chunkrodite", () -> new Item(new Item.Properties().stacksTo(64).tab(ItemGroup.TAB_MATERIALS)));


    // Tile Entities
    private static final RegistryObject<TileEntityType<ChunkLoaderTile>> CHUNK_LOADER_TILE_ENTITY = TILE_ENTITIES.register("chunk_loader_te", () -> TileEntityType.Builder.<ChunkLoaderTile>of(ChunkLoaderTile::new, chunk_loader).build(null));

    public static final TileEntityType<ChunkLoaderTile> chunk_loader_te = null;

    // Containers
    private static final RegistryObject<ContainerType<ChunkLoaderContainer>> CHUNK_LOADER_CONTAINER = CONTAINERS.register("chunk_loader_c", () -> IForgeContainerType.create((windowId, inv, data) -> new ChunkLoaderContainer(windowId, Minecraft.getInstance().level, data.readBlockPos(), inv, Minecraft.getInstance().player)));

    public static final ContainerType<ChunkLoaderContainer> chunk_loader_c = null;

    public MoreMinecartsMod() {

        MoreMinecartsPacketHandler.Init();

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());

    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        //LOGGER.info("HELLO FROM PREINIT");
        //LOGGER.info("BLOCK >> {}", WOODEN_RAIL_BLOCK.getId());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        //LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);

        ScreenManager.register(chunk_loader_c, ChunkLoaderScreen::new);

        RenderType cutout = RenderType.cutout();
        RenderTypeLookup.setRenderLayer(rail_turn, cutout);
        RenderTypeLookup.setRenderLayer(parallel_rail, cutout);
        RenderTypeLookup.setRenderLayer(wooden_rail, cutout);
        RenderTypeLookup.setRenderLayer(wooden_rail_turn, cutout);
        RenderTypeLookup.setRenderLayer(wooden_parallel_rail, cutout);
        RenderTypeLookup.setRenderLayer(maglev_rail, cutout);
        RenderTypeLookup.setRenderLayer(maglev_rail_turn, cutout);
        RenderTypeLookup.setRenderLayer(maglev_parallel_rail, cutout);
        RenderTypeLookup.setRenderLayer(maglev_powered_rail, cutout);
        RenderTypeLookup.setRenderLayer(bioluminescent_rail, cutout);
        RenderTypeLookup.setRenderLayer(chunk_loader, cutout);

        RenderTypeLookup.setRenderLayer(color_detector_rail_white, cutout);
        RenderTypeLookup.setRenderLayer(color_detector_rail_orange, cutout);
        RenderTypeLookup.setRenderLayer(color_detector_rail_magenta, cutout);
        RenderTypeLookup.setRenderLayer(color_detector_rail_light_blue, cutout);
        RenderTypeLookup.setRenderLayer(color_detector_rail_yellow, cutout);
        RenderTypeLookup.setRenderLayer(color_detector_rail_lime, cutout);
        RenderTypeLookup.setRenderLayer(color_detector_rail_pink, cutout);
        RenderTypeLookup.setRenderLayer(color_detector_rail_gray, cutout);
        RenderTypeLookup.setRenderLayer(color_detector_rail_light_gray, cutout);
        RenderTypeLookup.setRenderLayer(color_detector_rail_cyan, cutout);
        RenderTypeLookup.setRenderLayer(color_detector_rail_purple, cutout);
        RenderTypeLookup.setRenderLayer(color_detector_rail_blue, cutout);
        RenderTypeLookup.setRenderLayer(color_detector_rail_brown, cutout);
        RenderTypeLookup.setRenderLayer(color_detector_rail_green, cutout);
        RenderTypeLookup.setRenderLayer(color_detector_rail_red, cutout);
        RenderTypeLookup.setRenderLayer(color_detector_rail_black, cutout);

        RenderingRegistry.registerEntityRenderingHandler(minecart_with_net, VanillaMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(minecart_with_chunk_loader, VanillaMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(campfire_cart, CampfireCartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(soulfire_cart, SoulfireCartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(wooden_pushcart, WoodenPushcartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(iron_pushcart, IronPushcartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(coupler, CouplerRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(high_speed_minecart, HighSpeedMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_chest_minecart, HighSpeedMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_tnt_minecart, HighSpeedMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_command_block_minecart, HighSpeedMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_hopper_minecart, HighSpeedMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_spawner_minecart, HighSpeedMinecartRenderer::new);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        //InterModComms.sendTo("moreminecarts", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        //LOGGER.info("Got IMC {}", event.getIMCStream().
        //        map(m->m.getMessageSupplier().get()).
        //        collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        //LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            //LOGGER.info("HELLO from Register Block");
        }
    }
}
