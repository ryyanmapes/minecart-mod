package com.alc.moreminecarts.renderers.highspeed;

import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.alc.moreminecarts.renderers.StickyPistonPushcartRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class HSStickyPistonPushcartRenderer extends StickyPistonPushcartRenderer {
    public HSStickyPistonPushcartRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getTextureLocation(PistonPushcartEntity entity) {
        return new ResourceLocation("moreminecarts:textures/entity/high_speed_pushcart.png");
    }
}
