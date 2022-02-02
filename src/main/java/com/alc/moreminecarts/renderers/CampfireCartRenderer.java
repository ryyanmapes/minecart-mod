package com.alc.moreminecarts.renderers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

public class CampfireCartRenderer extends FixedBlockMinecart {
    public CampfireCartRenderer(EntityRendererProvider.Context p_174300_) {
        super(p_174300_);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractMinecart entity) {
        return new ResourceLocation("moreminecarts:textures/entity/campfire_cart.png");
    }

}
