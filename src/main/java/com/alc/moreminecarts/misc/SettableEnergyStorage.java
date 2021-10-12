package com.alc.moreminecarts.misc;

import net.minecraftforge.energy.EnergyStorage;

public class SettableEnergyStorage extends EnergyStorage {
    public SettableEnergyStorage(int capacity) {
        super(capacity);
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
}
