package com.alc.moreminecarts.renderers;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

public class VanillaMinecartRenderer extends MinecartRenderer {

    public VanillaMinecartRenderer(EntityRendererProvider.Context p_174300_) {
        super(p_174300_, ModelLayers.CHEST_MINECART);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractMinecart entity) {
        return new ResourceLocation("minecraft:textures/entity/minecart.png");
    }
}
