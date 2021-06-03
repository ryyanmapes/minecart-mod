package com.alc.moreminecarts.client;

import com.alc.moreminecarts.containers.FlagCartContainer;
import com.alc.moreminecarts.proxy.MoreMinecartsPacketHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlagCartScreen extends ContainerScreen<FlagCartContainer>{
    private static final ResourceLocation display = new ResourceLocation("moreminecarts:textures/gui/loader_gui.png");

    public FlagCartScreen(FlagCartContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, new StringTextComponent("Flag Minecart"));
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new LeftButton(leftPos + 46, topPos + 19));
        this.addButton(new MinusButton(leftPos + 68, topPos + 19));
        this.addButton(new PlusButton(leftPos + 90, topPos + 19));
        this.addButton(new RightButton(leftPos + 112, topPos + 19));
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

        for (int i = 0; i < menu.getDiscludedSlots(); i++) {
            this.blit(matrix, 151 - (18*i), 41, 176, 36, 18, 18);
        }

        int s = menu.getSelectedSlot();
        this.blit(matrix, 4 + (18*s), 38, 196, 36, 24, 24);
    }

    @OnlyIn(Dist.CLIENT)
    abstract class SimpleButton extends AbstractButton {

        protected SimpleButton(int x, int y) { super(x, y, 18, 18, StringTextComponent.EMPTY); }

        public void renderButton(MatrixStack matrix, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
            minecraft.getTextureManager().bind(display);

            boolean mouse_on = isDragging() && this.isHovered;

            if (mouse_on) {
                renderSelected(matrix);
            }
        }

        public abstract void renderSelected(MatrixStack matrix);
    }

    class LeftButton extends SimpleButton {
        protected LeftButton(int x, int y) { super(x, y); }
        @Override
        public void renderSelected(MatrixStack matrix) {
            this.blit(matrix, x,y, 194, 18, 18, 18);
        }
        @Override
        public void onPress() {
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(new MoreMinecartsPacketHandler.FlagCartPacket(true, true));
        }
    }

    class RightButton extends SimpleButton {
        protected RightButton(int x, int y) { super(x, y); }
        @Override
        public void renderSelected(MatrixStack matrix) {
            this.blit(matrix, x,y, 230, 18, 18, 18);
        }
        @Override
        public void onPress() {
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(new MoreMinecartsPacketHandler.FlagCartPacket(false, true));
        }
    }

    class MinusButton extends SimpleButton {
        protected MinusButton(int x, int y) { super(x, y); }
        @Override
        public void renderSelected(MatrixStack matrix) {
            this.blit(matrix, x,y, 230, 0, 18, 18);
        }
        @Override
        public void onPress() {
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(new MoreMinecartsPacketHandler.FlagCartPacket(true, false));
        }
    }

    class PlusButton extends SimpleButton {
        protected PlusButton(int x, int y) { super(x, y); }
        @Override
        public void renderSelected(MatrixStack matrix) {
            this.blit(matrix, x,y, 194, 0, 18, 18);
        }
        @Override
        public void onPress() {
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(new MoreMinecartsPacketHandler.FlagCartPacket(false, false));
        }
    }

}
