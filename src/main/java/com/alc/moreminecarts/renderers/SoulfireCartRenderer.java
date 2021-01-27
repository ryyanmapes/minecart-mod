package com.alc.moreminecarts.renderers;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.ResourceLocation;

public class SoulfireCartRenderer extends FixedBlockMinecart {
    public SoulfireCartRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractMinecartEntity entity) {
        return new ResourceLocation("moreminecarts:textures/entity/soul_campfire_cart.png");
    }
}
