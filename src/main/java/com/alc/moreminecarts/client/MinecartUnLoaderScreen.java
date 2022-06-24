package com.alc.moreminecarts.client;

import com.alc.moreminecarts.containers.MinecartUnLoaderContainer;
import com.alc.moreminecarts.proxy.MoreMinecartsPacketHandler;
import com.alc.moreminecarts.tile_entities.MinecartLoaderTile;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MinecartUnLoaderScreen extends AbstractContainerScreen<MinecartUnLoaderContainer> {
    private static final ResourceLocation display = new ResourceLocation("moreminecarts:textures/gui/loader_gui.png");
    private final List<AbstractButton> buttons = Lists.newArrayList();

    public MinecartUnLoaderScreen(MinecartUnLoaderContainer container, Inventory inv, Component titleIn) {
        super(container, inv, Component.translatable(container.getIsUnloader()? "Minecart Unloader" : "Minecart Loader"));
    }

    @Override
    public Component getTitle() {
        return Component.translatable(menu.getIsUnloader()? "Minecart Unloader" : "Minecart Loader");
    }

    private void addButton(AbstractButton p_169617_) {
        this.addWidget(p_169617_);
        this.buttons.add(p_169617_);
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new OutputTypeButton(leftPos + 46, topPos + 19));
        this.addButton(new OnlyLockedButton(leftPos + 68, topPos + 19));
        this.addButton(new ComparatorOutputButton(leftPos + 90, topPos + 19));
        this.addButton(new LeaveOneInStackButton(leftPos + 112, topPos + 19));
    }

    @Override
    public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {


        this.renderBackground(p_230430_1_);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);

        for (AbstractButton button : buttons) {
            button.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        }

        this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
    }

    @Override
    protected void renderBg(PoseStack matrix, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        RenderSystem.setShaderTexture(0, display);
        this.blit(matrix, leftPos, topPos, 0, 0, 176, 166);

        String contents_text = "";
        FluidStack fluid_stack = menu.getFluids();
        if (fluid_stack == null || fluid_stack.isEmpty()) {
            contents_text += "0/2,000 mB fluid, ";
        }
        else {
            contents_text += fluid_stack.getAmount() + "/2,000 mB " + fluid_stack.getDisplayName().getString() + ", ";
        }

        int energy_amount = menu.getEnergy();
        contents_text += energy_amount + "/2,000 RF";

        this.font.draw(matrix, contents_text, leftPos + 7, topPos + 62, 4210752);

    }

    // Taken from BeaconScreen, for tooltip rendering.
    @Override
    protected void renderLabels(PoseStack matrix, int p_230451_2_, int p_230451_3_) {
        this.font.draw(matrix, getTitle(), (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
        this.font.draw(matrix, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);

        Iterator var4 = this.buttons.iterator();

        while(var4.hasNext()) {
            Widget lvt_5_1_ = (Widget)var4.next();
            if (((AbstractButton)lvt_5_1_).isHoveredOrFocused()) {
                ((AbstractButton)lvt_5_1_).renderToolTip(matrix, p_230451_2_ - this.leftPos, p_230451_3_ - this.topPos);
                break;
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    class ComparatorOutputButton extends AbstractButton {

        protected ComparatorOutputButton(int x, int y) {
            super(x, y, 18, 18, Component.empty());
        }

        public void renderToolTip(PoseStack p_230443_1_, int p_230443_2_, int p_230443_3_) {
            String text;
            switch (menu.getComparatorOutputType()) {
                case done_loading:
                    text = "Activate output during loading inactivity";
                    break;
                case cart_full:
                    if (menu.getIsUnloader()) text = "Activate output when cart is empty";
                    else text = "Activate output when cart is full";
                    break;
                case cart_fullness:
                    text = "Output cart contents";
                    break;
                default:
                    text = "ERROR";
            }

            MinecartUnLoaderScreen.this.renderTooltip(p_230443_1_, Component.translatable(text) , p_230443_2_, p_230443_3_);
        }

        public void renderButton(PoseStack matrix, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
            RenderSystem.setShaderTexture(0, display);

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

        @Override
        public void updateNarration(NarrationElementOutput p_169152_) {

        }
    }

    @OnlyIn(Dist.CLIENT)
    class OnlyLockedButton extends AbstractButton {

        protected OnlyLockedButton(int x, int y) {
            super(x, y, 18, 18, Component.empty());
        }

        public void renderToolTip(PoseStack p_230443_1_, int p_230443_2_, int p_230443_3_) {
            MinecartUnLoaderScreen.this.renderTooltip(p_230443_1_,
                    Component.translatable(menu.getLockedMinecartsOnly()
                            ? "Consider only locked minecarts"
                            : "Consider all minecarts"
                    ) , p_230443_2_, p_230443_3_);
        }

        public void renderButton(PoseStack matrix, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
            RenderSystem.setShaderTexture(0, display);

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

        @Override
        public void updateNarration(NarrationElementOutput p_169152_) {

        }
    }

    @OnlyIn(Dist.CLIENT)
    class LeaveOneInStackButton extends AbstractButton {

        protected LeaveOneInStackButton(int x, int y) {
            super(x, y, 18, 18, Component.empty());
        }

        public void renderToolTip(PoseStack p_230443_1_, int p_230443_2_, int p_230443_3_) {
            MinecartUnLoaderScreen.this.renderTooltip(p_230443_1_,
                    Component.translatable(
                            menu.getIsUnloader()
                                ? (menu.getLeaveOneInStack()
                                    ? "Leave one item in minecart slots"
                                    : "Empty minecart slots entirely")
                                : (menu.getLeaveOneInStack()
                                    ? "Leave one item in loader slots"
                                    : "Empty loader slots entirely")
                    ) , p_230443_2_, p_230443_3_);
        }

        public void renderButton(PoseStack matrix, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
            RenderSystem.setShaderTexture(0, display);

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

        @Override
        public void updateNarration(NarrationElementOutput p_169152_) {

        }
    }

    @OnlyIn(Dist.CLIENT)
    class OutputTypeButton extends AbstractButton {

        protected OutputTypeButton(int x, int y) {
            super(x, y, 18, 18, Component.empty());
        }

        public void renderToolTip(PoseStack p_230443_1_, int p_230443_2_, int p_230443_3_) {
            MinecartUnLoaderScreen.this.renderTooltip(p_230443_1_,
                    Component.translatable(menu.getRedstoneOutput()
                            ? "Output redstone activation"
                            : "Output to comparator"
                    ) , p_230443_2_, p_230443_3_);
        }

        public void renderButton(PoseStack matrix, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
            RenderSystem.setShaderTexture(0, display);

            boolean mouse_on = isDragging() && this.isHovered;

            if (menu.getRedstoneOutput()) {
                if (mouse_on) {
                    this.blit(matrix, x,y, 212+18, 36, 18, 18);
                }
                else {
                    this.blit(matrix, x, y, 212, 36, 18, 18);
                }
            }
            else {
                if (mouse_on) {
                    this.blit(matrix, x,y, 230, 18, 18, 18);
                }
                else {
                    // Render nothing. This is already on the backdrop.
                }
            }
        }

        @Override
        public void onPress() {
            MoreMinecartsPacketHandler.MinecartLoaderPacket packet = menu.getCurrentPacket();
            packet.redstone_output = !packet.redstone_output;
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(packet);
        }

        @Override
        public void updateNarration(NarrationElementOutput p_169152_) {

        }
    }

}
