package com.alc.moreminecarts.renderers;

import com.alc.moreminecarts.MoreMinecartsMod;
import com.alc.moreminecarts.entities.CouplerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class CouplerRenderer extends EntityRenderer<CouplerEntity> {

    private static final ResourceLocation LEASH_KNOT_TEXTURES = new ResourceLocation("minecraft:textures/entity/lead_knot.png");
    protected LeashKnotModel<CouplerEntity> leashKnotModel;

    public CouplerRenderer(EntityRendererProvider.Context p_174300_) {
        super(p_174300_);
        this.leashKnotModel = new LeashKnotModel(p_174300_.bakeLayer(ModelLayers.LEASH_KNOT));
    }

    public void render(CouplerEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        //matrixStackIn.push();
        //matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        //this.leashKnotModel.setRotationAngles(entityIn, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        //IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.leashKnotModel.getRenderType(LEASH_KNOT_TEXTURES));
        //this.leashKnotModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        //matrixStackIn.pop();

        Entity vehicle1 = entityIn.getFirstVehicle();
        Entity vehicle2 = entityIn.getSecondVehicle();

        if (vehicle1 != null && vehicle2 != null) {

            this.renderCoupler(entityIn.level(), partialTicks, matrixStackIn, bufferIn, vehicle1, vehicle2);
        }


        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(CouplerEntity entity) {
        return LEASH_KNOT_TEXTURES;
    }


    public Vec3 getLerpedPosition(Entity entity, float partialTicks) {
        double d0 = Mth.lerp((double)partialTicks, entity.xOld, entity.getX());
        double d1 = Mth.lerp((double)partialTicks, entity.yOld, entity.getY());
        double d2 = Mth.lerp((double)partialTicks, entity.zOld, entity.getZ());
        return new Vec3(d0, d1, d2);
    }

    // Modified from MobRenderer
    // EntityLivingIn: from
    // leashHolder: to
    private <E extends Entity> void renderCoupler(Level world, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, Entity vehicle1, Entity vehicle2) {
        matrixStackIn.pushPose();

        Vec3 vehicle1_pos = getLerpedPosition(vehicle1, partialTicks);
        Vec3 vehicle2_pos = getLerpedPosition(vehicle2, partialTicks);

        Vec3 from_pos = vehicle1_pos.subtract(0, vehicle1.getBoundingBox().getYsize()/2, 0);
        Vec3 to_pos = vehicle2_pos.subtract(0, vehicle2.getBoundingBox().getYsize()/2, 0);

        double d0 = (double)(vehicle1.getYRot() * ((float)Math.PI / 180F) + (Math.PI / 2D));
        Vec3 v1_lead_pos = new Vec3(0.0D, (double)vehicle1.getEyeHeight(), (double)(vehicle1.getBbWidth() * 0.4F));
        double d1 = Math.cos(d0) * v1_lead_pos.z + Math.sin(d0) * v1_lead_pos.x;
        double d2 = Math.sin(d0) * v1_lead_pos.z - Math.cos(d0) * v1_lead_pos.x;
        double d3 = Mth.lerp((double)partialTicks, vehicle1.xOld, vehicle1.position().x) + d1;
        double d4 = Mth.lerp((double)partialTicks, vehicle1.yOld, vehicle1.position().y) + v1_lead_pos.y;
        double d5 = Mth.lerp((double)partialTicks, vehicle1.zOld, vehicle1.position().z) + d2;
        //matrixStackIn.translate(d1, v1_lead_pos.y, d2);
        float dx = (float)(to_pos.x - from_pos.x);
        float dy = (float)(to_pos.y - from_pos.y);
        float dz = (float)(to_pos.z - from_pos.z);

        matrixStackIn.translate(-dx/2, -dy/2, -dz/2);

        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.leash());
        Matrix4f matrix4f = matrixStackIn.last().pose();
        float dist = (float)Mth.fastInvSqrt(dx * dx + dz * dz) * 0.025F / 2.0F;
        float norm_z = dz * dist;
        float norm_x = dx * dist;
        BlockPos blockpos1 = new BlockPos((int)from_pos.x, (int)from_pos.y, (int)from_pos.z);
        BlockPos blockpos2 = new BlockPos((int)to_pos.x, (int)to_pos.y, (int)to_pos.z);
        int i = getBlockLightFake(vehicle1, blockpos1);
        int j = getBlockLightFake(vehicle2, blockpos2);
        int k = world.getBrightness(LightLayer.SKY, blockpos1);
        int l = world.getBrightness(LightLayer.SKY, blockpos2);
        renderSide(ivertexbuilder, matrix4f, dx, dy, dz, i, j, k, l, 0.1F, 0.1F, norm_z, norm_x);
        //renderSide(ivertexbuilder, matrix4f, dx, dy, dz, i, j, k, l, 0.1F, 0.0F, norm_z, norm_x);
        matrixStackIn.popPose();
    }

    protected int getBlockLightFake(Entity entityIn, BlockPos partialTicks) {
        return entityIn.isOnFire() ? 15 : entityIn.level().getBrightness(LightLayer.BLOCK, partialTicks);
    }

    public static void renderSide(VertexConsumer builderIn, Matrix4f matrixIn, float p_229119_2_, float p_229119_3_, float p_229119_4_, int blockLight, int holderBlockLight, int skyLight, int holderSkyLight, float p_229119_9_, float p_229119_10_, float norm_z, float norm_x) {
        int i = 4;

        for(int j = 0; j < 4; ++j) {
            float f = (float)j / 3.0F;
            int k = (int)Mth.lerp(f, (float)blockLight, (float)holderBlockLight);
            int l = (int)Mth.lerp(f, (float)skyLight, (float)holderSkyLight);
            int i1 = LightTexture.pack(k, l);
            addVertexPair(builderIn, matrixIn, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 4, j, false, norm_z, norm_x);
            addVertexPair(builderIn, matrixIn, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 4, j + 1, true, norm_z, norm_x);
            //addVertexPairOpposite(builderIn, matrixIn, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 12, j, false, norm_z, norm_x);
            //addVertexPairOpposite(builderIn, matrixIn, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 12, j + 1, true, norm_z, norm_x);

        }

    }


    public static void addVertexPair(VertexConsumer builderIn, Matrix4f matrixIn, int packedLight, float dx, float dy, float dz, float thickness, float y_width, int total, int index, boolean is_first, float norm_z, float norm_x) {
        float stress = Math.abs(dx*dx + dy*dy + dz * dz);
        stress = Math.max(0, stress - 3f);
        float R = 0.1F + (stress/40);
        float G = 0.15F;
        float B = 0.2F;

        float fraction_done = (float)index / (float)total;
        float center_x = dx * fraction_done;
        float center_y = dy * fraction_done; //dy > 0.0F ? dy * fraction_done * fraction_done : dy - dy * (1.0F - fraction_done) * (1.0F - fraction_done);
        float center_z = dz * fraction_done;
        if (!is_first) {
            builderIn.vertex(matrixIn, center_x + norm_z, center_y + thickness - y_width, center_z - norm_x).color(R, G, B, 1.0F).uv2(packedLight).endVertex();
        }

        builderIn.vertex(matrixIn, center_x - norm_z, center_y + y_width, center_z + norm_x).color(R, G, B, 1.0F).uv2(packedLight).endVertex();
        if (is_first) {
            builderIn.vertex(matrixIn, center_x + norm_z, center_y + thickness- y_width, center_z - norm_x).color(R, G, B, 1.0F).uv2(packedLight).endVertex();
        }

    }

    public static void addVertexPairOpposite(VertexConsumer builderIn, Matrix4f matrixIn, int packedLight, float dx, float dy, float dz, float thickness, float y_width, int total, int index, boolean is_first, float norm_z, float norm_x) {
        float stress = Math.abs(dx*dx + dy*dy + dz * dz);
        stress = Math.max(0, stress - 3f);
        float R = 0.1F + (stress/40);
        float G = 0.15F;
        float B = 0.2F;

        float fraction_done = (float)index / (float)total;
        float center_x = dx * fraction_done;
        float center_y = dy * fraction_done; //dy > 0.0F ? dy * fraction_done * fraction_done : dy - dy * (1.0F - fraction_done) * (1.0F - fraction_done);
        float center_z = dz * fraction_done;
        float magn = (float)Mth.fastInvSqrt(dx * dx + dy*dy);
        float thickness_z = norm_z * thickness*100;
        float thickness_x = norm_x * thickness*100;
        if (!is_first) {
            builderIn.vertex(matrixIn, center_x + thickness_x/2 + norm_z, center_y + thickness/2  - y_width, center_z + thickness_z/2 - norm_x).color(R, G, B, 1.0F).uv2(packedLight).endVertex();
        }

        builderIn.vertex(matrixIn, center_x - thickness_x/2 - norm_z, center_y  + thickness/2+ y_width, center_z - thickness_z/2 + norm_x).color(R, G, B, 1.0F).uv2(packedLight).endVertex();
        if (is_first) {
            builderIn.vertex(matrixIn, center_x + thickness_x/2 + norm_z, center_y + thickness/2 - y_width, center_z + thickness_z/2 - norm_x).color(R, G, B, 1.0F).uv2(packedLight).endVertex();
        }

    }







}
