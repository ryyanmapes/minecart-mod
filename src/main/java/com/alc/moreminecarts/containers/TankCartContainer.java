package com.alc.moreminecarts.containers;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.TankCartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class TankCartContainer extends Container {

    private TankCartEntity entity;
    private final IIntArray data;
    protected final World level;

    // For use on the client.
    public TankCartContainer(int n, World world, PlayerInventory player_inventory, PlayerEntity player_entity) {
        super(MMReferences.tank_cart_c, n);

        this.entity = null;
        this.level = world;
        this.data = new IntArray(1);

        CommonInitialization(player_inventory);
    }

    // For use with the entity cart.
    public TankCartContainer(int n, World world, TankCartEntity entity, PlayerInventory player_inventory, PlayerEntity player_entity) {
        super(MMReferences.tank_cart_c, n);

        this.entity = entity;
        this.level = player_inventory.player.level;
        this.data = entity.dataAccess;

        CommonInitialization(player_inventory);
    }

    // Only adds player inventory slots here.
    public void CommonInitialization(PlayerInventory player_inventory) {

        checkContainerDataCount(data, 1);

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
        return this.entity.stillValid(player);
    }

    @Nullable
    public FluidStack getFluids() {
        if (entity == null) {
            int id = data.get(0);
            Entity ent = level.getEntity(id);
            if (ent instanceof TankCartEntity) entity = (TankCartEntity) ent;
            else return null;
        }
        return entity.getFluidStack();
    }

}
