package com.alc.moreminecarts.renderers;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.blocks.PistonDisplayBlock;
import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class PistonPushcartRenderer extends MinecartRenderer {

    public PistonPushcartRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractMinecartEntity entity) {
        return new ResourceLocation("moreminecarts:textures/entity/iron_pushcart.png");
    }

    // 0: piston base
    // 1: piston rod
    // 2: piston head
    // 3: sticky piston head
    @Override
    protected void renderMinecartContents(AbstractMinecartEntity entityIn, float partialTicks, BlockState stateIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();

        matrixStackIn.translate(0, -0.15, 0);

        float anti_rotation = MathHelper.lerp(partialTicks, entityIn.yRotO, entityIn.yRot);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(anti_rotation));

        if (entityIn instanceof PistonPushcartEntity) {
            PistonPushcartEntity pushcart = (PistonPushcartEntity) entityIn;

            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                    MMReferences.piston_display_block.defaultBlockState().getBlockState().setValue(PistonDisplayBlock.VARIANT, 0),
                    matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);

            boolean is_first = true;
            float length = MathHelper.lerp(partialTicks, pushcart.last_height, pushcart.height) * 1.25f;
            matrixStackIn.translate(0, length, 0);
            while( length > 0 ) {

                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                        MMReferences.piston_display_block.defaultBlockState().getBlockState().setValue(PistonDisplayBlock.VARIANT, is_first? 2 : 1),
                        matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);

                matrixStackIn.translate(0, -1, 0);
                length -= 1;
                is_first = false;
            }
        }

        matrixStackIn.popPose();
    }

}
