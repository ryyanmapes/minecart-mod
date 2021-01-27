package com.example.examplemod.renderers;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.ResourceLocation;

public class SoulfireCartRenderer extends FixedBlockMinecart {
    public SoulfireCartRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractMinecartEntity entity) {
        return new ResourceLocation("examplemod:textures/entity/soul_campfire_cart.png");
    }
}
