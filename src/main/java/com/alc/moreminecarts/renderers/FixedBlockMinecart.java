package com.alc.moreminecarts.renderers;

import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class FixedBlockMinecart extends MinecartRenderer {

    public FixedBlockMinecart(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected void renderMinecartContents(AbstractMinecartEntity entityIn, float partialTicks, BlockState stateIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();

        matrixStackIn.translate(0, -0.15, 0);

        float f3 = MathHelper.lerp(partialTicks, entityIn.xRotO, entityIn.xRot);
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(f3));

        if (entityIn instanceof PistonPushcartEntity) {
            PistonPushcartEntity pushcart = (PistonPushcartEntity) entityIn;

            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(stateIn, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);

            boolean is_first = true;
            float length = pushcart.height * 1.25f;
            matrixStackIn.translate(0, length, 0);
            while( length > 0 ) {

                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(stateIn, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);

                matrixStackIn.translate(0, -1, 0);
                length -= 1;
                is_first = false;
            }
        }

        matrixStackIn.popPose();
    }

}
