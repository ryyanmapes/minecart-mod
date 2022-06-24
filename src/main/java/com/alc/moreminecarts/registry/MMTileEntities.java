package com.alc.moreminecarts.registry;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.tile_entities.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MMTileEntities {
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MMConstants.modid);

    // Tile Entities
    public static final RegistryObject<BlockEntityType<ChunkLoaderTile>> CHUNK_LOADER_TILE_ENTITY = TILE_ENTITIES.register("chunk_loader_te", () -> BlockEntityType.Builder.of(ChunkLoaderTile::new, MMBlocks.CHUNK_LOADER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<LockingRailTile>> LOCKING_RAIL_TILE_ENTITY = TILE_ENTITIES.register("locking_rail_te", () -> BlockEntityType.Builder.of(LockingRailTile::new, MMBlocks.LOCKING_RAIL_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<PoweredLockingRailTile>> POWERED_LOCKING_RAIL_TILE_ENTITY = TILE_ENTITIES.register("powered_locking_rail_te", () -> BlockEntityType.Builder.of(PoweredLockingRailTile::new, MMBlocks.POWERED_LOCKING_RAIL_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<MinecartLoaderTile>> MINECART_LOADER_TILE_ENTITY = TILE_ENTITIES.register("minecart_loader_te", () -> BlockEntityType.Builder.of(MinecartLoaderTile::new, MMBlocks.MINECART_LOADER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<MinecartUnloaderTile>> MINECART_UNLOADER_TILE_ENTITY = TILE_ENTITIES.register("minecart_unloader_te", () -> BlockEntityType.Builder.of(MinecartUnloaderTile::new, MMBlocks.MINECART_UNLOADER_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<OrbStasisTile>> PEARL_STASIS_CHAMBER_TILE_ENTITY = TILE_ENTITIES.register("pearl_stasis_chamber_te", () -> BlockEntityType.Builder.<OrbStasisTile>of(OrbStasisTile::new, MMBlocks.PEARL_STASIS_CHAMBER.get()).build(null));

    public static void register(IEventBus bus) {
        TILE_ENTITIES.register(bus);
    }
}
