package com.alc.moreminecarts.blocks;

import com.alc.moreminecarts.MMItemReferences;
import com.alc.moreminecarts.MMReferences;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.extensions.IForgeBlock;

import java.util.Random;

public class GlassCactusBlock extends CactusBlock implements IForgeBlock {

    public GlassCactusBlock(Properties p_i48435_1_) {
        super(p_i48435_1_);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    // Grows 2x as slow.
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rand) {
        if (rand.nextInt(2) == 0) super.randomTick(state, world, pos, rand);
    }

    // Dies outside of desert biomes.
    @Override
    public  boolean canSurvive(BlockState state, LevelReader world_reader, BlockPos pos) {

        Biome.BiomeCategory category = world_reader.getBiome(pos).getBiomeCategory();

        return (category == Biome.BiomeCategory.DESERT || category == Biome.BiomeCategory.MESA || category == Biome.BiomeCategory.NONE)
                && super.canSurvive(state, world_reader, pos);
    }

    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {

        // Turn into broken remote if entity is holo remote item.
        if (entity instanceof ItemEntity) {
            ItemStack item_stack = ((ItemEntity)entity).getItem();
            Item item = item_stack.getItem();

            if (item == MMItemReferences.holo_remote || item == MMItemReferences.backwards_holo_remote || item == MMItemReferences.simple_holo_remote){
                ((ItemEntity)entity).setItem(new ItemStack(MMItemReferences.broken_holo_remote, item_stack.getCount()));
                return;
            }
            else if (item == MMItemReferences.broken_holo_remote) return;
        }
        entity.hurt(DamageSource.CACTUS, 2.0F);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {

        BlockState plant = plantable.getPlant(world, pos.relative(facing));

        if (plant.getBlock() == MMReferences.glass_cactus && state.is(MMReferences.glass_cactus))
            return true;

        return super.canSustainPlant(state, world, pos, facing, plantable);
    }
}
