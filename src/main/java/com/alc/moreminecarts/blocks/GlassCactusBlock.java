package com.alc.moreminecarts.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.entity.Entity;
import net.minecraft.state.StateContainer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.extensions.IForgeBlock;

import java.util.Random;

public class GlassCactusBlock extends CactusBlock implements IForgeBlock {

    public GlassCactusBlock(Properties p_i48435_1_) {
        super(p_i48435_1_);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    // Grows 3x as slow.
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        if (rand.nextInt(3) == 0) super.randomTick(state, world, pos, rand);
    }

    // Dies outside of desert biomes.
    public boolean canSurvive(BlockState state, IWorldReader world_reader, BlockPos pos) {
        return world_reader.getBiome(pos).getBiomeCategory() == Biome.Category.DESERT && super.canSurvive(state, world_reader, pos);
    }

    public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
        p_196262_4_.hurt(DamageSource.CACTUS, 2.0F);
    }
}
