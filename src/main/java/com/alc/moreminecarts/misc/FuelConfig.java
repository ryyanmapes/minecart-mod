package com.alc.moreminecarts.misc;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.MMItemReferences;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.ItemPredicateArgument;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FuelConfig {

    // or -1 if nothing found
    public static void bakePredicatesIfNeeded() {
        if (MMConstants.CHUNK_LOADER_FUEL_PREDICATES_BAKED == null
                || MMConstants.CHUNK_LOADER_FUEL_PREDICATES_BAKED.size() < MMConstants.CONFIG_CHUNK_LOADER_FUEL_IDS.get().size()) {

            MMConstants.CHUNK_LOADER_FUEL_PREDICATES_BAKED = new ArrayList<Predicate<ItemStack>>();
            for (String s : MMConstants.CONFIG_CHUNK_LOADER_FUEL_IDS.get()) {

                try {

                    Predicate<ItemStack> bakedItemPredicate =
                            new ItemPredicateArgument().parse(new StringReader(s)).create(null);

                    MMConstants.CHUNK_LOADER_FUEL_PREDICATES_BAKED.add(bakedItemPredicate);

                } catch(CommandSyntaxException e) {
                    // should never hit here!
                }

            }
        }
    }

    public static boolean ValidateID(String id) {
        // ensure itemID is valid
        try {
            new ItemPredicateArgument().parse(new StringReader(id));
        } catch(CommandSyntaxException e) {
            return false;
        }

        return true;
    }

    public static boolean ValidateTicks(int ticks) {
        return ticks >= 0;
    }

    public static String[] DEFAULT_FUEL_IDS = new String[] {
        "minecraft:quartz",
        "minecraft:emerald",
        "minecraft:emerald_block",
        "minecraft:diamond",
        "minecraft:diamond_block",
        "minecraft:nether_star",
        "moreminecarts:chunkrodite",
        "moreminecarts:chunkrodite_block"
    };

    public static Integer[] DEFAULT_FUEL_TICKS = new Integer[] {
        600,
        6000,
        54000,
        72000,
        648000,
        720000,
        18000,
        162000
    };
}
