package com.alc.moreminecarts.containers;

import com.alc.moreminecarts.misc.ChunkLoaderSlot;
import com.alc.moreminecarts.tile_entities.ChunkLoaderTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChunkLoaderContainer extends Container {
    private final int LOG_STEEPNESS = 100;

    private final IInventory inventory;
    private final IIntArray data;
    protected final World level;

    public ChunkLoaderContainer(ContainerType<?> type, int n, PlayerInventory player_inventory, IInventory inventory, IIntArray data) {
        super(type, n);
        this.inventory = player_inventory;
        this.data = data;
        this.level = player_inventory.player.level;

        // TODO proper positioning
        this.addSlot(new ChunkLoaderSlot(inventory, 0, 0, 0));

        // player inventory slots, taken from the AbstractFurnaceContainer code.
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(player_inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(player_inventory, k, 8 + k * 18, 142));
        }

        this.addDataSlots(data);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return false;
    }

    // TODO
    @Override
    public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
        return super.quickMoveStack(p_82846_1_, p_82846_2_);
    }

    @OnlyIn(Dist.CLIENT)
    public int getSize() {
        return 1;
    }

    @OnlyIn(Dist.CLIENT)
    public int getTimeLeft() {
        return Math.abs(this.data.get(0)) - 1;
    }

    @OnlyIn(Dist.CLIENT)
    public int getLogProgress() {
        return (int)(Math.log10( ((float)getTimeLeft()/ChunkLoaderTile.MAX_TIME_LEFT)*(LOG_STEEPNESS-1) + LOG_STEEPNESS ));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isEnabled() {
        return this.data.get(0) >= 0;
    }
}
