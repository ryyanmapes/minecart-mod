package com.alc.moreminecarts.client;

import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.containers.ChunkLoaderContainer;
import com.alc.moreminecarts.proxy.MoreMinecartsPacketHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ChunkLoaderScreen extends ContainerScreen<ChunkLoaderContainer>{
    private static final ResourceLocation display = new ResourceLocation("moreminecarts:textures/gui/chunk_loader_gui.png");
    private static final ITextComponent TITLE = new TranslationTextComponent("gui.moreminecarts.chunk_loader.title");
    private static final ITextComponent ON_LABEL = new TranslationTextComponent("gui.moreminecarts.chunk_loader.on");
    private static final ITextComponent OFF_LABEL = new TranslationTextComponent("gui.moreminecarts.chunk_loader.off");
    private static final ITextComponent INFO_LABEL = new TranslationTextComponent("gui.moreminecarts.chunk_loader.info");
    private static final ITextComponent MINUTES_LEFT = new TranslationTextComponent("gui.moreminecarts.chunk_loader.minutes_left");

    public ChunkLoaderScreen(ChunkLoaderContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, TITLE);
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new ChunkLoaderButton(leftPos + 99, topPos + 14));
        this.addButton(new ChunkLoaderInfoButton(leftPos + 2, topPos + 3));
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

        double log_progress = menu.getLogProgress();
        int progess = (int)Math.ceil(120 * log_progress);

        this.blit(matrix, leftPos + 28, topPos + 36, 0, 166, progess, 16);

        int minutes_left = menu.getTimeLeft();
        this.font.draw(matrix, minutes_left + " " + MINUTES_LEFT.getString(), leftPos + 29, topPos + 55, 4210752);

    }

    // Taken from BeaconScreen, for tooltip rendering.
    @Override
    protected void renderLabels(MatrixStack matrix, int p_230451_2_, int p_230451_3_) {
        this.font.draw(matrix, getTitle(), (float)this.titleLabelX + 12, (float)this.titleLabelY, 4210752);
        this.font.draw(matrix, this.inventory.getDisplayName(), (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);

        Iterator var4 = this.buttons.iterator();

        while(var4.hasNext()) {
            Widget lvt_5_1_ = (Widget)var4.next();
            if (lvt_5_1_.isHovered()) {
                lvt_5_1_.renderToolTip(matrix, p_230451_2_ - this.leftPos, p_230451_3_ - this.topPos);
                break;
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    class ChunkLoaderButton extends AbstractButton {

        protected ChunkLoaderButton(int x, int y) {
            super(x, y, 18, 18, StringTextComponent.EMPTY);
        }

        public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_) {
            ChunkLoaderScreen.this.renderTooltip(p_230443_1_, menu.isEnabled()? ON_LABEL: OFF_LABEL, p_230443_2_, p_230443_3_);
        }

        public void renderButton(MatrixStack matrix, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
            minecraft.getTextureManager().bind(display);

            if (menu.isEnabled()) {
                if (isHovered() && isDragging()) {
                    this.blit(matrix, x,y, 194, 18, 18, 18);
                }
                else {
                    this.blit(matrix, x, y, 176, 18, 18, 18);
                }
            }
            else {
                if (isHovered() && isDragging()) {
                    this.blit(matrix, x,y, 176, 0, 18, 18);
                }
                else {
                    // Render nothing. This is already on the backdrop.
                }
            }
        }

        @Override
        public void onPress() {
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(new MoreMinecartsPacketHandler.ChunkLoaderPacket(!menu.isEnabled()));
        }
    }

    @OnlyIn(Dist.CLIENT)
    class ChunkLoaderInfoButton extends AbstractButton {

        protected ChunkLoaderInfoButton(int x, int y) {
            super(x, y, 18, 18, StringTextComponent.EMPTY);
        }

        public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_) {
            List<? extends String> configMessageLines = MMConstants.CONFIG_CHUNK_LOADER_MESSAGE.get();
            if (!configMessageLines.isEmpty()) {
                List<ITextComponent> configMessage = configMessageLines.stream().map(StringTextComponent::new).collect(Collectors.toList());
                ChunkLoaderScreen.this.renderComponentTooltip(p_230443_1_, configMessage, p_230443_2_, p_230443_3_);
            }
            else ChunkLoaderScreen.this.renderTooltip(p_230443_1_, INFO_LABEL, p_230443_2_, p_230443_3_);
        }

        public void renderButton(MatrixStack matrix, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
            minecraft.getTextureManager().bind(display);

            if (isHovered()) {
                this.blit(matrix, x,y, 176, 36, 18, 18);
            }
            else {
                // Render nothing. This is already on the backdrop.
            }
        }

        @Override
        public void onPress() {

        }
    }
}
