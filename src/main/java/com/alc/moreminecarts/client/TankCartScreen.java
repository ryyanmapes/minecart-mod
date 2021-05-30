package com.alc.moreminecarts.client;

import com.alc.moreminecarts.containers.TankCartContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

@OnlyIn(Dist.CLIENT)
public class TankCartScreen extends ContainerScreen<TankCartContainer>{
    private static final ResourceLocation display = new ResourceLocation("moreminecarts:textures/gui/loader_gui.png");

    public TankCartScreen(TankCartContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, new StringTextComponent("Minecart with Tank"));
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

        FluidStack fluid_stack = menu.getFluids();
        if (fluid_stack == null || fluid_stack.isEmpty()) {
            this.font.draw(matrix, "0/20,000 mB fluid", leftPos + 7, topPos + 30, 4210752);
        }
        else {
            this.font.draw(matrix, fluid_stack.getAmount() + "/20,000 mB " + fluid_stack.getDisplayName(), leftPos + 7, topPos + 30, 4210752);
        }

    }

}
