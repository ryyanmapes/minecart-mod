package com.example.examplemod.datagen;

import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraft.block.Block;

@ObjectHolder("examplemod")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = "examplemod")
public class RailDataGen {

    public static final net.minecraft.block.Block wooden_rail = null;

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        if (event.includeServer()) {
            DataGenerator gen = event.getGenerator();
            ExistingFileHelper xfh = event.getExistingFileHelper();

            gen.addProvider(new Block(gen, xfh));
        }
    }


    public static class Block extends BlockTagsProvider {
        public Block(DataGenerator gen, ExistingFileHelper existingFileHelper) {
            super(gen, "examplemod", existingFileHelper);
        }

        @Override
        protected void registerTags() {
            getOrCreateBuilder(BlockTags.RAILS).add(wooden_rail);
        }
    }

}
