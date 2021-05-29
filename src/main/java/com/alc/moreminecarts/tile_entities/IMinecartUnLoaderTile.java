package com.alc.moreminecarts.tile_entities;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.fluids.FluidStack;

public interface IMinecartUnLoaderTile extends ISidedInventory, ITickableTileEntity, INamedContainerProvider {

    public FluidStack getFluidStack();

    public int getEnergyAmount();

}
