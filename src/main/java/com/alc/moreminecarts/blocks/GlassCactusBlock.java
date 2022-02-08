package com.alc.moreminecarts.blocks;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.MMItemReferences;
import com.alc.moreminecarts.MMReferences;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.extensions.IForgeBlock;

import java.util.Random;

public class GlassCactusBlock extends CactusBlock implements IForgeBlock {

    public GlassCactusBlock(Properties p_i48435_1_) {
        super(p_i48435_1_);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    // Grows 2x as slow.
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        if (rand.nextInt(2) == 0) super.randomTick(state, world, pos, rand);
    }

    // Dies outside of desert biomes.
    public boolean canSurvive(BlockState state, IWorldReader world_reader, BlockPos pos) {

        Biome.Category category = world_reader.getBiome(pos).getBiomeCategory();

        return (category == Biome.Category.DESERT || category == Biome.Category.NONE || !MMConstants.CONFIG_GLASS_CACTUS_DESERT_ONLY.get())
                && super.canSurvive(state, world_reader, pos);
    }

    public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {

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
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {

        BlockState plant = plantable.getPlant(world, pos.relative(facing));

        if (plant.getBlock() == MMReferences.glass_cactus && state.is(MMReferences.glass_cactus))
            return true;

        return super.canSustainPlant(state, world, pos, facing, plantable);
    }
}
