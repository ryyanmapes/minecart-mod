package com.alc.moreminecarts;

import net.minecraftforge.common.ForgeConfigSpec;

public class MMConstants {
    public static final String modid = "moreminecarts";

    public static float WOODEN_MAX_SPEED = 0.2f;
    public static float MAGLEV_MAX_SPEED = 1f;
    public static float LIGHTSPEED_MAX_SPEED = 2.5f;

    public static double POWERED_LIGHTSPEED_BOOST = 0.2; // default powered rail: 0.06f


    public static final double POWERED_LOCKING_RAIL_SPEED = 0.05;

    public static final float HS_SLOWDOWN = 0.995f;
    public static final float HS_AIR_DRAG = 0.995f;

    public static final float PISTON_PUSHCART_VERTICAL_SPEED = 0.2f;
    public static final float PISTON_PUSHCART_AERODYNAMIC_VERTICAL_SPEED = 0.4f;
    public static final float PISTON_PUSHCART_MAX_HEIGHT = 5f;

    public static ForgeConfigSpec.DoubleValue CONFIG_CHUNK_LOADER_MULTIPLIER;
    public static ForgeConfigSpec.IntValue CONFIG_GLASS_CACTUS_SPAWNS;
    public static ForgeConfigSpec.BooleanValue CONFIG_GLASS_CACTUS_DESERT_ONLY;
    public static ForgeConfigSpec.DoubleValue CONFIG_WOOD_RAILS_MAX_SPEED;
    public static ForgeConfigSpec.DoubleValue CONFIG_MAGLEV_RAILS_MAX_SPEED;
    public static ForgeConfigSpec.DoubleValue CONFIG_LIGHTSPEED_RAILS_MAX_SPEED;
    public static ForgeConfigSpec.DoubleValue CONFIG_TURBO_BOOST;

}
