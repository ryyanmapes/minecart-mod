package com.alc.moreminecarts.renderers.highspeed;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

public class HSMinecartRenderer extends MinecartRenderer {
    public HSMinecartRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, ModelLayers.CHEST_MINECART);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractMinecart entity) {
        return new ResourceLocation("moreminecarts:textures/entity/high_speed_minecart.png");
    }
}
