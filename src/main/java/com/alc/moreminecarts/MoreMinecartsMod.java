package com.alc.moreminecarts;

import com.alc.moreminecarts.blocks.GlassCactusBlock;
import com.alc.moreminecarts.blocks.OrbStasisBlock;
import com.alc.moreminecarts.blocks.PistonDisplayBlock;
import com.alc.moreminecarts.blocks.containers.ChunkLoaderBlock;
import com.alc.moreminecarts.blocks.containers.MinecartLoaderBlock;
import com.alc.moreminecarts.blocks.containers.MinecartUnloaderBlock;
import com.alc.moreminecarts.blocks.holo_scaffolds.ChaoticHoloScaffold;
import com.alc.moreminecarts.blocks.holo_scaffolds.HoloScaffold;
import com.alc.moreminecarts.blocks.holographic_rails.*;
import com.alc.moreminecarts.blocks.parallel_rails.MaglevParallelRail;
import com.alc.moreminecarts.blocks.parallel_rails.ParallelRail;
import com.alc.moreminecarts.blocks.parallel_rails.WoodenParallelRail;
import com.alc.moreminecarts.blocks.powered_rails.PoweredLightspeedRail;
import com.alc.moreminecarts.blocks.powered_rails.PoweredMaglevRail;
import com.alc.moreminecarts.blocks.rail_crossings.LightspeedRailCrossing;
import com.alc.moreminecarts.blocks.rail_crossings.MaglevRailCrossing;
import com.alc.moreminecarts.blocks.rail_crossings.RailCrossing;
import com.alc.moreminecarts.blocks.rail_crossings.WoodenRailCrossing;
import com.alc.moreminecarts.blocks.rail_jumps.LightspeedRailJump;
import com.alc.moreminecarts.blocks.rail_jumps.MaglevRailJump;
import com.alc.moreminecarts.blocks.rail_jumps.RailJump;
import com.alc.moreminecarts.blocks.rail_turns.MaglevRailTurn;
import com.alc.moreminecarts.blocks.rail_turns.RailTurn;
import com.alc.moreminecarts.blocks.rail_turns.WoodenRailTurn;
import com.alc.moreminecarts.blocks.rails.LightspeedRail;
import com.alc.moreminecarts.blocks.rails.MaglevRail;
import com.alc.moreminecarts.blocks.rails.WoodenRail;
import com.alc.moreminecarts.blocks.utility_rails.ColorDetectorRailBlock;
import com.alc.moreminecarts.blocks.utility_rails.LockingRailBlock;
import com.alc.moreminecarts.blocks.utility_rails.PistonLifterRailBlock;
import com.alc.moreminecarts.blocks.utility_rails.PoweredLockingRailBlock;
import com.alc.moreminecarts.client.BatteryCartScreen;
import com.alc.moreminecarts.client.ChunkLoaderScreen;
import com.alc.moreminecarts.client.MinecartUnLoaderScreen;
import com.alc.moreminecarts.client.TankCartScreen;
import com.alc.moreminecarts.containers.BatteryCartContainer;
import com.alc.moreminecarts.containers.ChunkLoaderContainer;
import com.alc.moreminecarts.containers.MinecartUnLoaderContainer;
import com.alc.moreminecarts.containers.TankCartContainer;
import com.alc.moreminecarts.entities.*;
import com.alc.moreminecarts.entities.HSMinecartEntities.*;
import com.alc.moreminecarts.items.*;
import com.alc.moreminecarts.misc.CouplerClientFactory;
import com.alc.moreminecarts.proxy.ClientProxy;
import com.alc.moreminecarts.proxy.IProxy;
import com.alc.moreminecarts.proxy.MoreMinecartsPacketHandler;
import com.alc.moreminecarts.proxy.ServerProxy;
import com.alc.moreminecarts.renderers.*;
import com.alc.moreminecarts.renderers.highspeed.HSMinecartRenderer;
import com.alc.moreminecarts.renderers.highspeed.HSPistonPushcartRenderer;
import com.alc.moreminecarts.renderers.highspeed.HSPushcartRenderer;
import com.alc.moreminecarts.renderers.highspeed.HSStickyPistonPushcartRenderer;
import com.alc.moreminecarts.tile_entities.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.blockplacer.ColumnBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.ToIntFunction;

import static com.alc.moreminecarts.MMItemReferences.*;
import static com.alc.moreminecarts.MMReferences.*;
import static net.minecraft.block.AbstractBlock.Properties.of;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("moreminecarts")
public class MoreMinecartsMod
{
    // Directly reference a log4j logger.
    public static Logger LOGGER = LogManager.getLogger();
    public static String MODID = "moreminecarts";
    public static IProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public static ConfiguredFeature<?, ?> GLASS_CACTUS_FEATURE;


    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

