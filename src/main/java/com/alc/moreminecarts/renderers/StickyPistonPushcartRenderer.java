package com.alc.moreminecarts.renderers;

import net.minecraft.client.renderer.entity.EntityRendererManager;

public class StickyPistonPushcartRenderer extends PistonPushcartRenderer{

    public StickyPistonPushcartRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public int getPistonHeadVariant() {
        return 3;
    }

}
