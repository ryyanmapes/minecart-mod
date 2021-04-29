package com.alc.moreminecarts.renderers;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.ResourceLocation;

public class IronPushcartRenderer extends MinecartRenderer {
    public IronPushcartRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractMinecartEntity entity) {
        return new ResourceLocation("moreminecarts:textures/entity/iron_pushcart.png");
    }
}
