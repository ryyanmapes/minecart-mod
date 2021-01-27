package com.alc.moreminecarts.renderers;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.ResourceLocation;

public class VanillaMinecartRenderer extends MinecartRenderer {
    public VanillaMinecartRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractMinecartEntity entity) {
        return new ResourceLocation("minecraft:textures/entity/minecart.png");
    }
}
