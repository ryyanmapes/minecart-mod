package com.alc.moreminecarts.client;

import com.alc.moreminecarts.containers.MinecartUnLoaderContainer;
import com.alc.moreminecarts.proxy.MoreMinecartsPacketHandler;
import com.alc.moreminecarts.tile_entities.MinecartLoaderTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Iterator;

@OnlyIn(Dist.CLIENT)
public class MinecartLoaderScreen extends ContainerScreen<MinecartUnLoaderContainer>{
    private static final ResourceLocation display = new ResourceLocation("moreminecarts:textures/gui/loader_gui.png");

    public MinecartLoaderScreen(MinecartUnLoaderContainer container, PlayerInventory inv, ITextComponent titleIn) {
        super(container, inv, new StringTextComponent(container.getIsUnloader()? "Minecart Unloader" : "Minecart Loader"));
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new OnlyLockedButton(leftPos + 57, topPos + 19));
        this.addButton(new ComparatorOutputButton(leftPos + 79, topPos + 19));
        this.addButton(new LeaveOneInStackButton(leftPos + 101, topPos + 19));
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
    }

    // Taken from BeaconScreen, for tooltip rendering.
    protected void renderLabels(MatrixStack matrix, int p_230451_2_, int p_230451_3_) {
        super.renderLabels(matrix, p_230451_2_, p_230451_3_);

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
    class ComparatorOutputButton extends AbstractButton {

        protected ComparatorOutputButton(int x, int y) {
            super(x, y, 18, 18, StringTextComponent.EMPTY);
        }

        public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_) {
            String text;
            switch (menu.getComparatorOutputType()) {
                case done_loading:
                    text = "Activate comparator during loading inactivity";
                    break;
                case cart_full:
                    if (menu.getIsUnloader()) text = "Activate comparator when cart is empty";
                    else text = "Activate comparator when cart is full";
                    break;
                case cart_fullness:
                    text = "Comparator outputs cart contents";
                    break;
                default:
                    text = "ERROR";
            }

            MinecartLoaderScreen.this.renderTooltip(p_230443_1_, new StringTextComponent(text) , p_230443_2_, p_230443_3_);
        }

        public void renderButton(MatrixStack matrix, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
            minecraft.getTextureManager().bind(display);

            boolean mouse_on = isDragging() && this.isHovered;

            switch (menu.getComparatorOutputType()) {
                case done_loading:
                    if (mouse_on) {
                        this.blit(matrix, x,y, 176+18, 18, 18, 18);
                    }
                    else {
                        this.blit(matrix, x, y, 176, 18, 18, 18);
                    }
                    break;
                case cart_full:
                    if (mouse_on) {
                        if (menu.getIsUnloader()) this.blit(matrix, x,y, 176+18+36, 0, 18, 18);
                        else this.blit(matrix, x,y, 176+18, 0, 18, 18);
                    }
                    else {
                        if (menu.getIsUnloader()) this.blit(matrix, x,y, 176+36, 0, 18, 18);
                        //this.blit(matrix, x, y, 176, 0, 18, 18);
                    }
                    break;
                case cart_fullness:
                    if (mouse_on) {
                        this.blit(matrix, x,y, 176+18, 36, 18, 18);
                    }
                    else {
                        this.blit(matrix, x, y, 176, 36, 18, 18);
                    }
                    break;
                default:
            }
        }

        @Override
        public void onPress() {
            MoreMinecartsPacketHandler.MinecartLoaderPacket packet = menu.getCurrentPacket();
            packet.output_type = MinecartLoaderTile.ComparatorOutputType.next(packet.output_type);
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(packet);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class OnlyLockedButton extends AbstractButton {

        protected OnlyLockedButton(int x, int y) {
            super(x, y, 18, 18, StringTextComponent.EMPTY);
        }

        public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_) {
            MinecartLoaderScreen.this.renderTooltip(p_230443_1_,
                    new StringTextComponent(menu.getLockedMinecartsOnly()
                            ? "Consider only locked minecarts"
                            : "Consider all minecarts"
                    ) , p_230443_2_, p_230443_3_);
        }

        public void renderButton(MatrixStack matrix, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
            minecraft.getTextureManager().bind(display);

            boolean mouse_on = isDragging() && this.isHovered;

            if (menu.getLockedMinecartsOnly()) {
                if (mouse_on) {
                    if (menu.getIsUnloader()) this.blit(matrix, x,y, 176+18+36, 108, 18, 18);
                    else this.blit(matrix, x,y, 176+18, 108, 18, 18);
                }
                else {
                    if (menu.getIsUnloader()) this.blit(matrix, x,y, 176+36, 108, 18, 18);
                    else this.blit(matrix, x, y, 176, 108, 18, 18);
                }
            }
            else {
                if (mouse_on) {
                    this.blit(matrix, x,y, 176+18, 108-18, 18, 18);
                }
                else {
                    // Render nothing. This is already on the backdrop.
                }
            }
        }

        @Override
        public void onPress() {
            MoreMinecartsPacketHandler.MinecartLoaderPacket packet = menu.getCurrentPacket();
            packet.locked_minecarts_only = !packet.locked_minecarts_only;
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(packet);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class LeaveOneInStackButton extends AbstractButton {

        protected LeaveOneInStackButton(int x, int y) {
            super(x, y, 18, 18, StringTextComponent.EMPTY);
        }

        public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_) {
            MinecartLoaderScreen.this.renderTooltip(p_230443_1_,
                    new StringTextComponent(
                            menu.getIsUnloader()
                                ? (menu.getLeaveOneInStack()
                                    ? "Leave one item in minecart slots"
                                    : "Empty minecart slots entirely")
                                : (menu.getLeaveOneInStack()
                                    ? "Leave one item in loader slots"
                                    : "Empty loader slots entirely")
                    ) , p_230443_2_, p_230443_3_);
        }

        public void renderButton(MatrixStack matrix, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
            minecraft.getTextureManager().bind(display);

            boolean mouse_on = isDragging() && this.isHovered;

            if (menu.getLeaveOneInStack()) {
                if (mouse_on) {
                    if (menu.getIsUnloader()) this.blit(matrix, x,y, 176+18+36, 72, 18, 18);
                    else this.blit(matrix, x,y, 176+18, 72, 18, 18);
                }
                else {
                    if (menu.getIsUnloader()) this.blit(matrix, x, y, 176+36, 72, 18, 18);
                    else this.blit(matrix, x, y, 176, 72, 18, 18);
                }
            }
            else {
                if (mouse_on) {
                    this.blit(matrix, x,y, 176+18, 72-18, 18, 18);
                }
                else {
                    // Render nothing. This is already on the backdrop.
                }
            }
        }

        @Override
        public void onPress() {
            MoreMinecartsPacketHandler.MinecartLoaderPacket packet = menu.getCurrentPacket();
            packet.leave_one_item_in_stack = !packet.leave_one_item_in_stack;
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(packet);
        }
    }
}
