package com.alc.moreminecarts.containers;

import com.alc.moreminecarts.MMReferences;
import com.alc.moreminecarts.entities.BatteryCartEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;

public class BatteryCartContainer extends AbstractContainerMenu {

    private BatteryCartEntity entity;
    private final ContainerData data;
    protected final Level level;

    // For use on the client.
    public BatteryCartContainer(int n, Level world, Inventory player_inventory, Player player_entity) {
        super(MMReferences.tank_cart_c, n);

        this.entity = null;
        this.level = world;
        this.data = new SimpleContainerData(1);

        CommonInitialization(player_inventory);
    }

    // For use with the entity cart.
    public BatteryCartContainer(int n, Level world, BatteryCartEntity entity, Inventory player_inventory, Player player_entity) {
        super(MMReferences.battery_cart_c, n);

        this.entity = entity;
        this.level = player_inventory.player.level;
        this.data = entity.dataAccess;

        CommonInitialization(player_inventory);
    }

    // Only adds player inventory slots here.
    public void CommonInitialization(Inventory player_inventory) {

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
    public boolean stillValid(Player player) {
        return this.entity.stillValid(player);
    }

    public int getEnergy() {
        if (entity == null) {
            int id = data.get(0);
            Entity ent = level.getEntity(id);
            if (ent instanceof BatteryCartEntity) entity = (BatteryCartEntity) ent;
            else return 0;
        }
        return entity.getEnergyAmount();
    }

}
