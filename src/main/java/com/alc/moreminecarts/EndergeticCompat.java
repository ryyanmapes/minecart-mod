package com.alc.moreminecarts;

import net.minecraft.block.Block;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ObjectHolder;

public class EndergeticCompat {

    @ObjectHolder("endergetic:ender_campfire")
    public static final Block ender_campfire = null;

    public static boolean endergeticInstalled() {
        ModList mod_list = ModList.get();
        if (mod_list == null) return false;
        return mod_list.getModContainerById("endergetic").isPresent();
    }

}
