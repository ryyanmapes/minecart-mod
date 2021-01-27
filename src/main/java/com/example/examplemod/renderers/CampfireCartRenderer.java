package com.example.examplemod.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.ResourceLocation;

public class CampfireCartRenderer extends FixedBlockMinecart {
    public CampfireCartRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractMinecartEntity entity) {
        return new ResourceLocation("examplemod:textures/entity/campfire_cart.png");
    }

}
