package com.alc.moreminecarts.renderers.highspeed;

import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.alc.moreminecarts.renderers.PistonPushcartRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class HSPistonPushcartRenderer extends PistonPushcartRenderer {
    public HSPistonPushcartRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getTextureLocation(PistonPushcartEntity entity) {
        return new ResourceLocation("moreminecarts:textures/entity/high_speed_pushcart.png");
    }
}
