package com.alc.moreminecarts.blocks;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.registry.MMBlocks;
import com.alc.moreminecarts.registry.MMItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.extensions.IForgeBlock;

public class GlassCactusBlock extends CactusBlock implements IForgeBlock {

    public GlassCactusBlock(Properties p_i48435_1_) {
        super(p_i48435_1_);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }



    // Dies outside of desert biomes.
    @Override
    public  boolean canSurvive(BlockState state, LevelReader world_reader, BlockPos pos) {

        Holder<Biome> biome = world_reader.getBiome(pos);

        var canSurviveInBiome = !MMConstants.CONFIG_GLASS_CACTUS_DESERT_ONLY.get() ||
                (biome.is(Biomes.DESERT) || biome.is(BiomeTags.IS_BADLANDS));

        return canSurviveInBiome && super.canSurvive(state, world_reader, pos);
    }

    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {

        // Turn into broken remote if entity is holo remote item.
        if (entity instanceof ItemEntity) {
            ItemStack item_stack = ((ItemEntity)entity).getItem();
            Item item = item_stack.getItem();

            if (item == MMItems.HOLO_REMOTE_ITEM.get() || item == MMItems.BACKWARDS_HOLO_REMOTE_ITEM.get() || item == MMItems.SIMPLE_HOLO_REMOTE_ITEM.get()){
                ((ItemEntity)entity).setItem(new ItemStack(MMItems.BROKEN_HOLO_REMOTE_ITEM.get(), item_stack.getCount()));
                return;
            }
            else if (item == MMItems.BROKEN_HOLO_REMOTE_ITEM.get()) return;
        }
        entity.hurt(world.damageSources().cactus(), 2.0F);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {

        BlockState plant = plantable.getPlant(world, pos.relative(facing));

        Block glassCactus = MMBlocks.GLASS_CACTUS.get();

        if (plant.getBlock() == glassCactus && state.is(glassCactus))
            return true;

        return super.canSustainPlant(state, world, pos, facing, plantable);
    }
}
