package com.alc.moreminecarts.renderers;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

public class WoodenPushcartRenderer extends MinecartRenderer {

    public WoodenPushcartRenderer(EntityRendererProvider.Context p_174300_) {
        super(p_174300_, ModelLayers.CHEST_MINECART);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractMinecart entity) {
        return new ResourceLocation("moreminecarts:textures/entity/wooden_pushcart.png");
    }
}
