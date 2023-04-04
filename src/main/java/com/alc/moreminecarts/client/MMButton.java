package com.alc.moreminecarts.client;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class MMButton extends AbstractButton {

    public int xPos;
    public int yPos;

    public MMButton(int x, int y) {
        super(x, y, 18, 18, Component.empty());

        this.xPos = x;
        this.yPos = y;
    }

    @Override
    public void onPress() {
        
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

    }
}
