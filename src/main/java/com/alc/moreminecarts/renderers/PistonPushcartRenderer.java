package com.alc.moreminecarts.renderers;

import com.alc.moreminecarts.blocks.PistonDisplayBlock;
import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.alc.moreminecarts.registry.MMBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class PistonPushcartRenderer extends EntityRenderer<PistonPushcartEntity> {

    protected EntityModel<PistonPushcartEntity> model;

    public PistonPushcartRenderer(EntityRendererProvider.Context p_174300_) {
        super(p_174300_);
        model = new MinecartModel<>(p_174300_.bakeLayer(ModelLayers.CHEST_MINECART));
    }

    @Override
    public ResourceLocation getTextureLocation(PistonPushcartEntity entity) {
        return new ResourceLocation("moreminecarts:textures/entity/iron_pushcart.png");
    }

    // Copied from AbstractMinecartRenderer
    public void render(PistonPushcartEntity p_225623_1_, float p_225623_2_, float p_225623_3_, PoseStack p_225623_4_, MultiBufferSource p_225623_5_, int p_225623_6_) {
        super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
        p_225623_4_.pushPose();
        long i = (long)p_225623_1_.getId() * 493286711L;
        i = i * i * 4392167121L + i * 98761L;
        float f = (((float)(i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f1 = (((float)(i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f2 = (((float)(i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        p_225623_4_.translate((double)f, (double)f1, (double)f2);
        double d0 = Mth.lerp((double)p_225623_3_, p_225623_1_.xOld, p_225623_1_.getX());
        double d1 = Mth.lerp((double)p_225623_3_, p_225623_1_.yOld, p_225623_1_.getY());
        double d2 = Mth.lerp((double)p_225623_3_, p_225623_1_.zOld, p_225623_1_.getZ());
        double d3 = (double)0.3F;
        Vec3 vector3d = p_225623_1_.getPos(d0, d1, d2);
        float f3 = Mth.lerp(p_225623_3_, p_225623_1_.xRotO, p_225623_1_.getXRot());
        if (vector3d != null) {
            Vec3 vector3d1 = p_225623_1_.getPosOffs(d0, d1, d2, (double)0.3F);
            Vec3 vector3d2 = p_225623_1_.getPosOffs(d0, d1, d2, (double)-0.3F);
            if (vector3d1 == null) {
                vector3d1 = vector3d;
            }

            if (vector3d2 == null) {
                vector3d2 = vector3d;
            }

            p_225623_4_.translate(vector3d.x - d0, (vector3d1.y + vector3d2.y) / 2.0D - d1, vector3d.z - d2);
            Vec3 vector3d3 = vector3d2.add(-vector3d1.x, -vector3d1.y, -vector3d1.z);
            if (vector3d3.length() != 0.0D) {
                vector3d3 = vector3d3.normalize();
                p_225623_2_ = (float)(Math.atan2(vector3d3.z, vector3d3.x) * 180.0D / Math.PI);
                f3 = (float)(Math.atan(vector3d3.y) * 73.0D);
            }
        }

        p_225623_4_.translate(0.0D, 0.375D, 0.0D);
        p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(180.0F - p_225623_2_));
        p_225623_4_.mulPose(Vector3f.ZP.rotationDegrees(-f3));
        float f5 = (float)p_225623_1_.getHurtTime() - p_225623_3_;
        float f6 = p_225623_1_.getDamage() - p_225623_3_;
        if (f6 < 0.0F) {
            f6 = 0.0F;
        }

        if (f5 > 0.0F) {
            p_225623_4_.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(f5) * f5 * f6 / 10.0F * (float)p_225623_1_.getHurtDir()));
        }

        int j = p_225623_1_.getDisplayOffset();
        BlockState blockstate = p_225623_1_.getDisplayBlockState();
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            p_225623_4_.pushPose();
            float f4 = 0.75F;
            p_225623_4_.scale(0.75F, 0.75F, 0.75F);
            p_225623_4_.translate(-0.5D, (double)((float)(j - 8) / 16.0F), 0.5D);

            // REMOVED
            // p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(90.0F));
            // ADDED
            p_225623_4_.translate(0, -0.15, -1);
            p_225623_4_.mulPose(Vector3f.ZP.rotationDegrees(f3));

            this.renderMinecartContents(p_225623_1_, p_225623_3_, blockstate, p_225623_4_, p_225623_5_, p_225623_6_);
            p_225623_4_.popPose();
        }

        p_225623_4_.scale(-1.0F, -1.0F, 1.0F);
        this.model.setupAnim(p_225623_1_, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer ivertexbuilder = p_225623_5_.getBuffer(this.model.renderType(this.getTextureLocation(p_225623_1_)));
        this.model.renderToBuffer(p_225623_4_, ivertexbuilder, p_225623_6_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        p_225623_4_.popPose();
    }

    // 0: piston base
    // 1: piston rod
    // 2: piston head
    // 3: sticky piston head
    protected void renderMinecartContents(PistonPushcartEntity pushcart, float partialTicks, BlockState stateIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                MMBlocks.PISTON_DISPLAY_BLOCK.get().defaultBlockState().setValue(PistonDisplayBlock.VARIANT, 0),
                matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);

        boolean is_first = true;
        float length = Mth.lerp(partialTicks, pushcart.getLastHeight(), pushcart.getHeight()) * (4.0f/3.0f);
        matrixStackIn.translate(0, length, 0);
        while( length >= 0 ) {

            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                    MMBlocks.PISTON_DISPLAY_BLOCK.get().defaultBlockState()
                            .setValue(PistonDisplayBlock.VARIANT, is_first? getPistonHeadVariant() : 1),
                    matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);

            matrixStackIn.translate(0, -1, 0);
            length -= 1;
            is_first = false;
        }


        matrixStackIn.popPose();
    }

    public int getPistonHeadVariant() {
        return 2;
    }

}
