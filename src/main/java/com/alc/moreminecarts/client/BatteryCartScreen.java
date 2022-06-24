package com.alc.moreminecarts.client;

import com.alc.moreminecarts.containers.BatteryCartContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BatteryCartScreen extends AbstractContainerScreen<BatteryCartContainer> {
    private static final ResourceLocation display = new ResourceLocation("moreminecarts:textures/gui/blank.png");

    public BatteryCartScreen(BatteryCartContainer container, Inventory inv, Component titleIn) {
        super(container, inv, Component.translatable("Minecart with Battery"));
    }

    @Override
    public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
    }

    @Override
    protected void renderBg(PoseStack matrix, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        RenderSystem.setShaderTexture(0, display);
        this.blit(matrix, leftPos, topPos, 0, 0, 176, 166);

        int energy = menu.getEnergy();
        this.font.draw(matrix, energy+"/40,000 FE", leftPos + 8, topPos + 20, 4210752);
    }

}
