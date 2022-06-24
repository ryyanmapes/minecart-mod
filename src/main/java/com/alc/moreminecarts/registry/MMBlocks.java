package com.alc.moreminecarts.registry;

import com.alc.moreminecarts.MMConstants;
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
import com.alc.moreminecarts.blocks.rail_turns.MaglevRailTurn;
import com.alc.moreminecarts.blocks.rail_turns.RailTurn;
import com.alc.moreminecarts.blocks.rail_turns.WoodenRailTurn;
import com.alc.moreminecarts.blocks.rails.LightspeedRail;
import com.alc.moreminecarts.blocks.rails.MaglevRail;
import com.alc.moreminecarts.blocks.rails.WoodenRail;
import com.alc.moreminecarts.blocks.utility_rails.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ToIntFunction;

public class MMBlocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MMConstants.modid);

    // Rail Blocks
    public static final RegistryObject<Block> RAIL_TURN = BLOCKS.register("rail_turn", () -> new RailTurn(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> PARALLEL_RAIL_BLOCK = BLOCKS.register("parallel_rail", () -> new ParallelRail(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> CROSS_RAIL_BLOCK = BLOCKS.register("cross_rail", () -> new RailCrossing(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> PROJECTOR_RAIL = BLOCKS.register("projector_rail", () -> new ProjectorRail(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> HOLOGRAM_RAIL = BLOCKS.register("hologram_rail", () -> new HolographicRail(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.2F).sound(SoundType.GLASS)));
    public static final RegistryObject<Block> WOODEN_RAIL_BLOCK = BLOCKS.register("wooden_rail", () -> new WoodenRail(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.7F).sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> WOODEN_RAIL_TURN = BLOCKS.register("wooden_rail_turn", () -> new WoodenRailTurn(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.7F).sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> WOODEN_PARALLEL_RAIL_BLOCK = BLOCKS.register("wooden_parallel_rail", () -> new WoodenParallelRail(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.7F).sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> WOODEN_CROSS_RAIL_BLOCK = BLOCKS.register("wooden_cross_rail", () -> new WoodenRailCrossing(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.7F).sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> WOODEN_PROJECTOR_RAIL = BLOCKS.register("wooden_projector_rail", () -> new WoodenProjectorRail(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.7F).sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> WOODEN_HOLOGRAM_RAIL = BLOCKS.register("wooden_hologram_rail", () -> new WoodenHolographicRail(BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(0.2F).sound(SoundType.GLASS)));
    public static final RegistryObject<Block> MAGLEV_RAIL_BLOCK = BLOCKS.register("maglev_rail", () -> new MaglevRail(BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().strength(0.7F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> MAGLEV_RAIL_TURN = BLOCKS.register("maglev_rail_turn", () -> new MaglevRailTurn(BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().strength(0.7F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> MAGLEV_PARALLEL_RAIL_BLOCK = BLOCKS.register("maglev_parallel_rail", () -> new MaglevParallelRail(BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().strength(0.7F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> MAGLEV_CROSS_RAIL_BLOCK = BLOCKS.register("maglev_cross_rail", () -> new MaglevRailCrossing(BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().strength(0.7F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> MAGLEV_PROJECTOR_RAIL = BLOCKS.register("maglev_projector_rail", () -> new MaglevProjectorRail(BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().strength(0.7F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> MAGLEV_HOLOGRAM_RAIL = BLOCKS.register("maglev_hologram_rail", () -> new MaglevHolographicRail(BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().strength(0.2F).sound(SoundType.GLASS)));
    public static final RegistryObject<Block> MAGLEV_POWERED_RAIL_BLOCK = BLOCKS.register("maglev_powered_rail", () -> new PoweredMaglevRail(BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().strength(0.7F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> LIGHTSPEED_RAIL_BLOCK = BLOCKS.register("lightspeed_rail", () -> new LightspeedRail(BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().strength(1F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> LIGHTSPEED_CROSS_RAIL_BLOCK = BLOCKS.register("lightspeed_cross_rail", () -> new LightspeedRailCrossing(BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().strength(1F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> LIGHTSPEED_POWERED_RAIL_BLOCK = BLOCKS.register("lightspeed_powered_rail", () -> new PoweredLightspeedRail(BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.COLOR_BLUE).noCollission().strength(1F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> BIOLUMINESCENT_RAIL_BLOCK = BLOCKS.register("bioluminescent_rail", () -> new WoodenRail(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.BAMBOO).lightLevel((state)->10)));
    public static final RegistryObject<Block> LOCKING_RAIL_BLOCK = BLOCKS.register("locking_rail", () -> new LockingRailBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> POWERED_LOCKING_RAIL_BLOCK = BLOCKS.register("powered_locking_rail", () -> new PoweredLockingRailBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> PISTON_LIFTER_RAIL = BLOCKS.register("piston_lifter_rail", () -> new PistonLifterRailBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> ARITHMETIC_RAIL = BLOCKS.register("arithmetic_rail", () -> new ArithmeticRailBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL)));

    // Container Blocks
    public static final RegistryObject<Block> CHUNK_LOADER_BLOCK = BLOCKS.register("chunk_loader", () -> new ChunkLoaderBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GREEN).strength(5f).noOcclusion().lightLevel(poweredBlockEmission(13))));
    public static final RegistryObject<Block> MINECART_LOADER_BLOCK = BLOCKS.register("minecart_loader", () -> new MinecartLoaderBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3f)));
    public static final RegistryObject<Block> MINECART_UNLOADER_BLOCK = BLOCKS.register("minecart_unloader", () -> new MinecartUnloaderBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(3f)));
    public static final RegistryObject<Block> PEARL_STASIS_CHAMBER = BLOCKS.register("pearl_stasis_chamber", () -> new OrbStasisBlock(BlockBehaviour.Properties.of(Material.ICE_SOLID, MaterialColor.COLOR_PURPLE).strength(5f).noOcclusion()));

    // Other Blocks
    public static final RegistryObject<Block> SILICA_STEEL_BLOCK = BLOCKS.register("silica_steel_block", () -> new Block( BlockBehaviour.Properties.of(Material.METAL).strength(3f,3f).requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final RegistryObject<Block> CHUNKRODITE_BLOCK = BLOCKS.register("chunkrodite_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE).strength(2f, 2f)));
    public static final RegistryObject<Block> CORRUGATED_SILICA_STEEL = BLOCKS.register("corrugated_silica_steel", () -> new Block(BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().sound(SoundType.METAL).strength(2f, 2f)));
    public static final RegistryObject<Block> SILICA_STEEL_PILLAR = BLOCKS.register("silica_steel_pillar", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().sound(SoundType.METAL).strength(2f, 2f)));
    public static final RegistryObject<Block> ORGANIC_GLASS = BLOCKS.register("organic_glass", () -> new GlassBlock(BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS).strength(0.3F).sound(SoundType.GLASS).noOcclusion().isValidSpawn((a, b, c, d)->false).isRedstoneConductor((a, b, c)->false).isSuffocating((a, b, c)->false).isViewBlocking((a, b, c)->false)));
    public static final RegistryObject<Block> ORGANIC_GLASS_PANE = BLOCKS.register("organic_glass_pane", () -> new IronBarsBlock(BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS).strength(0.3F).sound(SoundType.GLASS).noOcclusion().isValidSpawn((a, b, c, d)->false).isRedstoneConductor((a, b, c)->false).isSuffocating((a, b, c)->false).isViewBlocking((a, b, c)->false)));
    public static final RegistryObject<Block> CHISELED_ORGANIC_GLASS = BLOCKS.register("chiseled_organic_glass", () -> new GlassBlock(BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS).strength(0.3F).sound(SoundType.GLASS).noOcclusion().isValidSpawn((a,b,c,d)->false).isRedstoneConductor((a,b,c)->false).isSuffocating((a,b,c)->false).isViewBlocking((a,b,c)->false)));
    public static final RegistryObject<Block> CHISELED_ORGANIC_GLASS_PANE = BLOCKS.register("chiseled_organic_glass_pane", () -> new IronBarsBlock(BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS).strength(0.3F).sound(SoundType.GLASS).noOcclusion().isValidSpawn((a,b,c,d)->false).isRedstoneConductor((a,b,c)->false).isSuffocating((a,b,c)->false).isViewBlocking((a,b,c)->false)));
    public static final RegistryObject<Block> GLASS_CACTUS = BLOCKS.register("glass_cactus", () -> new GlassCactusBlock(BlockBehaviour.Properties.of(Material.CACTUS).randomTicks().strength(2F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> HOLO_SCAFFOLD_GENERATOR = BLOCKS.register("holo_scaffold_generator", () -> new Block(BlockBehaviour.Properties.of(Material.HEAVY_METAL, MaterialColor.COLOR_GRAY).strength(3f,3f).lightLevel((state) -> 13)));
    public static final RegistryObject<Block> HOLO_SCAFFOLD = BLOCKS.register("holo_scaffold", () -> new HoloScaffold(BlockBehaviour.Properties.of(Material.DECORATION).strength(0.05F).noOcclusion().dynamicShape()));
    public static final RegistryObject<Block> CHAOTIC_HOLO_SCAFFOLD = BLOCKS.register("chaotic_holo_scaffold", () -> new ChaoticHoloScaffold(BlockBehaviour.Properties.of(Material.DECORATION).strength(0.05F).noOcclusion().dynamicShape()));
    public static final RegistryObject<Block> PISTON_DISPLAY_BLOCK = BLOCKS.register("piston_display_block", () -> new PistonDisplayBlock(BlockBehaviour.Properties.of(Material.DECORATION)));

    // Potted Plants
    public static final RegistryObject<Block> POTTED_GLASS_CACTUS = BLOCKS.register("potted_glass_cactus", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, MMBlocks.GLASS_CACTUS, BlockBehaviour.Properties.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final RegistryObject<Block> POTTED_BEET = BLOCKS.register("potted_beet", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, MMBlocks.CHUNKRODITE_BLOCK, BlockBehaviour.Properties.of(Material.DECORATION).instabreak().noOcclusion()));


    // Color Detector Rail Blocks
    public static final Map<DyeColor, RegistryObject<Block>> COLOR_DETECTOR_RAILS = new HashMap<>();
    static {
        for (DyeColor color : DyeColor.values()) {
            RegistryObject<Block> entry = BLOCKS.register("color_detector_rail_" + color.getName(), () -> new ColorDetectorRailBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL), MMItems.RAIL_SIGNALS.get(color)));
            COLOR_DETECTOR_RAILS.put(color, entry);
        }
    }

    // Taken from Blocks
    private static ToIntFunction<BlockState> poweredBlockEmission(int p_235420_0_) {
        return (p_235421_1_) -> {
            return p_235421_1_.getValue(BlockStateProperties.POWERED) ? p_235420_0_ : 0;
        };
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