    // Entities
    private static final RegistryObject<EntityType<NetMinecartEntity>> MINECART_WITH_NET_ENTITY = ENTITIES.register("minecart_with_net", () -> EntityType.Builder.<NetMinecartEntity>of(NetMinecartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("minecart_with_net"));
    private static final RegistryObject<EntityType<ChunkLoaderCartEntity>> CHUNK_LOADER_CART = ENTITIES.register("minecart_with_chunk_loader", () -> EntityType.Builder.<ChunkLoaderCartEntity>of(ChunkLoaderCartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("minecart_with_chunk_loader"));
    private static final RegistryObject<EntityType<CampfireCartEntity>> CAMPFIRE_CART_ENTITY = ENTITIES.register("campfire_cart", () -> EntityType.Builder.<CampfireCartEntity>of(CampfireCartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("campfire_cart"));
    private static final RegistryObject<EntityType<SoulfireCartEntity>> SOULFIRE_CART_ENTITY = ENTITIES.register("soulfire_cart", () -> EntityType.Builder.<SoulfireCartEntity>of(SoulfireCartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("soulfire_cart"));
    private static RegistryObject<EntityType<EndfireCartEntity>> ENDFIRE_CART_ENTITY;
    private static final RegistryObject<EntityType<WoodenPushcartEntity>> WOODEN_PUSHCART_ENTITY = ENTITIES.register("wooden_pushcart", () -> EntityType.Builder.<WoodenPushcartEntity>of(WoodenPushcartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("wooden_pushcart"));
    private static final RegistryObject<EntityType<IronPushcartEntity>> IRON_PUSHCART_ENTITY = ENTITIES.register("iron_pushcart", () -> EntityType.Builder.<IronPushcartEntity>of(IronPushcartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("iron_pushcart"));
    private static final RegistryObject<EntityType<PistonPushcartEntity>> PISTON_PUSHCART_ENTITY = ENTITIES.register("piston_pushcart", () -> EntityType.Builder.<PistonPushcartEntity>of(PistonPushcartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("piston_pushcart"));
    private static final RegistryObject<EntityType<StickyPistonPushcartEntity>> STICKY_PISTON_PUSHCART_ENTITY = ENTITIES.register("sticky_piston_pushcart", () -> EntityType.Builder.<StickyPistonPushcartEntity>of(StickyPistonPushcartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("sticky_piston_pushcart"));
    private static final RegistryObject<EntityType<TankCartEntity>> TANK_CART_ENTITY = ENTITIES.register("tank_cart", () -> EntityType.Builder.<TankCartEntity>of(TankCartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("tank_cart"));
    private static final RegistryObject<EntityType<BatteryCartEntity>> BATTERY_CART_ENTITY = ENTITIES.register("battery_cart", () -> EntityType.Builder.<BatteryCartEntity>of(BatteryCartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("battery_cart"));
    private static final RegistryObject<EntityType<CouplerEntity>> COUPLER_ENTITY = ENTITIES.register("coupler", () -> EntityType.Builder.<CouplerEntity>of(CouplerEntity::new, EntityClassification.MISC ).sized(0.3F, 0.3F).noSummon().setCustomClientFactory(CouplerClientFactory.get()).build("coupler"));

    // High Speed Cart Entities
    private static final RegistryObject<EntityType<HSMinecart>> HS_CART_ENTITY = ENTITIES.register("high_speed_minecart", () -> EntityType.Builder.<HSMinecart>of(HSMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_minecart"));
    private static final RegistryObject<EntityType<HSChestMinecart>> HS_CHEST_CART_ENTITY = ENTITIES.register("high_speed_chest_minecart", () -> EntityType.Builder.<HSChestMinecart>of(HSChestMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_chest_minecart"));
    private static final RegistryObject<EntityType<HSTNTMinecart>> HS_TNT_CART_ENTITY = ENTITIES.register("high_speed_tnt_minecart", () -> EntityType.Builder.<HSTNTMinecart>of(HSTNTMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_tnt_minecart"));
    private static final RegistryObject<EntityType<HSCommandBlockMinecart>> HS_COMMAND_BLOCK_CART_ENTITY = ENTITIES.register("high_speed_command_block_minecart", () -> EntityType.Builder.<HSCommandBlockMinecart>of(HSCommandBlockMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_command_block_minecart"));
    private static final RegistryObject<EntityType<HSHopperMinecart>> HS_HOPPER_CART_ENTITY = ENTITIES.register("high_speed_hopper_minecart", () -> EntityType.Builder.<HSHopperMinecart>of(HSHopperMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_hopper_minecart"));
    private static final RegistryObject<EntityType<HSSpawnerMinecart>> HS_SPAWNER_CART_ENTITY = ENTITIES.register("high_speed_spawner_minecart", () -> EntityType.Builder.<HSSpawnerMinecart>of(HSSpawnerMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_spawner_minecart"));
    private static final RegistryObject<EntityType<HSFurnaceMinecart>> HS_FURNACE_CART_ENTITY = ENTITIES.register("high_speed_furnace_minecart", () -> EntityType.Builder.<HSFurnaceMinecart>of(HSFurnaceMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_furnace_minecart"));
    private static final RegistryObject<EntityType<HSNetMinecart>> HS_NET_CART_ENTITY = ENTITIES.register("high_speed_net_minecart", () -> EntityType.Builder.<HSNetMinecart>of(HSNetMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_net_minecart"));
    private static final RegistryObject<EntityType<HSChunkLoaderMinecart>> HS_CHUNK_LOADER_CART_ENTITY = ENTITIES.register("high_speed_chunk_loader_minecart", () -> EntityType.Builder.<HSChunkLoaderMinecart>of(HSChunkLoaderMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_chunk_loader_minecart"));
    private static final RegistryObject<EntityType<HSTankMinecart>> HS_TANK_CART_ENTITY = ENTITIES.register("high_speed_tank_minecart", () -> EntityType.Builder.<HSTankMinecart>of(HSTankMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_tank_minecart"));
    private static final RegistryObject<EntityType<HSBatteryMinecart>> HS_BATTERY_CART_ENTITY = ENTITIES.register("high_speed_battery_minecart", () -> EntityType.Builder.<HSBatteryMinecart>of(HSBatteryMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_battery_minecart"));
    private static final RegistryObject<EntityType<HSCampfireMinecart>> HS_CAMPFIRE_CART_ENTITY = ENTITIES.register("high_speed_campfire_minecart", () -> EntityType.Builder.<HSCampfireMinecart>of(HSCampfireMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_campfire_minecart"));
    private static final RegistryObject<EntityType<HSSoulfireMinecart>> HS_SOULFIRE_CART_ENTITY = ENTITIES.register("high_speed_soulfire_minecart", () -> EntityType.Builder.<HSSoulfireMinecart>of(HSSoulfireMinecart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_soulfire_minecart"));
    private static final RegistryObject<EntityType<HSPushcart>> HS_PUSHCART_ENTITY = ENTITIES.register("high_speed_pushcart", () -> EntityType.Builder.<HSPushcart>of(HSPushcart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_pushcart"));
    private static final RegistryObject<EntityType<HSPistonPushcart>> HS_PISTON_PUSHCART_ENTITY = ENTITIES.register("high_speed_piston_pushcart", () -> EntityType.Builder.<HSPistonPushcart>of(HSPistonPushcart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_piston_pushcart"));
    private static final RegistryObject<EntityType<HSStickyPistonPushcart>> HS_STICKY_PISTON_PUSHCART_ENTITY = ENTITIES.register("high_speed_sticky_piston_pushcart", () -> EntityType.Builder.<HSStickyPistonPushcart>of(HSStickyPistonPushcart::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("high_speed_sticky_piston_pushcart"));


    // Rail Blocks
    private static final RegistryObject<Block> RAIL_TURN = BLOCKS.register("rail_turn", () -> new RailTurn(of(Material.DECORATION).noCollission().harvestTool(ToolType.PICKAXE).strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> PARALLEL_RAIL_BLOCK = BLOCKS.register("parallel_rail", () -> new ParallelRail(of(Material.DECORATION).noCollission().harvestTool(ToolType.PICKAXE).strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> CROSS_RAIL_BLOCK = BLOCKS.register("cross_rail", () -> new RailCrossing(of(Material.DECORATION).noCollission().harvestTool(ToolType.PICKAXE).strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> PROJECTOR_RAIL = BLOCKS.register("projector_rail", () -> new ProjectorRail(of(Material.DECORATION).noCollission().harvestTool(ToolType.PICKAXE).strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> HOLOGRAM_RAIL = BLOCKS.register("hologram_rail", () -> new HolographicRail(of(Material.GLASS).noCollission().harvestTool(ToolType.PICKAXE).strength(0.2F).sound(SoundType.GLASS)));
    private static final RegistryObject<Block> RAIL_JUMP = BLOCKS.register("rail_jump", () -> new RailJump(of(Material.DECORATION).noCollission().harvestTool(ToolType.PICKAXE).strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> WOODEN_RAIL_BLOCK = BLOCKS.register("wooden_rail", () -> new WoodenRail(of(Material.WOOD, MaterialColor.WOOD).noCollission().harvestTool(ToolType.AXE).strength(0.7F).sound(SoundType.BAMBOO)));
    private static final RegistryObject<Block> WOODEN_RAIL_TURN = BLOCKS.register("wooden_rail_turn", () -> new WoodenRailTurn(of(Material.WOOD, MaterialColor.WOOD).noCollission().harvestTool(ToolType.AXE).strength(0.7F).sound(SoundType.BAMBOO)));
    private static final RegistryObject<Block> WOODEN_PARALLEL_RAIL_BLOCK = BLOCKS.register("wooden_parallel_rail", () -> new WoodenParallelRail(of(Material.WOOD, MaterialColor.WOOD).noCollission().harvestTool(ToolType.AXE).strength(0.7F).sound(SoundType.BAMBOO)));
    private static final RegistryObject<Block> WOODEN_CROSS_RAIL_BLOCK = BLOCKS.register("wooden_cross_rail", () -> new WoodenRailCrossing(of(Material.WOOD, MaterialColor.WOOD).noCollission().harvestTool(ToolType.AXE).strength(0.7F).sound(SoundType.BAMBOO)));
    private static final RegistryObject<Block> WOODEN_PROJECTOR_RAIL = BLOCKS.register("wooden_projector_rail", () -> new WoodenProjectorRail(of(Material.WOOD, MaterialColor.WOOD).noCollission().harvestTool(ToolType.AXE).strength(0.7F).sound(SoundType.BAMBOO)));
    private static final RegistryObject<Block> WOODEN_HOLOGRAM_RAIL = BLOCKS.register("wooden_hologram_rail", () -> new WoodenHolographicRail(of(Material.GLASS, MaterialColor.WOOD).noCollission().harvestTool(ToolType.AXE).strength(0.2F).sound(SoundType.GLASS)));
    private static final RegistryObject<Block> MAGLEV_RAIL_BLOCK = BLOCKS.register("maglev_rail", () -> new MaglevRail(of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().harvestTool(ToolType.PICKAXE).strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> MAGLEV_RAIL_TURN = BLOCKS.register("maglev_rail_turn", () -> new MaglevRailTurn(of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().harvestTool(ToolType.PICKAXE).strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> MAGLEV_PARALLEL_RAIL_BLOCK = BLOCKS.register("maglev_parallel_rail", () -> new MaglevParallelRail(of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().harvestTool(ToolType.PICKAXE).strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> MAGLEV_CROSS_RAIL_BLOCK = BLOCKS.register("maglev_cross_rail", () -> new MaglevRailCrossing(of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().harvestTool(ToolType.PICKAXE).strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> MAGLEV_PROJECTOR_RAIL = BLOCKS.register("maglev_projector_rail", () -> new MaglevProjectorRail(of(Material.DECORATION, MaterialColor.COLOR_BLUE).harvestTool(ToolType.PICKAXE).noCollission().strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> MAGLEV_HOLOGRAM_RAIL = BLOCKS.register("maglev_hologram_rail", () -> new MaglevHolographicRail(of(Material.GLASS, MaterialColor.COLOR_BLUE).noCollission().harvestTool(ToolType.PICKAXE).strength(0.2F).sound(SoundType.GLASS)));
    private static final RegistryObject<Block> MAGLEV_POWERED_RAIL_BLOCK = BLOCKS.register("maglev_powered_rail", () -> new PoweredMaglevRail(of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().harvestTool(ToolType.PICKAXE).strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> MAGLEV_RAIL_JUMP = BLOCKS.register("maglev_rail_jump", () -> new MaglevRailJump(of(Material.DECORATION).noCollission().harvestTool(ToolType.PICKAXE).strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> LIGHTSPEED_RAIL_BLOCK = BLOCKS.register("lightspeed_rail", () -> new LightspeedRail(of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().harvestTool(ToolType.PICKAXE).strength(1F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> LIGHTSPEED_CROSS_RAIL_BLOCK = BLOCKS.register("lightspeed_cross_rail", () -> new LightspeedRailCrossing(of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().harvestTool(ToolType.PICKAXE).strength(1F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> LIGHTSPEED_POWERED_RAIL_BLOCK = BLOCKS.register("lightspeed_powered_rail", () -> new PoweredLightspeedRail(of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().harvestTool(ToolType.PICKAXE).strength(1F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> LIGHTSPEED_RAIL_JUMP = BLOCKS.register("lightspeed_rail_jump", () -> new LightspeedRailJump(of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().harvestTool(ToolType.PICKAXE).strength(1F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> BIOLUMINESCENT_RAIL_BLOCK = BLOCKS.register("bioluminescent_rail", () -> new WoodenRail(of(Material.WOOD, MaterialColor.WOOD).noCollission().strength(0.7F).sound(SoundType.BAMBOO).lightLevel((state)->10)));
    private static final RegistryObject<Block> LOCKING_RAIL_BLOCK = BLOCKS.register("locking_rail", () -> new LockingRailBlock(of(Material.DECORATION).noCollission().harvestTool(ToolType.PICKAXE).strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> POWERED_LOCKING_RAIL_BLOCK = BLOCKS.register("powered_locking_rail", () -> new PoweredLockingRailBlock(of(Material.DECORATION).noCollission().harvestTool(ToolType.PICKAXE).strength(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> PISTON_LIFTER_RAIL = BLOCKS.register("piston_lifter_rail", () -> new PistonLifterRailBlock(of(Material.DECORATION).noCollission().harvestTool(ToolType.PICKAXE).strength(0.7F).sound(SoundType.METAL)));

    // Container Blocks
    private static final RegistryObject<Block> CHUNK_LOADER_BLOCK = BLOCKS.register("chunk_loader", () -> new ChunkLoaderBlock(of(Material.METAL, MaterialColor.COLOR_GREEN).strength(5f).harvestTool(ToolType.PICKAXE).noOcclusion().lightLevel(poweredBlockEmission(13))));
    private static final RegistryObject<Block> MINECART_LOADER_BLOCK = BLOCKS.register("minecart_loader", () -> new MinecartLoaderBlock(of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3f).harvestTool(ToolType.PICKAXE)));
    private static final RegistryObject<Block> MINECART_UNLOADER_BLOCK = BLOCKS.register("minecart_unloader", () -> new MinecartUnloaderBlock(of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3f).harvestTool(ToolType.PICKAXE)));
    private static final RegistryObject<Block> PEARL_STASIS_CHAMBER = BLOCKS.register("pearl_stasis_chamber", () -> new OrbStasisBlock(of(Material.ICE_SOLID, MaterialColor.COLOR_PURPLE).strength(5f).harvestTool(ToolType.PICKAXE).noOcclusion()));

    // Other Blocks
    private static final RegistryObject<Block> SILICA_STEEL_BLOCK = BLOCKS.register("silica_steel_block", () -> new Block(of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3f,3f).harvestTool(ToolType.PICKAXE)));
    private static final RegistryObject<Block> CHUNKRODITE_BLOCK = BLOCKS.register("chunkrodite_block", () -> new Block(of(Material.STONE, MaterialColor.COLOR_BLACK).strength(3f, 3f).harvestTool(ToolType.PICKAXE)));
    private static final RegistryObject<Block> GLASS_CACTUS = BLOCKS.register("glass_cactus", () -> new GlassCactusBlock(of(Material.GLASS, MaterialColor.WOOL).randomTicks().strength(2F).sound(SoundType.WOOL).noOcclusion()));
    private static final RegistryObject<Block> HOLO_SCAFFOLD_GENERATOR = BLOCKS.register("holo_scaffold_generator", () -> new Block(of(Material.HEAVY_METAL, MaterialColor.COLOR_GRAY).strength(3f,3f).harvestTool(ToolType.PICKAXE).lightLevel((state) -> 13)));
    private static final RegistryObject<Block> HOLO_SCAFFOLD = BLOCKS.register("holo_scaffold", () -> new HoloScaffold(of(Material.DECORATION).strength(0.05F).noOcclusion().dynamicShape()));
    private static final RegistryObject<Block> CHAOTIC_HOLO_SCAFFOLD = BLOCKS.register("chaotic_holo_scaffold", () -> new ChaoticHoloScaffold(of(Material.DECORATION).strength(0.05F).noOcclusion().dynamicShape()));
    private static final RegistryObject<Block> PISTON_DISPLAY_BLOCK = BLOCKS.register("piston_display_block", () -> new PistonDisplayBlock(of(Material.DECORATION)));

    // Potted Plants
    private static final RegistryObject<Block> POTTED_GLASS_CACTUS = BLOCKS.register("potted_glass_cactus", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> glass_cactus, of(Material.DECORATION).instabreak().noOcclusion()));
    private static final RegistryObject<Block> POTTED_BEET = BLOCKS.register("potted_beet", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> MMReferences.chunkrodite_block, of(Material.DECORATION).instabreak().noOcclusion()));

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


    // Rail Items
    private static final RegistryObject<Item> RAIL_TURN_ITEM = ITEMS.register("rail_turn", () -> new BlockItem(rail_turn, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> PARALLEL_RAIL_ITEM = ITEMS.register("parallel_rail", () -> new BlockItem(parallel_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> CROSS_RAIL_ITEM = ITEMS.register("cross_rail", () -> new BlockItem(cross_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> PROJECTOR_RAIL_ITEM = ITEMS.register("projector_rail", () -> new BlockItem(projector_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_JUMP_ITEM = ITEMS.register("rail_jump", () -> new BlockItem(rail_jump, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> WOODEN_RAIL_ITEM = ITEMS.register("wooden_rail", () -> new BlockItem(wooden_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> WOODEN_RAIL_TURN_ITEM = ITEMS.register("wooden_rail_turn", () -> new BlockItem(wooden_rail_turn, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> WOODEN_PARALLEL_RAIL_ITEM = ITEMS.register("wooden_parallel_rail", () -> new BlockItem(wooden_parallel_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> WOODEN_CROSS_RAIL_ITEM = ITEMS.register("wooden_cross_rail", () -> new BlockItem(wooden_cross_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> WOODEN_PROJECTOR_RAIL_ITEM = ITEMS.register("wooden_projector_rail", () -> new BlockItem(wooden_projector_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> MAGLEV_RAIL_ITEM = ITEMS.register("maglev_rail", () -> new BlockItem(maglev_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> MAGLEV_RAIL_TURN_ITEM = ITEMS.register("maglev_rail_turn", () -> new BlockItem(maglev_rail_turn, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> MAGLEV_PARALLEL_RAIL_ITEM = ITEMS.register("maglev_parallel_rail", () -> new BlockItem(maglev_parallel_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> MAGLEV_CROSS_RAIL = ITEMS.register("maglev_cross_rail", () -> new BlockItem(maglev_cross_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> MAGLEV_PROJECTOR_RAIL_ITEM = ITEMS.register("maglev_projector_rail", () -> new BlockItem(maglev_projector_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> MAGLEV_POWERED_RAIL_ITEM = ITEMS.register("maglev_powered_rail", () -> new BlockItem(maglev_powered_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> MAGLEV_RAIL_JUMP_ITEM = ITEMS.register("maglev_rail_jump", () -> new BlockItem(maglev_rail_jump, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> LIGHTSPEED_RAIL_ITEM = ITEMS.register("lightspeed_rail", () -> new BlockItem(lightspeed_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> LIGHTSPEED_CROSS_RAIL_ITEM = ITEMS.register("lightspeed_cross_rail", () -> new BlockItem(lightspeed_cross_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> LIGHTSPEED_POWERED_RAIL_ITEM = ITEMS.register("lightspeed_powered_rail", () -> new BlockItem(lightspeed_powered_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> LIGHTSPEED_RAIL_JUMP_ITEM = ITEMS.register("lightspeed_rail_jump", () -> new BlockItem(lightspeed_rail_jump, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> BIOLUMINESCENT_RAIL_ITEM = ITEMS.register("bioluminescent_rail", () -> new BlockItem(bioluminescent_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> LOCKING_RAIL_ITEM = ITEMS.register("locking_rail", () -> new BlockItem(locking_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> POWERED_LOCKING_RAIL_ITEM = ITEMS.register("powered_locking_rail", () -> new BlockItem(powered_locking_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> PISTON_LIFTER_RAIL_ITEM = ITEMS.register("piston_lifter_rail", () -> new BlockItem(piston_lifter_rail, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));

    // Minecart Items
    private static final RegistryObject<Item> MINECART_WITH_NET_ITEM = ITEMS.register("minecart_with_net", () -> new MinecartWithNetItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> MINECART_WITH_CHUNK_LOADER_ITEM = ITEMS.register("minecart_with_chunk_loader", () -> new ChunkLoaderCartItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> CAMPFIRE_CART_ITEM = ITEMS.register("campfire_cart", () -> new CampfireCartItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> SOULFIRE_CART_ITEM = ITEMS.register("soulfire_cart", () -> new SoulfireCartItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static RegistryObject<Item> ENDFIRE_CART_ITEM;
    private static final RegistryObject<Item> WOODEN_PUSHCART_ITEM = ITEMS.register("wooden_pushcart", () -> new WoodenPushcartItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> IRON_PUSHCART_ITEM = ITEMS.register("iron_pushcart", () -> new IronPushcartItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> PISTON_PUSHCART_ITEM = ITEMS.register("piston_pushcart", () -> new PistonPushcartItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> STICKY_PISTON_PUSHCART_ITEM = ITEMS.register("sticky_piston_pushcart", () -> new StickyPistonPushcartItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> TANK_CART_ITEM = ITEMS.register("tank_cart", () -> new TankCartItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> BATTERY_CART_ITEM = ITEMS.register("battery_cart", () -> new BatteryCartItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));

    // Block Items
    private static final RegistryObject<Item> CHUNK_LOADER_ITEM = ITEMS.register("chunk_loader", () -> new BlockItem(MMReferences.chunk_loader, new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
    private static final RegistryObject<Item> SILICA_STEEL_BLOCK_ITEM = ITEMS.register("silica_steel_block", () -> new BlockItem(silica_steel_block, new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
    private static final RegistryObject<Item> CHUNKRODITE_BLOCK_ITEM = ITEMS.register("chunkrodite_block", () -> new BlockItem(MMReferences.chunkrodite_block, new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
    private static final RegistryObject<Item> GLASS_CACTUS_ITEM = ITEMS.register("glass_cactus", () -> new GlassCactusItem(glass_cactus, new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));
    private static final RegistryObject<Item> HOLO_SCAFFOLD_GENERATOR_ITEM = ITEMS.register("holo_scaffold_generator", () -> new BlockItem(holo_scaffold_generator, new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));
    private static final RegistryObject<Item> MINECART_LOADER_ITEM = ITEMS.register("minecart_loader", () -> new BlockItem(MMReferences.minecart_loader, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> MINECART_UNLOADER_ITEM = ITEMS.register("minecart_unloader", () -> new BlockItem(MMReferences.minecart_unloader, new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> PEARL_STASIS_CHAMBER_ITEM = ITEMS.register("pearl_stasis_chamber", () -> new BlockItem(pearl_stasis_chamber, new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));

    // Misc Items
    private static final RegistryObject<Item> COUPLER_ITEM = ITEMS.register("coupler", () -> new CouplerItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<Item> HIGH_SPEED_UPGRADE_ITEM = ITEMS.register("high_speed_upgrade", () -> new Item(new Item.Properties().tab(ItemGroup.TAB_TRANSPORTATION)));
    private static final RegistryObject<HoloRemoteItem> HOLO_REMOTE_ITEM = ITEMS.register("holo_remote", () -> new HoloRemoteItem(HoloRemoteItem.HoloRemoteType.regular, new Item.Properties().tab(ItemGroup.TAB_TOOLS)));
    private static final RegistryObject<HoloRemoteItem> BACKWARDS_HOLO_REMOTE_ITEM = ITEMS.register("backwards_holo_remote", () -> new HoloRemoteItem(HoloRemoteItem.HoloRemoteType.backwards, new Item.Properties().tab(ItemGroup.TAB_TOOLS)));
    private static final RegistryObject<HoloRemoteItem> SIMPLE_HOLO_REMOTE_ITEM = ITEMS.register("simple_holo_remote", () -> new HoloRemoteItem(HoloRemoteItem.HoloRemoteType.simple, new Item.Properties().tab(ItemGroup.TAB_TOOLS)));
    private static final RegistryObject<HoloRemoteItem> BROKEN_HOLO_REMOTE_ITEM = ITEMS.register("broken_holo_remote", () -> new HoloRemoteItem(HoloRemoteItem.HoloRemoteType.broken, new Item.Properties().tab(ItemGroup.TAB_TOOLS)));

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
    private static final RegistryObject<Item> HARD_LIGHT_LENS = ITEMS.register("hard_light_lens", () -> new Item(new Item.Properties().stacksTo(64).tab(ItemGroup.TAB_MATERIALS)));
    private static final RegistryObject<Item> GLASS_SPINES = ITEMS.register("glass_spines", () -> new Item(new Item.Properties().stacksTo(64).tab(ItemGroup.TAB_MATERIALS)));


    // Tile Entities
    private static final RegistryObject<TileEntityType<ChunkLoaderTile>> CHUNK_LOADER_TILE_ENTITY = TILE_ENTITIES.register("chunk_loader_te", () -> TileEntityType.Builder.<ChunkLoaderTile>of(ChunkLoaderTile::new, MMReferences.chunk_loader).build(null));
    private static final RegistryObject<TileEntityType<LockingRailTile>> LOCKING_RAIL_TILE_ENTITY = TILE_ENTITIES.register("locking_rail_te", () -> TileEntityType.Builder.<LockingRailTile>of(LockingRailTile::new, locking_rail).build(null));
    private static final RegistryObject<TileEntityType<PoweredLockingRailTile>> POWERED_LOCKING_RAIL_TILE_ENTITY = TILE_ENTITIES.register("powered_locking_rail_te", () -> TileEntityType.Builder.<PoweredLockingRailTile>of(PoweredLockingRailTile::new, powered_locking_rail).build(null));
    private static final RegistryObject<TileEntityType<MinecartLoaderTile>> MINECART_LOADER_TILE_ENTITY = TILE_ENTITIES.register("minecart_loader_te", () -> TileEntityType.Builder.<MinecartLoaderTile>of(MinecartLoaderTile::new, minecart_loader).build(null));
    private static final RegistryObject<TileEntityType<MinecartUnloaderTile>> MINECART_UNLOADER_TILE_ENTITY = TILE_ENTITIES.register("minecart_unloader_te", () -> TileEntityType.Builder.<MinecartUnloaderTile>of(MinecartUnloaderTile::new, minecart_unloader).build(null));
    private static final RegistryObject<TileEntityType<OrbStasisTile>> PEARL_STASIS_CHAMBER_TILE_ENTITY = TILE_ENTITIES.register("pearl_stasis_chamber_te", () -> TileEntityType.Builder.<OrbStasisTile>of(OrbStasisTile::new, pearl_stasis_chamber).build(null));

    // Containers
    private static final RegistryObject<ContainerType<ChunkLoaderContainer>> CHUNK_LOADER_CONTAINER = CONTAINERS.register("chunk_loader_c", () -> IForgeContainerType.create(
            (windowId, inv, data) -> {
                if (data != null) return new ChunkLoaderContainer(windowId, PROXY.getWorld(), data.readBlockPos(), inv, PROXY.getPlayer());
                else return new ChunkLoaderContainer(windowId, PROXY.getWorld(), inv, PROXY.getPlayer());
            }));
    private static final RegistryObject<ContainerType<MinecartUnLoaderContainer>> MINECART_LOADER_CONTAINER = CONTAINERS.register("minecart_loader_c", () -> IForgeContainerType.create(
            (windowId, inv, data) -> {
                if (data != null) return new MinecartUnLoaderContainer(windowId, PROXY.getWorld(), data.readBlockPos(), inv, PROXY.getPlayer());
                else return new MinecartUnLoaderContainer(windowId, PROXY.getWorld(), inv, PROXY.getPlayer());
            }));
    private static final RegistryObject<ContainerType<TankCartContainer>> TANK_CART_CONTAINER = CONTAINERS.register("tank_cart_c", () -> IForgeContainerType.create(
            (windowId, inv, data) -> {
                return new TankCartContainer(windowId, PROXY.getWorld(), inv, PROXY.getPlayer());
            }));
    private static final RegistryObject<ContainerType<BatteryCartContainer>> BATTERY_CART_CONTAINER = CONTAINERS.register("battery_cart_c", () -> IForgeContainerType.create(
            (windowId, inv, data) -> {
                return new BatteryCartContainer(windowId, PROXY.getWorld(), inv, PROXY.getPlayer());
            }));

    public MoreMinecartsMod() {

        if (EndergeticCompat.endergeticInstalled()) {
            ENDFIRE_CART_ENTITY = ENTITIES.register("endfire_cart", () -> EntityType.Builder.<EndfireCartEntity>of(EndfireCartEntity::new, EntityClassification.MISC ).sized(0.98F, 0.7F).build("endfire_cart"));
            ENDFIRE_CART_ITEM = ITEMS.register("endfire_cart", () -> new EndfireCartItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION)));
        }

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

        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Changes how expensive keeping the chunk loader on is. Set to zero to prevent chunk loading completely.");
        MMConstants.CONFIG_CHUNK_LOADER_MULTIPLIER = builder.defineInRange("chunk_loader_multiplier", ()->1.0D, 0, 100);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, builder.build(), "moreminecartsconfig.toml");

    }

    private void setup(final FMLCommonSetupEvent event)
    {
        GLASS_CACTUS_FEATURE = Feature.RANDOM_PATCH.configured((new BlockClusterFeatureConfig.Builder(
                new SimpleBlockStateProvider(glass_cactus.getStateDefinition().any()),
                new ColumnBlockPlacer(1, 2))).tries(2).noProjection().build())
                .decorated(Features.Placements.HEIGHTMAP_DOUBLE_SQUARE).count(2);

        ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(new ResourceLocation("moreminecarts:chunkrodite_block"), ()->potted_beet);
        ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(new ResourceLocation("moreminecarts:glass_cactus"), ()->potted_glass_cactus);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        //LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);

        PROXY.setupKeybindings();

        ScreenManager.register(chunk_loader_c, ChunkLoaderScreen::new);
        ScreenManager.register(minecart_loader_c, MinecartUnLoaderScreen::new);
        ScreenManager.register(tank_cart_c, TankCartScreen::new);
        ScreenManager.register(battery_cart_c, BatteryCartScreen::new);

        RenderType cutout = RenderType.cutout();

        RenderTypeLookup.setRenderLayer(rail_turn, cutout);
        RenderTypeLookup.setRenderLayer(parallel_rail, cutout);
        RenderTypeLookup.setRenderLayer(cross_rail, cutout);
        RenderTypeLookup.setRenderLayer(projector_rail, cutout);
        RenderTypeLookup.setRenderLayer(hologram_rail, cutout);
        RenderTypeLookup.setRenderLayer(rail_jump, cutout);
        RenderTypeLookup.setRenderLayer(wooden_rail, cutout);
        RenderTypeLookup.setRenderLayer(wooden_rail_turn, cutout);
        RenderTypeLookup.setRenderLayer(wooden_parallel_rail, cutout);
        RenderTypeLookup.setRenderLayer(wooden_cross_rail, cutout);
        RenderTypeLookup.setRenderLayer(wooden_projector_rail, cutout);
        RenderTypeLookup.setRenderLayer(wooden_hologram_rail, cutout);
        RenderTypeLookup.setRenderLayer(maglev_rail, cutout);
        RenderTypeLookup.setRenderLayer(maglev_rail_turn, cutout);
        RenderTypeLookup.setRenderLayer(maglev_parallel_rail, cutout);
        RenderTypeLookup.setRenderLayer(maglev_cross_rail, cutout);
        RenderTypeLookup.setRenderLayer(maglev_projector_rail, cutout);
        RenderTypeLookup.setRenderLayer(maglev_hologram_rail, cutout);
        RenderTypeLookup.setRenderLayer(maglev_powered_rail, cutout);
        RenderTypeLookup.setRenderLayer(maglev_rail_jump, cutout);
        RenderTypeLookup.setRenderLayer(lightspeed_rail, cutout);
        RenderTypeLookup.setRenderLayer(lightspeed_cross_rail, cutout);
        RenderTypeLookup.setRenderLayer(lightspeed_powered_rail, cutout);
        RenderTypeLookup.setRenderLayer(lightspeed_rail_jump, cutout);
        RenderTypeLookup.setRenderLayer(bioluminescent_rail, cutout);
        RenderTypeLookup.setRenderLayer(locking_rail, cutout);
        RenderTypeLookup.setRenderLayer(powered_locking_rail, cutout);
        RenderTypeLookup.setRenderLayer(piston_lifter_rail, cutout);

        RenderTypeLookup.setRenderLayer(MMReferences.chunk_loader, cutout);
        RenderTypeLookup.setRenderLayer(pearl_stasis_chamber, cutout);
        RenderTypeLookup.setRenderLayer(holo_scaffold, cutout);
        RenderTypeLookup.setRenderLayer(chaotic_holo_scaffold, cutout);
        RenderTypeLookup.setRenderLayer(glass_cactus, cutout);
        RenderTypeLookup.setRenderLayer(potted_beet, cutout);
        RenderTypeLookup.setRenderLayer(potted_glass_cactus, cutout);

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
        RenderingRegistry.registerEntityRenderingHandler(MMReferences.campfire_cart, CampfireCartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MMReferences.soulfire_cart, SoulfireCartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MMReferences.wooden_pushcart, WoodenPushcartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MMReferences.iron_pushcart, IronPushcartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MMReferences.piston_pushcart, PistonPushcartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MMReferences.sticky_piston_pushcart, StickyPistonPushcartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(tank_cart, VanillaMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(battery_cart, VanillaMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MMReferences.coupler, CouplerRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(high_speed_minecart, HSMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_chest_minecart, HSMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_tnt_minecart, HSMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_command_block_minecart, HSMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_hopper_minecart, HSMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_spawner_minecart, HSMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_furnace_minecart, HSMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_net_minecart, HSMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_chunk_loader_minecart, HSMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_tank_minecart, HSMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_battery_minecart, HSMinecartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_campfire_minecart, HSPushcartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_soulfire_minecart, HSPushcartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_pushcart, HSPushcartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_piston_pushcart, HSPistonPushcartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(high_speed_sticky_piston_pushcart, HSStickyPistonPushcartRenderer::new);

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

    // Taken from Blocks

    private static ToIntFunction<BlockState> poweredBlockEmission(int p_235420_0_) {
        return (p_235421_1_) -> {
            return p_235421_1_.getValue(BlockStateProperties.POWERED) ? p_235420_0_ : 0;
        };
    }

}
