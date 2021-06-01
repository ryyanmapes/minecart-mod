package com.alc.moreminecarts.client;

import com.alc.moreminecarts.containers.BatteryCartContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BatteryCartScreen extends ContainerScreen<BatteryCartContainer>{
    private static final ResourceLocation display = new ResourceLocation("moreminecarts:textures/gui/blank.png");

    public BatteryCartScreen(BatteryCartContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, new StringTextComponent("Minecart with Battery"));
    }

    @Override
    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
    }

    @Override
    protected void renderBg(MatrixStack matrix, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        this.minecraft.getTextureManager().bind(display);
        this.blit(matrix, leftPos, topPos, 0, 0, 176, 166);

        int energy = menu.getEnergy();
        this.font.draw(matrix, energy+"/40,000 RF", leftPos + 8, topPos + 20, 4210752);
    }

}
