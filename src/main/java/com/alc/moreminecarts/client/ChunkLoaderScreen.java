package com.alc.moreminecarts.client;

import com.alc.moreminecarts.MoreMinecartsMod;
import com.alc.moreminecarts.containers.ChunkLoaderContainer;
import com.alc.moreminecarts.proxy.MoreMinecartsPacketHandler;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.Level;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ChunkLoaderScreen extends AbstractContainerScreen<ChunkLoaderContainer> {
    private static final ResourceLocation display = new ResourceLocation("moreminecarts:textures/gui/chunk_loader_gui.png");
    private final List<AbstractButton> buttons = Lists.newArrayList();

    public ChunkLoaderScreen(ChunkLoaderContainer container, Inventory inv, Component titleIn) {
        super(container, inv, Component.translatable("Chunk Loader"));
    }

    private void addButton(AbstractButton p_169617_) {
        this.addRenderableWidget(p_169617_);
        this.buttons.add(p_169617_);
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new ChunkLoaderButton(leftPos + 131, topPos + 14));
    }

    @Override
    public void render(GuiGraphics p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);

        for (AbstractButton button : buttons) {
            button.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        }

        this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
    }

    @Override
    protected void renderBg(GuiGraphics matrix, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, display);

        matrix.blit(display, leftPos, topPos, 0, 0, 176, 166);

        double log_progress = menu.getLogProgress();
        int progess = (int)Math.ceil(120 * log_progress);

        matrix.blit(display, leftPos + 28, topPos + 36, 0, 166, progess, 16);

        int minutes_left = menu.getTimeLeft();
        matrix.drawString(font, minutes_left + " minutes left", leftPos + 29, topPos + 55, 4210752, false);

    }


    @OnlyIn(Dist.CLIENT)
    class ChunkLoaderButton extends MMButton {

        boolean oldValue;

        protected ChunkLoaderButton(int x, int y) {
            super(x, y);
            UpdateTooltip();
        }

        @Override
        public void renderWidget(GuiGraphics matrix, int x, int y, float p_230431_4_) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, display);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            if (menu.isEnabled()) {
                if (isHovered) {
                    matrix.blit(display, xPos,yPos, 194, 18, 18, 18);
                }
                else {
                    matrix.blit(display, xPos,yPos, 176, 18, 18, 18);
                }
            }
            else {
                if (isHovered) {
                    matrix.blit(display, xPos,yPos, 176, 0, 18, 18);
                }
                else {
                    // Render nothing. This is already on the backdrop.
                }
            }

            var newValue = menu.isEnabled();
            if (newValue != oldValue) {
                oldValue = newValue;
                UpdateTooltip();
            }
        }

        @Override
        public void onPress() {
            MoreMinecartsPacketHandler.INSTANCE.send(new MoreMinecartsPacketHandler.ChunkLoaderPacket(!menu.isEnabled()), ChunkLoaderScreen.this.minecraft.getConnection().getConnection());
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

        }

        public void UpdateTooltip() {
            this.setTooltip(Tooltip.create(Component.translatable(menu.isEnabled()
                    ? "On"
                    : "Off"
            )));
        }
    }
}
