package com.example.examplemod;

import com.example.examplemod.blocks.ColorDetectorRailBlock;
import com.example.examplemod.blocks.RailTurn;
import com.example.examplemod.blocks.WoodenRail;
import com.example.examplemod.blocks.WoodenRailTurn;
import com.example.examplemod.entities.*;
import com.example.examplemod.items.*;
import com.example.examplemod.misc.CouplerClientFactory;
import com.example.examplemod.misc.CouplerPacketHandler;
import com.example.examplemod.renderers.*;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.MinecraftForge;
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
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraft.block.AbstractBlock.Properties.create;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("examplemod")
@ObjectHolder("examplemod")
public class ExampleMod
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String MODID = "examplemod";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);


    private static final RegistryObject<EntityType<MinecartWithNet>> MINECART_WITH_NET_ENTITY = ENTITIES.register("minecart_with_net", () -> EntityType.Builder.<MinecartWithNet>create(MinecartWithNet::new, EntityClassification.MISC ).size(0.98F, 0.7F).build("minecart_with_net"));
    private static final RegistryObject<EntityType<CampfireCartEntity>> CAMPFIRE_CART_ENTITY = ENTITIES.register("campfire_cart", () -> EntityType.Builder.<CampfireCartEntity>create(CampfireCartEntity::new, EntityClassification.MISC ).size(0.98F, 0.5F).build("campfire_cart"));
    private static final RegistryObject<EntityType<SoulfireCartEntity>> SOULFIRE_CART_ENTITY = ENTITIES.register("soulfire_cart", () -> EntityType.Builder.<SoulfireCartEntity>create(SoulfireCartEntity::new, EntityClassification.MISC ).size(0.98F, 0.5F).build("soulfire_cart"));
    private static final RegistryObject<EntityType<WoodenPushcartEntity>> WOODEN_PUSHCART_ENTITY = ENTITIES.register("wooden_pushcart", () -> EntityType.Builder.<WoodenPushcartEntity>create(WoodenPushcartEntity::new, EntityClassification.MISC ).size(0.98F, 0.3F).build("wooden_pushcart"));
    private static final RegistryObject<EntityType<IronPushcartEntity>> IRON_PUSHCART_ENTITY = ENTITIES.register("iron_pushcart", () -> EntityType.Builder.<IronPushcartEntity>create(IronPushcartEntity::new, EntityClassification.MISC ).size(0.98F, 0.3F).build("iron_pushcart"));
    private static final RegistryObject<EntityType<CouplerEntity>> COUPLER_ENTITY = ENTITIES.register("coupler", () -> EntityType.Builder.<CouplerEntity>create(CouplerEntity::new, EntityClassification.MISC ).size(0.3F, 0.3F).setCustomClientFactory(CouplerClientFactory.get()).build("coupler"));


    public static final EntityType<WoodenPushcartEntity> minecart_with_net = null;
    public static final EntityType<CampfireCartEntity> campfire_cart = null;
    public static final EntityType<CampfireCartEntity> soulfire_cart = null;
    public static final EntityType<WoodenPushcartEntity> wooden_pushcart = null;
    public static final EntityType<IronPushcartEntity> iron_pushcart = null;
    public static final EntityType<CouplerEntity> coupler = null;


    private static final RegistryObject<Block> WOODEN_RAIL_BLOCK = BLOCKS.register("wooden_rail", () -> new WoodenRail(create(Material.WOOD, MaterialColor.WOOD).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.BAMBOO)));
    private static final RegistryObject<Block> BIOLUMINESCENT_RAIL_BLOCK = BLOCKS.register("bioluminescent_rail", () -> new WoodenRail(create(Material.WOOD, MaterialColor.WOOD).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.BAMBOO).setLightLevel((state)->10)));
    private static final RegistryObject<Block> RAIL_TURN = BLOCKS.register("rail_turn", () -> new RailTurn(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL)));
    private static final RegistryObject<Block> WOODEN_RAIL_TURN = BLOCKS.register("wooden_rail_turn", () -> new WoodenRailTurn(create(Material.WOOD, MaterialColor.WOOD).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.BAMBOO)));

    public static final Block wooden_rail = null;
    public static final Block bioluminescent_rail = null;
    public static final Block rail_turn = null;
    public static final Block wooden_rail_turn = null;

    private static final RegistryObject<Item> WOODEN_RAIL_ITEM = ITEMS.register("wooden_rail", () -> new BlockItem(wooden_rail, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> BIOLUMINESCENT_RAIL_ITEM = ITEMS.register("bioluminescent_rail", () -> new BlockItem(bioluminescent_rail, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_TURN_ITEM = ITEMS.register("rail_turn", () -> new BlockItem(rail_turn, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> WOODEN_RAIL_TURN_ITEM = ITEMS.register("wooden_rail_turn", () -> new BlockItem(wooden_rail_turn, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> MINECART_WITH_NET_ITEM = ITEMS.register("minecart_with_net", () -> new MinecartWithNetItem(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> CAMPFIRE_CART_ITEM = ITEMS.register("campfire_cart", () -> new CampfireCartItem(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> SOULFIRE_CART_ITEM = ITEMS.register("soulfire_cart", () -> new SoulfireCartItem(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> WOODEN_PUSHCART_ITEM = ITEMS.register("wooden_pushcart", () -> new WoodenPushcartItem(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> IRON_PUSHCART_ITEM = ITEMS.register("iron_pushcart", () -> new IronPushcartItem(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COUPLER_ITEM = ITEMS.register("coupler", () -> new CouplerItem(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));

    private static final RegistryObject<Item> RAIL_SIGNAL_WHITE = ITEMS.register("rail_signal_white", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_ORANGE = ITEMS.register("rail_signal_orange", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_MAGENTA = ITEMS.register("rail_signal_magenta", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_LIGHT_BLUE = ITEMS.register("rail_signal_light_blue", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_YELLOW = ITEMS.register("rail_signal_yellow", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_LIME = ITEMS.register("rail_signal_lime", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_PINK = ITEMS.register("rail_signal_pink", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_GRAY = ITEMS.register("rail_signal_gray", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_LIGHT_GRAY = ITEMS.register("rail_signal_light_gray", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_CYAN = ITEMS.register("rail_signal_cyan", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_PURPLE = ITEMS.register("rail_signal_purple", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_BLUE = ITEMS.register("rail_signal_blue", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_BROWN = ITEMS.register("rail_signal_brown", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_GREEN = ITEMS.register("rail_signal_green", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_RED = ITEMS.register("rail_signal_red", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> RAIL_SIGNAL_BLACK = ITEMS.register("rail_signal_black", () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION)));

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

    private static final RegistryObject<Block> DETECTOR_RAIL_WHITE = BLOCKS.register("color_detector_rail_white", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_white));
    private static final RegistryObject<Block> DETECTOR_RAIL_ORANGE = BLOCKS.register("color_detector_rail_orange", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_orange));
    private static final RegistryObject<Block> DETECTOR_RAIL_MAGENTA = BLOCKS.register("color_detector_rail_magenta", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_magenta));
    private static final RegistryObject<Block> DETECTOR_RAIL_LIGHT_BLUE = BLOCKS.register("color_detector_rail_light_blue", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_light_blue));
    private static final RegistryObject<Block> DETECTOR_RAIL_YELLOW = BLOCKS.register("color_detector_rail_yellow", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_yellow));
    private static final RegistryObject<Block> DETECTOR_RAIL_LIME = BLOCKS.register("color_detector_rail_lime", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_lime));
    private static final RegistryObject<Block> DETECTOR_RAIL_PINK = BLOCKS.register("color_detector_rail_pink", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_pink));
    private static final RegistryObject<Block> DETECTOR_RAIL_GRAY = BLOCKS.register("color_detector_rail_gray", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_gray));
    private static final RegistryObject<Block> DETECTOR_RAIL_LIGHT_GRAY = BLOCKS.register("color_detector_rail_light_gray", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_light_grey));
    private static final RegistryObject<Block> DETECTOR_RAIL_CYAN = BLOCKS.register("color_detector_rail_cyan", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_cyan));
    private static final RegistryObject<Block> DETECTOR_RAIL_PURPLE = BLOCKS.register("color_detector_rail_purple", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_purple));
    private static final RegistryObject<Block> DETECTOR_RAIL_BLUE = BLOCKS.register("color_detector_rail_blue", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_blue));
    private static final RegistryObject<Block> DETECTOR_RAIL_BROWN = BLOCKS.register("color_detector_rail_brown", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_brown));
    private static final RegistryObject<Block> DETECTOR_RAIL_GREEN = BLOCKS.register("color_detector_rail_green", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_green));
    private static final RegistryObject<Block> DETECTOR_RAIL_RED = BLOCKS.register("color_detector_rail_red", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_red));
    private static final RegistryObject<Block> DETECTOR_RAIL_BLACK = BLOCKS.register("color_detector_rail_black", () -> new ColorDetectorRailBlock(create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL), ()->rail_signal_black));

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

    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_WHITE = ITEMS.register("color_detector_rail_white", () -> new BlockItem(color_detector_rail_white, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_ORANGE = ITEMS.register("color_detector_rail_orange", () -> new BlockItem(color_detector_rail_orange, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_MAGENTA = ITEMS.register("color_detector_rail_magenta", () -> new BlockItem(color_detector_rail_magenta, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_LIGHT_BLUE = ITEMS.register("color_detector_rail_light_blue", () -> new BlockItem(color_detector_rail_light_blue, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_YELLOW = ITEMS.register("color_detector_rail_yellow", () -> new BlockItem(color_detector_rail_yellow, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_LIME = ITEMS.register("color_detector_rail_lime", () -> new BlockItem(color_detector_rail_lime, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_PINK = ITEMS.register("color_detector_rail_pink", () -> new BlockItem(color_detector_rail_pink, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_GRAY = ITEMS.register("color_detector_rail_gray", () -> new BlockItem(color_detector_rail_gray, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_LIGHT_GRAY = ITEMS.register("color_detector_rail_light_gray", () -> new BlockItem(color_detector_rail_light_gray, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_CYAN = ITEMS.register("color_detector_rail_cyan", () -> new BlockItem(color_detector_rail_cyan, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_PURPLE = ITEMS.register("color_detector_rail_purple", () -> new BlockItem(color_detector_rail_purple, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_BLUE = ITEMS.register("color_detector_rail_blue", () -> new BlockItem(color_detector_rail_blue, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_BROWN = ITEMS.register("color_detector_rail_brown", () -> new BlockItem(color_detector_rail_brown, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_GREEN = ITEMS.register("color_detector_rail_green", () -> new BlockItem(color_detector_rail_green, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_RED = ITEMS.register("color_detector_rail_red", () -> new BlockItem(color_detector_rail_red, new Item.Properties().group(ItemGroup.TRANSPORTATION)));
    private static final RegistryObject<Item> COLOR_DETECTOR_RAIL_ITEM_BLACK = ITEMS.register("color_detector_rail_black", () -> new BlockItem(color_detector_rail_black, new Item.Properties().group(ItemGroup.TRANSPORTATION)));


    public ExampleMod() {

        CouplerPacketHandler.Init();

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

    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("BLOCK >> {}", WOODEN_RAIL_BLOCK.getId());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        //LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);

        RenderType cutout = RenderType.getCutout();
        RenderTypeLookup.setRenderLayer(wooden_rail, cutout);
        RenderTypeLookup.setRenderLayer(bioluminescent_rail, cutout);
        RenderTypeLookup.setRenderLayer(rail_turn, cutout);
        RenderTypeLookup.setRenderLayer(wooden_rail_turn, cutout);

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
        RenderingRegistry.registerEntityRenderingHandler(campfire_cart, CampfireCartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(soulfire_cart, SoulfireCartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(wooden_pushcart, WoodenPushcartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(iron_pushcart, IronPushcartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(coupler, CouplerRenderer::new);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        //InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
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
