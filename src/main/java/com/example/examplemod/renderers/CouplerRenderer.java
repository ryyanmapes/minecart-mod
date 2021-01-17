package com.example.examplemod.renderers;

import com.example.examplemod.entities.CouplerEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.LeashKnotModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class CouplerRenderer extends EntityRenderer<CouplerEntity> {

    private static final ResourceLocation LEASH_KNOT_TEXTURES = new ResourceLocation("minecraft:textures/entity/lead_knot.png");
    private final LeashKnotModel<CouplerEntity> leashKnotModel = new LeashKnotModel<>();

    public CouplerRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    public void render(CouplerEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        //matrixStackIn.push();
        //matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        //this.leashKnotModel.setRotationAngles(entityIn, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        //IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.leashKnotModel.getRenderType(LEASH_KNOT_TEXTURES));
        //this.leashKnotModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        //matrixStackIn.pop();

        Entity vehicle1 = entityIn.getFirstVehicle();
        Entity vehicle2 = entityIn.getSecondVehicle();

        if (vehicle1 != null && vehicle2 != null)
            this.renderCoupler(entityIn.world, partialTicks, matrixStackIn, bufferIn, vehicle1, vehicle2);


        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(CouplerEntity entity) {
        return LEASH_KNOT_TEXTURES;
    }


    // Modified from MobRenderer
    // EntityLivingIn: from
    // leashHolder: to
    private <E extends Entity> void renderCoupler(World world, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, Entity vehicle1, Entity vehicle2) {
        matrixStackIn.push();
        Vector3d from_pos = vehicle1.getPositionVec().subtract(0, vehicle1.getBoundingBox().getYSize()/2, 0);
        Vector3d to_pos = vehicle2.getPositionVec().subtract(0, vehicle2.getBoundingBox().getYSize()/2, 0);

        double d0 = (double)(vehicle1.getYaw(partialTicks) * ((float)Math.PI / 180F) + (Math.PI / 2D));
        Vector3d v1_lead_pos = vehicle1.func_241205_ce_();
        double d1 = Math.cos(d0) * v1_lead_pos.z + Math.sin(d0) * v1_lead_pos.x;
        double d2 = Math.sin(d0) * v1_lead_pos.z - Math.cos(d0) * v1_lead_pos.x;
        double d3 = MathHelper.lerp((double)partialTicks, vehicle1.prevPosX, vehicle1.getPosX()) + d1;
        double d4 = MathHelper.lerp((double)partialTicks, vehicle1.prevPosY, vehicle1.getPosY()) + v1_lead_pos.y;
        double d5 = MathHelper.lerp((double)partialTicks, vehicle1.prevPosZ, vehicle1.getPosZ()) + d2;
        //matrixStackIn.translate(d1, v1_lead_pos.y, d2);
        float dx = (float)(to_pos.x - from_pos.x);
        float dy = (float)(to_pos.y - from_pos.y);
        float dz = (float)(to_pos.z - from_pos.z);

        matrixStackIn.translate(-dx/2, -dy/2, -dz/2);

        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getLeash());
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        float dist = MathHelper.fastInvSqrt(dx * dx + dz * dz) * 0.025F / 2.0F;
        float norm_z = dz * dist;
        float norm_x = dx * dist;
        BlockPos blockpos1 = new BlockPos(from_pos);
        BlockPos blockpos2 = new BlockPos(to_pos);
        int i = getBlockLightFake(vehicle1, blockpos1);
        int j = getBlockLightFake(vehicle2, blockpos2);
        int k = world.getLightFor(LightType.SKY, blockpos1);
        int l = world.getLightFor(LightType.SKY, blockpos2);
        renderSide(ivertexbuilder, matrix4f, dx, dy, dz, i, j, k, l, 0.1F, 0.1F, norm_z, norm_x);
        //renderSide(ivertexbuilder, matrix4f, dx, dy, dz, i, j, k, l, 0.1F, 0.0F, norm_z, norm_x);
        matrixStackIn.pop();
    }

    protected int getBlockLightFake(Entity entityIn, BlockPos partialTicks) {
        return entityIn.isBurning() ? 15 : entityIn.world.getLightFor(LightType.BLOCK, partialTicks);
    }

    public static void renderSide(IVertexBuilder builderIn, Matrix4f matrixIn, float p_229119_2_, float p_229119_3_, float p_229119_4_, int blockLight, int holderBlockLight, int skyLight, int holderSkyLight, float p_229119_9_, float p_229119_10_, float norm_z, float norm_x) {
        int i = 4;

        for(int j = 0; j < 4; ++j) {
            float f = (float)j / 3.0F;
            int k = (int)MathHelper.lerp(f, (float)blockLight, (float)holderBlockLight);
            int l = (int)MathHelper.lerp(f, (float)skyLight, (float)holderSkyLight);
            int i1 = LightTexture.packLight(k, l);
            addVertexPair(builderIn, matrixIn, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 4, j, false, norm_z, norm_x);
            addVertexPair(builderIn, matrixIn, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 4, j + 1, true, norm_z, norm_x);
            //addVertexPairOpposite(builderIn, matrixIn, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 12, j, false, norm_z, norm_x);
            //addVertexPairOpposite(builderIn, matrixIn, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 12, j + 1, true, norm_z, norm_x);

        }

    }


    public static void addVertexPair(IVertexBuilder builderIn, Matrix4f matrixIn, int packedLight, float dx, float dy, float dz, float thickness, float y_width, int total, int index, boolean is_first, float norm_z, float norm_x) {
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
            builderIn.pos(matrixIn, center_x + norm_z, center_y + thickness - y_width, center_z - norm_x).color(R, G, B, 1.0F).lightmap(packedLight).endVertex();
        }

        builderIn.pos(matrixIn, center_x - norm_z, center_y + y_width, center_z + norm_x).color(R, G, B, 1.0F).lightmap(packedLight).endVertex();
        if (is_first) {
            builderIn.pos(matrixIn, center_x + norm_z, center_y + thickness- y_width, center_z - norm_x).color(R, G, B, 1.0F).lightmap(packedLight).endVertex();
        }

    }

    public static void addVertexPairOpposite(IVertexBuilder builderIn, Matrix4f matrixIn, int packedLight, float dx, float dy, float dz, float thickness, float y_width, int total, int index, boolean is_first, float norm_z, float norm_x) {
        float stress = Math.abs(dx*dx + dy*dy + dz * dz);
        stress = Math.max(0, stress - 3f);
        float R = 0.1F + (stress/40);
        float G = 0.15F;
        float B = 0.2F;

        float fraction_done = (float)index / (float)total;
        float center_x = dx * fraction_done;
        float center_y = dy * fraction_done; //dy > 0.0F ? dy * fraction_done * fraction_done : dy - dy * (1.0F - fraction_done) * (1.0F - fraction_done);
        float center_z = dz * fraction_done;
        float magn = MathHelper.fastInvSqrt(dx * dx + dy*dy);
        float thickness_z = norm_z * thickness*100;
        float thickness_x = norm_x * thickness*100;
        if (!is_first) {
            builderIn.pos(matrixIn, center_x + thickness_x/2 + norm_z, center_y + thickness/2  - y_width, center_z + thickness_z/2 - norm_x).color(R, G, B, 1.0F).lightmap(packedLight).endVertex();
        }

        builderIn.pos(matrixIn, center_x - thickness_x/2 - norm_z, center_y  + thickness/2+ y_width, center_z - thickness_z/2 + norm_x).color(R, G, B, 1.0F).lightmap(packedLight).endVertex();
        if (is_first) {
            builderIn.pos(matrixIn, center_x + thickness_x/2 + norm_z, center_y + thickness/2 - y_width, center_z + thickness_z/2 - norm_x).color(R, G, B, 1.0F).lightmap(packedLight).endVertex();
        }

    }







}
