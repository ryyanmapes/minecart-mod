package com.alc.moreminecarts.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;

public class FixedBlockMinecart extends MinecartRenderer {

    public FixedBlockMinecart(EntityRendererProvider.Context p_174300_) {
        super(p_174300_, ModelLayers.CHEST_MINECART);
    }

    @Override
    protected void renderMinecartContents(AbstractMinecart entityIn, float partialTicks, BlockState stateIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, -0.15, 0);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(stateIn, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
        matrixStackIn.popPose();
    }

}
