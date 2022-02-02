package com.alc.moreminecarts.renderers;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class StickyPistonPushcartRenderer extends PistonPushcartRenderer{

    public StickyPistonPushcartRenderer(EntityRendererProvider.Context p_174300_) {
        super(p_174300_);
    }

    @Override
    public int getPistonHeadVariant() {
        return 3;
    }

}
