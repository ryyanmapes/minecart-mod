package com.alc.moreminecarts.client;

import com.alc.moreminecarts.containers.FilterUnloaderContainer;
import com.alc.moreminecarts.containers.MinecartUnLoaderContainer;
import com.alc.moreminecarts.proxy.MoreMinecartsPacketHandler;
import com.alc.moreminecarts.tile_entities.FilterUnloaderTile;
import com.alc.moreminecarts.tile_entities.MinecartLoaderTile;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
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
import java.util.logging.Filter;

@OnlyIn(Dist.CLIENT)
public class FilterUnloaderScreen extends AbstractContainerScreen<FilterUnloaderContainer> {
    private static final ResourceLocation display = new ResourceLocation("moreminecarts:textures/gui/filter_loader_gui.png");
    private final List<AbstractButton> buttons = Lists.newArrayList();

    public FilterUnloaderScreen(FilterUnloaderContainer container, Inventory inv, Component titleIn) {
        super(container, inv, Component.translatable("Filter Unloader"));
    }

    @Override
    public Component getTitle() {
        return Component.translatable("Filter Unloader");
    }

    private void addButton(AbstractButton p_169617_) {
        this.addRenderableWidget(p_169617_);
        this.buttons.add(p_169617_);
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new OutputTypeButton(leftPos + 46, topPos + 6));
        this.addButton(new OnlyLockedButton(leftPos + 68, topPos + 6));
        this.addButton(new ComparatorOutputButton(leftPos + 90, topPos + 6));
        this.addButton(new LeaveOneInStackButton(leftPos + 112, topPos + 6));
        this.addButton(new FilterTypeButton(leftPos + 7, topPos + 6));
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
        this.blit(matrix, leftPos, topPos - 14, 0, 0, 176, 180);

    }

    // Taken from BeaconScreen, for tooltip rendering.
    @Override
    protected void renderLabels(PoseStack matrix, int p_230451_2_, int p_230451_3_) {
        this.font.draw(matrix, getTitle(), (float)this.titleLabelX, (float)this.titleLabelY - 14, 4210752);
        this.font.draw(matrix, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);

    }

    @OnlyIn(Dist.CLIENT)
    class ComparatorOutputButton extends MMButton {

        protected ComparatorOutputButton(int x, int y) {
            super(x,y);
        }

        public void renderWidget(PoseStack matrix, int x, int y, float p_230431_4_) {
            RenderSystem.setShaderTexture(0, display);

            boolean mouse_on = isDragging() && this.isHovered;

            switch (menu.getComparatorOutputType()) {
                case done_loading:
                    if (mouse_on) {
                        this.blit(matrix, xPos,yPos, 176+18, 18, 18, 18);
                    }
                    else {
                        this.blit(matrix, xPos,yPos, 176, 18, 18, 18);
                    }
                    break;
                case cart_full:
                    if (mouse_on) {
                        if (menu.getIsUnloader()) this.blit(matrix, xPos,yPos, 176+18+36, 0, 18, 18);
                        else this.blit(matrix, xPos,yPos, 176+18, 0, 18, 18);
                    }
                    else {
                        if (menu.getIsUnloader()) this.blit(matrix, xPos,yPos, 176+36, 0, 18, 18);
                        //this.blit(matrix, x, y, 176, 0, 18, 18);
                    }
                    break;
                case cart_fullness:
                    if (mouse_on) {
                        this.blit(matrix, xPos,yPos, 176+18, 36, 18, 18);
                    }
                    else {
                        this.blit(matrix, xPos,yPos, 176, 36, 18, 18);
                    }
                    break;
                default:
            }

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

            this.setTooltip(Tooltip.create(Component.translatable(text)));
        }

        @Override
        public void onPress() {
            MoreMinecartsPacketHandler.MinecartLoaderPacket packet = menu.getCurrentPacket();
            packet.output_type = MinecartLoaderTile.ComparatorOutputType.next(packet.output_type);
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(packet);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput p_259858_) {}
    }

    @OnlyIn(Dist.CLIENT)
    class FilterTypeButton extends MMButton {

        protected FilterTypeButton(int x, int y) {
            super(x, y);
        }

        public void renderWidget(PoseStack matrix, int x, int y, float p_230431_4_) {
            RenderSystem.setShaderTexture(0, display);

            boolean mouse_on = isDragging() && this.isHovered;

            switch (menu.getFilterType()) {
                case allow_for_all:
                    if (mouse_on) {
                        this.blit(matrix, xPos,yPos, 176+18, 126, 18, 18);
                    }
                    else {
                        this.blit(matrix, xPos,yPos, 176, 126, 18, 18);
                    }
                    break;
                case disallow_for_all:
                    if (mouse_on) {
                        this.blit(matrix, xPos,yPos, 176+18, 126+18, 18, 18);
                    }
                    else {
                        this.blit(matrix, xPos,yPos, 176, 126+18, 18, 18);
                    }
                    break;
                case allow_per_slot:
                    if (mouse_on) {
                        this.blit(matrix, xPos,yPos, 176+18, 126+36, 18, 18);
                    }
                    else {
                        this.blit(matrix, xPos,yPos, 176, 126+36, 18, 18);
                    }
                    break;
                default:
            }

            String text;
            switch (menu.getFilterType()) {
                case allow_per_slot:
                    text = "Take items matching respective filter slot";
                    break;
                case allow_for_all:
                    text = "Take items matching any filter slot";
                    break;
                case disallow_for_all:
                    text = "Take items not matching any filter slot";
                    break;
                default:
                    text = "ERROR";
            }

            this.setTooltip(Tooltip.create(Component.translatable(text)));
        }

        @Override
        public void onPress() {
            MoreMinecartsPacketHandler.MinecartLoaderPacket packet = menu.getCurrentPacket();
            packet.filterType = FilterUnloaderTile.FilterType.next(packet.filterType);
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(packet);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput p_259858_) {}
    }

    @OnlyIn(Dist.CLIENT)
    class OnlyLockedButton extends MMButton {

        protected OnlyLockedButton(int x, int y) {
            super(x, y);
        }

        public void renderWidget(PoseStack matrix, int x, int y, float p_230431_4_) {
            RenderSystem.setShaderTexture(0, display);

            boolean mouse_on = isDragging() && this.isHovered;

            if (menu.getLockedMinecartsOnly()) {
                if (mouse_on) {
                    if (menu.getIsUnloader()) this.blit(matrix, xPos,yPos, 176+18+36, 108, 18, 18);
                    else this.blit(matrix, xPos,yPos, 176+18, 108, 18, 18);
                }
                else {
                    if (menu.getIsUnloader()) this.blit(matrix, xPos,yPos, 176+36, 108, 18, 18);
                    else this.blit(matrix, xPos,yPos, 176, 108, 18, 18);
                }
            }
            else {
                if (mouse_on) {
                    this.blit(matrix, xPos,yPos, 176+18, 108-18, 18, 18);
                }
                else {
                    // Render nothing. This is already on the backdrop.
                }
            }

            this.setTooltip(Tooltip.create(Component.translatable(
                    menu.getLockedMinecartsOnly()
                    ? "Consider only locked minecarts"
                    : "Consider all minecarts"
            )));
        }

        @Override
        public void onPress() {
            MoreMinecartsPacketHandler.MinecartLoaderPacket packet = menu.getCurrentPacket();
            packet.locked_minecarts_only = !packet.locked_minecarts_only;
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(packet);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput p_259858_) {}
    }

    @OnlyIn(Dist.CLIENT)
    class LeaveOneInStackButton extends MMButton {

        protected LeaveOneInStackButton(int x, int y) {
            super(x, y);
        }

        public void renderWidget(PoseStack matrix, int x, int y, float p_230431_4_) {
            RenderSystem.setShaderTexture(0, display);

            boolean mouse_on = isDragging() && this.isHovered;

            if (menu.getLeaveOneInStack()) {
                if (mouse_on) {
                    if (menu.getIsUnloader()) this.blit(matrix, xPos,yPos, 176+18+36, 72, 18, 18);
                    else this.blit(matrix,xPos,yPos, 176+18, 72, 18, 18);
                }
                else {
                    if (menu.getIsUnloader()) this.blit(matrix, xPos, yPos, 176+36, 72, 18, 18);
                    else this.blit(matrix, xPos,yPos, 176, 72, 18, 18);
                }
            }
            else {
                if (mouse_on) {
                    this.blit(matrix, xPos,yPos, 176+18, 72-18, 18, 18);
                }
                else {
                    // Render nothing. This is already on the backdrop.
                }
            }

            this.setTooltip(Tooltip.create(Component.translatable(
                    menu.getIsUnloader()
                            ? (menu.getLeaveOneInStack()
                            ? "Leave one item in minecart slots"
                            : "Empty minecart slots entirely")
                            : (menu.getLeaveOneInStack()
                            ? "Leave one item in loader slots"
                            : "Empty loader slots entirely")
            )));
        }

        @Override
        public void onPress() {
            MoreMinecartsPacketHandler.MinecartLoaderPacket packet = menu.getCurrentPacket();
            packet.leave_one_item_in_stack = !packet.leave_one_item_in_stack;
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(packet);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput p_259858_) {}
    }

    @OnlyIn(Dist.CLIENT)
    class OutputTypeButton extends MMButton {

        protected OutputTypeButton(int x, int y) {
            super(x, y);
        }

        public void renderWidget(PoseStack matrix, int x, int y, float p_230431_4_) {
            RenderSystem.setShaderTexture(0, display);

            boolean mouse_on = isDragging() && this.isHovered;

            if (menu.getRedstoneOutput()) {
                if (mouse_on) {
                    this.blit(matrix, xPos,yPos, 212+18, 36, 18, 18);
                }
                else {
                    this.blit(matrix, xPos,yPos, 212, 36, 18, 18);
                }
            }
            else {
                if (mouse_on) {
                    this.blit(matrix, xPos,yPos, 230, 18, 18, 18);
                }
                else {
                    // Render nothing. This is already on the backdrop.
                }
            }

            this.setTooltip(Tooltip.create(Component.translatable(menu.getRedstoneOutput()
                    ? "Output redstone activation"
                    : "Output to comparator"
            )));
        }

        @Override
        public void onPress() {
            MoreMinecartsPacketHandler.MinecartLoaderPacket packet = menu.getCurrentPacket();
            packet.redstone_output = !packet.redstone_output;
            MoreMinecartsPacketHandler.INSTANCE.sendToServer(packet);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput p_259858_) {}
    }

}
