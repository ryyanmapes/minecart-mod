package com.alc.moreminecarts;

import com.alc.moreminecarts.client.*;
import com.alc.moreminecarts.proxy.ClientProxy;
import com.alc.moreminecarts.proxy.IProxy;
import com.alc.moreminecarts.proxy.MoreMinecartsPacketHandler;
import com.alc.moreminecarts.proxy.ServerProxy;
import com.alc.moreminecarts.registry.*;
import com.alc.moreminecarts.renderers.*;
import com.alc.moreminecarts.renderers.highspeed.HSMinecartRenderer;
import com.alc.moreminecarts.renderers.highspeed.HSPistonPushcartRenderer;
import com.alc.moreminecarts.renderers.highspeed.HSPushcartRenderer;
import com.alc.moreminecarts.renderers.highspeed.HSStickyPistonPushcartRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;


// The value here should match an entry in the META-INF/mods.toml file
@Mod("moreminecarts")
@Mod.EventBusSubscriber(modid = MMConstants.modid, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MoreMinecartsMod {
    // Directly reference a log4j logger.
    public static Logger LOGGER = LogManager.getLogger();
    public static IProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public MoreMinecartsMod() {

        /*
        if (EndergeticCompat.endergeticInstalled()) {
            ENDFIRE_CART_ENTITY = ENTITIES.register("endfire_cart", () -> EntityType.Builder.<EndfireCartEntity>of(EndfireCartEntity::new, MobCategory.MISC ).sized(0.98F, 0.7F).build("endfire_cart"));
            ENDFIRE_CART_ITEM = ITEMS.register("endfire_cart", () -> new EndfireCartItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TRANSPORTATION)));
            HS_ENDFIRE_CART_ENTITY = ENTITIES.register("high_speed_endfire_minecart", () -> EntityType.Builder.<HSEndfireMinecart>of(HSEndfireMinecart::new, MobCategory.MISC ).sized(0.98F, 0.7F).build("high_speed_endfire_minecart"));
        }*/

        MoreMinecartsPacketHandler.Init();

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerEntityRenderers);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        MMEntities.register(bus);
        MMBlocks.register(bus);
        MMItems.register(bus);
        MMTileEntities.register(bus);
        MMContainers.register(bus);

        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        MMConstants.CONFIG_CHUNK_LOADER_MULTIPLIER = builder.defineInRange("chunk_loader_multiplier", () -> 1.0D, 0, 9999);
        builder.comment("How often vitric cactus spawns. A larger number is more rare. Default cactus is 6-13, set to zero to disable.");
        MMConstants.CONFIG_GLASS_CACTUS_SPAWNS = builder.defineInRange("vitric_cactus_rarity", () -> 100, 0, 9999);
        builder.comment("Requires that vitric cactus be grown only in desert and mesa biomes.");
        MMConstants.CONFIG_GLASS_CACTUS_DESERT_ONLY = builder.define("vitric_cactus_desert_only", true);
        builder.comment("Sets the max speed of various rail types. Default rails are 0.4.");
        MMConstants.CONFIG_WOOD_RAILS_MAX_SPEED = builder.defineInRange("wood_rails_max_speed", () -> 0.2D, 0.1, 10);
        MMConstants.CONFIG_MAGLEV_RAILS_MAX_SPEED = builder.defineInRange("maglev_rails_max_speed", () -> 1.0D, 0.1, 10);
        MMConstants.CONFIG_LIGHTSPEED_RAILS_MAX_SPEED = builder.defineInRange("lightspeed_rails_max_speed", () -> 2.5D, 0.1, 10);
        builder.comment("Sets the extra speed boost given by turbo rails. 0.06 is the default for regular powered rails.");
        MMConstants.CONFIG_TURBO_BOOST = builder.defineInRange("turbo_rails_boost", () -> 0.2D, 0, 1);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, builder.build(), "moreminecartsconfig.toml");

    }

    private void setup(final FMLCommonSetupEvent event) {
        ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation("moreminecarts:chunkrodite_block"), MMBlocks.POTTED_BEET);
        ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(new ResourceLocation("moreminecarts:glass_cactus"), MMBlocks.POTTED_GLASS_CACTUS);

        int actualGlassCactusRarity = MMConstants.CONFIG_GLASS_CACTUS_SPAWNS.get();
        if (actualGlassCactusRarity == 0) actualGlassCactusRarity = 100;

        MMConstants.WOODEN_MAX_SPEED = MMConstants.CONFIG_WOOD_RAILS_MAX_SPEED.get().floatValue();
        MMConstants.MAGLEV_MAX_SPEED = MMConstants.CONFIG_MAGLEV_RAILS_MAX_SPEED.get().floatValue();
        MMConstants.LIGHTSPEED_MAX_SPEED = MMConstants.CONFIG_LIGHTSPEED_RAILS_MAX_SPEED.get().floatValue();
        MMConstants.POWERED_LIGHTSPEED_BOOST = MMConstants.CONFIG_TURBO_BOOST.get().floatValue();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        //LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);

        PROXY.setupKeybindings();

        event.enqueueWork(
                () -> {
                    MenuScreens.register(MMContainers.CHUNK_LOADER_CONTAINER.get(), ChunkLoaderScreen::new);
                    MenuScreens.register(MMContainers.MINECART_LOADER_CONTAINER.get(), MinecartUnLoaderScreen::new);
                    MenuScreens.register(MMContainers.TANK_CART_CONTAINER.get(), TankCartScreen::new);
                    MenuScreens.register(MMContainers.BATTERY_CART_CONTAINER.get(), BatteryCartScreen::new);
                    MenuScreens.register(MMContainers.FLAG_CART_CONTAINER.get(), FlagCartScreen::new);

                    RenderType cutout = RenderType.cutout();
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.RAIL_TURN.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.PARALLEL_RAIL_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.CROSS_RAIL_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.PROJECTOR_RAIL.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.HOLOGRAM_RAIL.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.WOODEN_RAIL_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.WOODEN_RAIL_TURN.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.WOODEN_PARALLEL_RAIL_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.WOODEN_CROSS_RAIL_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.WOODEN_PROJECTOR_RAIL.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.WOODEN_HOLOGRAM_RAIL.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.MAGLEV_RAIL_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.MAGLEV_RAIL_TURN.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.MAGLEV_PARALLEL_RAIL_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.MAGLEV_CROSS_RAIL_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.MAGLEV_PROJECTOR_RAIL.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.MAGLEV_HOLOGRAM_RAIL.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.MAGLEV_POWERED_RAIL_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.LIGHTSPEED_RAIL_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.LIGHTSPEED_CROSS_RAIL_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.LIGHTSPEED_POWERED_RAIL_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.BIOLUMINESCENT_RAIL_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.LOCKING_RAIL_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.POWERED_LOCKING_RAIL_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.PISTON_LIFTER_RAIL.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.ARITHMETIC_RAIL.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.CHUNK_LOADER_BLOCK.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.PEARL_STASIS_CHAMBER.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.HOLO_SCAFFOLD.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.CHAOTIC_HOLO_SCAFFOLD.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.GLASS_CACTUS.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.POTTED_BEET.get(), cutout);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.POTTED_GLASS_CACTUS.get(), cutout);
                    
                    for (Map.Entry<DyeColor, RegistryObject<Block>> entry : MMBlocks.COLOR_DETECTOR_RAILS.entrySet()) {
                        ItemBlockRenderTypes.setRenderLayer(entry.getValue().get(), cutout);
                    }

                    RenderType transparent = RenderType.translucent();
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.ORGANIC_GLASS.get(), transparent);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.ORGANIC_GLASS_PANE.get(), transparent);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.CHISELED_ORGANIC_GLASS.get(), transparent);
                    ItemBlockRenderTypes.setRenderLayer(MMBlocks.CHISELED_ORGANIC_GLASS_PANE.get(), transparent);
                }
        );

    }

    @SubscribeEvent
    public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {

        evt.registerEntityRenderer(MMEntities.MINECART_WITH_NET_ENTITY.get(), VanillaMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.CHUNK_LOADER_CART.get(), VanillaMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.ORB_STASIS_CART.get(), VanillaMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.FLAG_CART.get(), VanillaMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.CAMPFIRE_CART_ENTITY.get(), CampfireCartRenderer::new);
        evt.registerEntityRenderer(MMEntities.SOULFIRE_CART_ENTITY.get(), SoulfireCartRenderer::new);
        evt.registerEntityRenderer(MMEntities.WOODEN_PUSHCART_ENTITY.get(), WoodenPushcartRenderer::new);
        evt.registerEntityRenderer(MMEntities.IRON_PUSHCART_ENTITY.get(), IronPushcartRenderer::new);
        evt.registerEntityRenderer(MMEntities.PISTON_PUSHCART_ENTITY.get(), PistonPushcartRenderer::new);
        evt.registerEntityRenderer(MMEntities.STICKY_PISTON_PUSHCART_ENTITY.get(), StickyPistonPushcartRenderer::new);
        evt.registerEntityRenderer(MMEntities.TANK_CART_ENTITY.get(), VanillaMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.BATTERY_CART_ENTITY.get(), VanillaMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.COUPLER_ENTITY.get(), CouplerRenderer::new);

        evt.registerEntityRenderer(MMEntities.HS_CART_ENTITY.get(), HSMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_CHEST_CART_ENTITY.get(), HSMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_TNT_CART_ENTITY.get(), HSMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_COMMAND_BLOCK_CART_ENTITY.get(), HSMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_HOPPER_CART_ENTITY.get(), HSMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_SPAWNER_CART_ENTITY.get(), HSMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_FURNACE_CART_ENTITY.get(), HSMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_NET_CART_ENTITY.get(), HSMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_CHUNK_LOADER_CART_ENTITY.get(), HSMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_STASIS_CART_ENTITY.get(), HSMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_FLAG_CART_ENTITY.get(), HSMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_TANK_CART_ENTITY.get(), HSMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_BATTERY_CART_ENTITY.get(), HSMinecartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_CAMPFIRE_CART_ENTITY.get(), HSPushcartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_SOULFIRE_CART_ENTITY.get(), HSPushcartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_PUSHCART_ENTITY.get(), HSPushcartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_PISTON_PUSHCART_ENTITY.get(), HSPistonPushcartRenderer::new);
        evt.registerEntityRenderer(MMEntities.HS_STICKY_PISTON_PUSHCART_ENTITY.get(), HSStickyPistonPushcartRenderer::new);

    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        //InterModComms.sendTo("moreminecarts", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        //LOGGER.info("Got IMC {}", event.getIMCStream().
        //        map(m->m.getMessageSupplier().get()).
        //        collect(Collectors.toList()));
    }
}
