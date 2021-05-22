package com.alc.moreminecarts;

import com.alc.moreminecarts.containers.ChunkLoaderContainer;
import com.alc.moreminecarts.containers.MinecartUnLoaderContainer;
import com.alc.moreminecarts.entities.*;
import com.alc.moreminecarts.tile_entities.ChunkLoaderTile;
import com.alc.moreminecarts.tile_entities.LockingRailTile;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("moreminecarts")
public class MMReferences {

    private MMReferences() {

    }

    // Entities

    public static final EntityType<NetMinecartEntity> minecart_with_net = null;
    public static final EntityType<ChunkLoaderCartEntity> minecart_with_chunk_loader = null;
    public static final EntityType<CampfireCartEntity> campfire_cart = null;
    public static final EntityType<SoulfireCartEntity> soulfire_cart = null;
    public static final EntityType<WoodenPushcartEntity> wooden_pushcart = null;
    public static final EntityType<IronPushcartEntity> iron_pushcart = null;
    public static final EntityType<PistonPushcartEntity> piston_pushcart = null;
    public static final EntityType<StickyPistonPushcartEntity> sticky_piston_pushcart = null;
    public static final EntityType<CouplerEntity> coupler = null;

    // Tile Entities

    public static final TileEntityType<ChunkLoaderTile> chunk_loader_te = null;
    public static final TileEntityType<LockingRailTile> locking_rail_te = null;
    public static final TileEntityType<LockingRailTile> powered_locking_rail_te = null;
    public static final TileEntityType<LockingRailTile> minecart_loader_te = null;
    public static final TileEntityType<LockingRailTile> minecart_unloader_te = null;

    // Containers

    public static final ContainerType<ChunkLoaderContainer> chunk_loader_c = null;
    public static final ContainerType<MinecartUnLoaderContainer> minecart_loader_c = null;

    // High Speed Entities

    public static final EntityType<HSMinecartEntities.HSMinecart> high_speed_minecart = null;
    public static final EntityType<HSMinecartEntities.HSChestMinecart> high_speed_chest_minecart = null;
    public static final EntityType<HSMinecartEntities.HSTNTMinecart> high_speed_tnt_minecart = null;
    public static final EntityType<HSMinecartEntities.HSCommandBlockMinecart> high_speed_command_block_minecart = null;
    public static final EntityType<HSMinecartEntities.HSHopperMinecart> high_speed_hopper_minecart = null;
    public static final EntityType<HSMinecartEntities.HSSpawnerMinecart> high_speed_spawner_minecart = null;
    public static final EntityType<HSMinecartEntities.HSFurnaceMinecart> high_speed_furnace_minecart = null;
    public static final EntityType<HSMinecartEntities.HSNetMinecart> high_speed_net_minecart = null;
    public static final EntityType<HSMinecartEntities.HSChunkLoaderMinecart> high_speed_chunk_loader_minecart = null;
    public static final EntityType<HSMinecartEntities.HSCampfireMinecart> high_speed_campfire_minecart = null;
    public static final EntityType<HSMinecartEntities.HSSoulfireMinecart> high_speed_soulfire_minecart = null;
    public static final EntityType<HSMinecartEntities.HSPushcart> high_speed_pushcart = null;
    public static final EntityType<HSMinecartEntities.HSPistonPushcart> high_speed_piston_pushcart = null;
    public static final EntityType<HSMinecartEntities.HSStickyPistonPushcart> high_speed_sticky_piston_pushcart = null;

    // Blocks
    public static final Block rail_turn = null;
    public static final Block parallel_rail = null;
    public static final Block projector_rail = null;
    public static final Block hologram_rail = null;
    public static final Block wooden_rail = null;
    public static final Block wooden_rail_turn = null;
    public static final Block wooden_parallel_rail = null;
    public static final Block wooden_projector_rail = null;
    public static final Block wooden_hologram_rail = null;
    public static final Block maglev_rail = null;
    public static final Block maglev_rail_turn = null;
    public static final Block maglev_parallel_rail = null;
    public static final Block maglev_projector_rail = null;
    public static final Block maglev_hologram_rail = null;
    public static final Block maglev_powered_rail = null;
    public static final Block lightspeed_rail = null;
    public static final Block lightspeed_powered_rail = null;
    public static final Block bioluminescent_rail = null;
    public static final Block locking_rail = null;
    public static final Block powered_locking_rail = null;

    public static final Block holo_scaffold_generator = null;
    public static final Block holo_scaffold = null;
    public static final Block chaotic_holo_scaffold = null;
    public static final Block piston_display_block = null;

    public static final Block chunk_loader = null;
    public static final Block minecart_loader = null;
    public static final Block minecart_unloader = null;
    public static final Block silica_steel_block = null;
    public static final Block chunkrodite_block = null;
    public static final Block glass_cactus = null;
    public static final Block potted_glass_cactus = null;
    public static final Block potted_beet = null;

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

}
