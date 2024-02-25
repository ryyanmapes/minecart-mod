package com.alc.moreminecarts.client;

import com.alc.moreminecarts.containers.TankCartContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

@OnlyIn(Dist.CLIENT)
public class TankCartScreen extends AbstractContainerScreen<TankCartContainer> {
    private static final ResourceLocation display = new ResourceLocation("moreminecarts:textures/gui/blank.png");

    public TankCartScreen(TankCartContainer container, Inventory inv, Component titleIn) {
        super(container, inv, Component.translatable("Minecart with Tank"));
    }

    @Override
    public void render(GuiGraphics p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
    }

    @Override
    protected void renderBg(GuiGraphics matrix, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        RenderSystem.setShaderTexture(0, display);
        matrix.blit(display, leftPos, topPos, 0, 0, 176, 166);

        FluidStack fluid_stack = menu.getFluids();
        if (fluid_stack == null || fluid_stack.isEmpty()) {
            matrix.drawString(font, "0/40,000 mB fluid", leftPos + 8, topPos + 20, 4210752, false);
        }
        else {
            matrix.drawString(font, fluid_stack.getAmount() + "/40,000 mB " + fluid_stack.getDisplayName().getString(), leftPos + 8, topPos + 20, 4210752, false);
        }

    }

}
