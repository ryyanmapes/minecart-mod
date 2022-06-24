package com.alc.moreminecarts.containers;

import com.alc.moreminecarts.proxy.MoreMinecartsPacketHandler;
import com.alc.moreminecarts.registry.MMBlocks;
import com.alc.moreminecarts.registry.MMContainers;
import com.alc.moreminecarts.tile_entities.AbstractCommonLoader;
import com.alc.moreminecarts.tile_entities.MinecartLoaderTile;
import com.alc.moreminecarts.tile_entities.MinecartUnloaderTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class MinecartUnLoaderContainer extends AbstractContainerMenu {

    private final Container inventory;
    private final ContainerData data;
    private final Level level;

    private AbstractCommonLoader tile;

    public MinecartUnLoaderContainer(int n, Level world, Inventory player_inventory, Player player_entity) {
        super(MMContainers.MINECART_LOADER_CONTAINER.get(), n);

        this.tile = null;
        this.level = player_inventory.player.level;
        this.inventory = new SimpleContainer(9);
        this.data = new SimpleContainerData(5);

        CommonInitialization(player_inventory);
    }

    // For use with tile entity loaders (server).
    public MinecartUnLoaderContainer(int n, Level world, BlockPos pos, Inventory player_inventory, Player player_entity) {
        super(MMContainers.MINECART_LOADER_CONTAINER.get(), n);

        BlockEntity te = world.getBlockEntity(pos);

        if (te instanceof MinecartLoaderTile) {
            MinecartLoaderTile loader = (MinecartLoaderTile) te;
            this.inventory = loader;
            this.data = loader.dataAccess;
            this.tile = loader;
        } else if (te instanceof MinecartUnloaderTile) {
            MinecartUnloaderTile unloader = (MinecartUnloaderTile) te;
            this.inventory = unloader;
            this.data = unloader.dataAccess;
            this.tile = unloader;
        } else {
            // should error out?
            this.inventory = new SimpleContainer(9);
            this.data = new SimpleContainerData(5);
            this.tile = null;
        }
        this.level = player_inventory.player.level;

        CommonInitialization(player_inventory);
    }

    // For use with tile entity loaders (client).
    public MinecartUnLoaderContainer(int p_38969_, Inventory p_38970_, Container p_38971_, ContainerData p_38972_, BlockPos tilePos) {
        super(MMContainers.MINECART_LOADER_CONTAINER.get(), p_38969_);

        this.level = p_38970_.player.level;
        this.tile = (AbstractCommonLoader) level.getBlockEntity(tilePos);
        this.inventory = p_38971_;
        this.data = p_38972_;

        CommonInitialization(p_38970_);
    }

    public void CommonInitialization(Inventory player_inventory) {

        checkContainerSize(inventory, 9);
        checkContainerDataCount(data, 5);

        // content slots
        for(int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 42));
        }

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
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    // Taken from the chest container. No clue what this does really.
    @Override
    public ItemStack quickMoveStack(Player p_82846_1_, int p_82846_2_) {
        int containerRows = 1;
        ItemStack lvt_3_1_ = ItemStack.EMPTY;
        Slot lvt_4_1_ = (Slot)this.slots.get(p_82846_2_);
        if (lvt_4_1_ != null && lvt_4_1_.hasItem()) {
            ItemStack lvt_5_1_ = lvt_4_1_.getItem();
            lvt_3_1_ = lvt_5_1_.copy();
            if (p_82846_2_ < containerRows * 9) {
                if (!this.moveItemStackTo(lvt_5_1_, containerRows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(lvt_5_1_, 0, containerRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (lvt_5_1_.isEmpty()) {
                lvt_4_1_.set(ItemStack.EMPTY);
            } else {
                lvt_4_1_.setChanged();
            }
        }

        return lvt_3_1_;
    }

    @OnlyIn(Dist.CLIENT)
    public int getSize() {
        return 9;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public FluidStack getFluids() {
        if (tile == null) {
            AttemptFindTile();
            if (tile == null) return null;
        }
        return tile.getFluidStack();
    }

    public int getEnergy() {
        if (tile == null) {
            AttemptFindTile();
            if (tile == null) return 0;
        }
        return tile.getEnergyAmount();
    }

    protected void AttemptFindTile() {
        BlockPos location = new BlockPos(data.get(2),data.get(3),data.get(4));
        if (location.getX() == 0 && location.getY() == 0 && location.getZ() == 0) return;
        BlockEntity ent = level.getBlockEntity(location);
        if (ent == null || !(ent instanceof AbstractCommonLoader)) return;
        tile = (AbstractCommonLoader) ent;
    }

    public boolean getRedstoneOutput()
    {
        return (this.data.get(0) & 16) == 16;
    }

    public boolean getLockedMinecartsOnly()
    {
        return this.data.get(0) % 2 == 1;
    }

    public boolean getLeaveOneInStack()
    {
        return (this.data.get(0) & 2) == 2;
    }

    public MinecartLoaderTile.ComparatorOutputType getComparatorOutputType()
    {
        return MinecartLoaderTile.ComparatorOutputType.fromInt((this.data.get(0) & 12) >> 2);
    }

    public MoreMinecartsPacketHandler.MinecartLoaderPacket getCurrentPacket() {
        return new MoreMinecartsPacketHandler.MinecartLoaderPacket(
                false,
                getLockedMinecartsOnly(),
                getLeaveOneInStack(),
                getComparatorOutputType(),
                getRedstoneOutput()
        );
    }

    public boolean getIsUnloader() {
        return data.get(1) >= 1;
    }

    public void setOptions(boolean locked_minecarts_only, boolean leave_one_in_stack, MinecartLoaderTile.ComparatorOutputType comparator_output, boolean redstone_output) {
       int options = (locked_minecarts_only?1:0) + ((leave_one_in_stack?1:0) << 1) + (comparator_output.toInt() << 2) + ((redstone_output?1:0) << 4);
       this.setData(0, options);
       this.broadcastChanges();
    }
}
