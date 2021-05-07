package com.alc.moreminecarts.guis;

import com.alc.moreminecarts.containers.ChunkLoaderContainer;
import com.alc.moreminecarts.misc.MoreMinecartsPacketHandler;
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
public class ChunkLoaderScreen extends ContainerScreen<ChunkLoaderContainer>{
    private static final ResourceLocation display = new ResourceLocation("moreminecarts:textures/gui/chunk_loader_gui.png");

    public ChunkLoaderScreen(ChunkLoaderContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, new StringTextComponent("Chunk Loader"));
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new ChunkLoaderButton(leftPos + 131, topPos + 14));
    }

    @Override
    protected void renderBg(MatrixStack matrix, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        this.minecraft.getTextureManager().bind(display);
        this.blit(matrix, leftPos, topPos, 0, 0, 176, 166);

        double log_progress = menu.getLogProgress();
        int progess = (int)Math.floor(120 * log_progress);

        this.blit(matrix, leftPos + 28, topPos + 36, 0, 166, progess, 16);

        int actual_progress = menu.getTimeLeft();
        int minutes_left = (int)Math.ceil(actual_progress / 1200.0f);
        this.font.draw(matrix, minutes_left + " minutes left", leftPos + 29, topPos + 55, 4210752);

    }

    @OnlyIn(Dist.CLIENT)
    class ChunkLoaderButton extends AbstractButton {

        protected ChunkLoaderButton(int x, int y) {
            super(x, y, 18, 18, StringTextComponent.EMPTY);
        }

        public void renderButton(MatrixStack matrix, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
            minecraft.getTextureManager().bind(display);

            if (menu.isEnabled()) {
                if (isDragging()) {
                    this.blit(matrix, x,y, 194, 18, 18, 18);
                }
                else {
                    this.blit(matrix, x, y, 176, 18, 18, 18);
                }
            }
            else {
                if (isDragging()) {
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
}
