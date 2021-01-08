package com.example.examplemod.renderers;

import com.example.examplemod.entities.CouplerEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LeashKnotRenderer;
import net.minecraft.client.renderer.entity.model.LeashKnotModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.util.ResourceLocation;

public class CouplerRenderer extends EntityRenderer<CouplerEntity> {

    private static final ResourceLocation LEASH_KNOT_TEXTURES = new ResourceLocation("minecraft:textures/entity/lead_knot.png");
    private final LeashKnotModel<CouplerEntity> leashKnotModel = new LeashKnotModel<>();

    public CouplerRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    public void render(CouplerEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        this.leashKnotModel.setRotationAngles(entityIn, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.leashKnotModel.getRenderType(LEASH_KNOT_TEXTURES));
        this.leashKnotModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(CouplerEntity entity) {
        return LEASH_KNOT_TEXTURES;
    }
}
