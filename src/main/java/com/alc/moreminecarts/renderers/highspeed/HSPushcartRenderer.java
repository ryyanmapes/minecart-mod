package com.alc.moreminecarts.renderers.highspeed;

import com.alc.moreminecarts.renderers.FixedBlockMinecart;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

public class HSPushcartRenderer extends FixedBlockMinecart {
    public HSPushcartRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractMinecart entity) {
        return new ResourceLocation("moreminecarts:textures/entity/high_speed_pushcart.png");
    }
}
