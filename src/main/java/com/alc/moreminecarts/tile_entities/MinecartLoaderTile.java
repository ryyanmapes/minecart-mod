package com.alc.moreminecarts.tile_entities;

import com.alc.moreminecarts.containers.MinecartUnLoaderContainer;
import com.alc.moreminecarts.entities.ChunkLoaderCartEntity;
import com.alc.moreminecarts.registry.MMTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class MinecartLoaderTile extends AbstractCommonLoader {

    public MinecartLoaderTile(BlockPos pos, BlockState state) {
        super(MMTileEntities.MINECART_LOADER_TILE_ENTITY.get(), pos, state);
        last_redstone_output = !redstone_output;
    }

    @Override
    public boolean getIsUnloader() {
        return false;
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        setChanged();
        return new MinecartUnLoaderContainer(i, inventory, this, this.dataAccess, getBlockPos());
    }

    @Override
    protected AbstractContainerMenu createMenu(int p_213906_1_, Inventory p_213906_2_) {
        return null;
    }

    public static void doTick(Level level, BlockPos pos, BlockState state, MinecartLoaderTile ent) {
        ent.tick();
    }

    public void tick() {

        if (!level.isClientSide) {

            if (!isOnCooldown()) {

                List<AbstractMinecart> minecarts = getLoadableMinecartsInRange();
                float criteria_total = 0;
                for (AbstractMinecart minecart : minecarts) {

                    LazyOptional<IFluidHandler> tankCapability = minecart.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
                    LazyOptional<IEnergyStorage> energyCapability = minecart.getCapability(CapabilityEnergy.ENERGY);
                    if (tankCapability.isPresent()) {
                        IFluidHandler fluid_handler = tankCapability.orElse(null);
                        criteria_total += doFluidLoads(fluid_handler);
                    }
                    else if (energyCapability.isPresent()) {
                        IEnergyStorage energy_storage = energyCapability.orElse(null);
                        criteria_total += doElectricLoads(energy_storage);
                    }
                    else if (minecart instanceof AbstractMinecartContainer) {
                        criteria_total += doMinecartLoads((AbstractMinecartContainer) minecart);
                    } else if (minecart instanceof MinecartFurnace) {
                        criteria_total += doFuranceCartLoads((MinecartFurnace) minecart);
                    }
                }

                if (minecarts.size() == 0) criteria_total = 0;
                else criteria_total /= minecarts.size();

                if (comparator_output != ComparatorOutputType.cart_fullness)
                    criteria_total = (float) Math.floor(criteria_total);

                int new_comparator_output_value = (int) (criteria_total * 15);
                if (new_comparator_output_value != comparator_output_value || last_redstone_output != redstone_output) {
                    comparator_output_value = new_comparator_output_value;
                    last_redstone_output = redstone_output;
                    level.updateNeighbourForOutputSignal(getBlockPos(), this.getBlockState().getBlock());
                    level.updateNeighborsAt(getBlockPos(), this.getBlockState().getBlock());
                }

            } else {
                decCooldown();
            }

            if (changed_flag) {
                this.setChanged();
                changed_flag = false;
            }

        }
    }

    public float doFluidLoads(IFluidHandler minecart_handler) {
        boolean changed = false;

        IFluidHandler our_fluid_handler = fluid_handler.orElse(null);
        FluidStack our_fluid_stack = our_fluid_handler.getFluidInTank(0);

        float fluid_content_proportion = 0;
        for (int i = 0; i < minecart_handler.getTanks(); i++) {

            if (minecart_handler.getTankCapacity(i) > 0)
                fluid_content_proportion += (float)minecart_handler.getFluidInTank(i).getAmount() / minecart_handler.getTankCapacity(i);

            if (our_fluid_stack.getAmount() <= (leave_one_in_stack? 1 : 0)) continue;

            boolean did_load = false;

            if (minecart_handler.isFluidValid(i, our_fluid_stack)) {

                FluidStack add_to_stack = minecart_handler.getFluidInTank(i);

                if (add_to_stack.isEmpty()) {
                    FluidStack new_stack = our_fluid_stack.copy();
                    int transfer_amount = Math.min(1000, new_stack.getAmount());
                    new_stack.setAmount(transfer_amount);
                    minecart_handler.fill(new_stack, IFluidHandler.FluidAction.EXECUTE);
                    our_fluid_stack.shrink(transfer_amount);
                    did_load = true;
                }
                else if (add_to_stack.isFluidEqual(our_fluid_stack)) {
                    int true_count = our_fluid_stack.getAmount() - (leave_one_in_stack? 1 : 0);
                    int to_fill = minecart_handler.getTankCapacity(i) - add_to_stack.getAmount();
                    int transfer = Math.min(1000, Math.min(true_count, to_fill));

                    FluidStack adding_stack = add_to_stack.copy();
                    adding_stack.setAmount(transfer);
                    minecart_handler.fill(adding_stack, IFluidHandler.FluidAction.EXECUTE);
                    our_fluid_stack.shrink(transfer);
                    did_load = transfer > 0;
                }
            }

            if (did_load) {
                changed = true;
                break;
            }
        }
        if (minecart_handler.getTanks() > 0) fluid_content_proportion /= minecart_handler.getTanks();

        if (changed) {
            resetCooldown();
            changed_flag = true;
        }

        if (comparator_output == ComparatorOutputType.done_loading) return changed? 0.0f : 1.0f;
        else {
            return fluid_content_proportion;
        }
    }

    public float doElectricLoads(IEnergyStorage minecart_handler) {
        boolean changed = false;

        IEnergyStorage our_handler = energy_handler.orElse(null);

        if (minecart_handler.canReceive()) {

            int true_count = our_handler.getEnergyStored() - (leave_one_in_stack? 1 : 0);
            int to_fill = minecart_handler.getMaxEnergyStored() - minecart_handler.getEnergyStored();
            int transfer = Math.min(1000, Math.min(true_count, to_fill));

            minecart_handler.receiveEnergy(transfer, false);
            our_handler.extractEnergy(transfer, false);
            changed = transfer > 0;
        }

        if (changed) {
            resetCooldown();
            changed_flag = true;
        }

        if (comparator_output == ComparatorOutputType.done_loading) return changed? 0.0f : 1.0f;
        else {
            return (float)minecart_handler.getEnergyStored() / minecart_handler.getMaxEnergyStored();
        }
    }

    public float doMinecartLoads(AbstractMinecartContainer minecart) {
        boolean changed = false;
        for (ItemStack stack : items) {

            if (!stack.isEmpty() && !(leave_one_in_stack && stack.getCount() == 1) ) {

                for (int i = 0; i < minecart.getContainerSize() && !stack.isEmpty(); i++) {

                    boolean did_load = false;

                    if (minecart.canPlaceItem(i, stack)) {

                        ItemStack add_to_stack = minecart.getItem(i);

                        if (add_to_stack.isEmpty()) {
                            ItemStack new_stack = stack.copy();
                            int transfer_amount = Math.min(8, new_stack.getCount());
                            new_stack.setCount(transfer_amount);
                            minecart.setItem(i, new_stack);
                            stack.shrink(transfer_amount);
                            did_load = true;
                        }
                        else if (canMergeItems(add_to_stack, stack)) {
                            int true_count = stack.getCount() - (leave_one_in_stack? 1 : 0);
                            int to_fill = add_to_stack.getMaxStackSize() - add_to_stack.getCount();
                            int transfer = Math.min(8, Math.min(true_count, to_fill));
                            stack.shrink(transfer);
                            add_to_stack.grow(transfer);
                            did_load = transfer > 0;
                        }
                    }

                    if (did_load) {
                        changed = true;
                        break;
                    }
                }
            }
        }

        if (changed) {
            resetCooldown();
            changed_flag = true;
        }

        if (comparator_output == ComparatorOutputType.done_loading) return changed? 0.0f : 1.0f;
        else if (minecart instanceof ChunkLoaderCartEntity && comparator_output == ComparatorOutputType.cart_fullness) {
            return ((ChunkLoaderCartEntity)minecart).getComparatorSignal() / 15.0f;
        }
        else {
            return AbstractContainerMenu.getRedstoneSignalFromContainer(minecart) / 15.0f;
        }
    }

    public float doFuranceCartLoads(MinecartFurnace minecart) {
        boolean changed = false;

        BlockEntity te_at_minecart = level.getBlockEntity(minecart.getCurrentRailPosition());

        if (te_at_minecart instanceof LockingRailTile && ((LockingRailTile)te_at_minecart).locked_minecart == minecart) {
            LockingRailTile locking_rail_tile = (LockingRailTile)te_at_minecart;

            for (ItemStack stack : items) {
                if (locking_rail_tile.saved_fuel + 3600 > 32000) break;
                if (Ingredient.of(new ItemLike[]{Items.COAL, Items.CHARCOAL}).test(stack) && !(leave_one_in_stack && stack.getCount() == 1)) {
                    stack.shrink(1);
                    locking_rail_tile.saved_fuel += 3600;
                    changed = true;
                }
            }
        }
        else {

            for (ItemStack stack : items) {
                if (minecart.fuel + 3600 > 32000) break;
                if (Ingredient.of(new ItemLike[]{Items.COAL, Items.CHARCOAL}).test(stack) && !(leave_one_in_stack && stack.getCount() == 1)) {
                    stack.shrink(1);
                    minecart.fuel += 3600;
                    changed = true;
                }
            }

            if (minecart.xPush == 0 && minecart.zPush == 0)
            {
                minecart.xPush = minecart.getDeltaMovement().x;
                minecart.zPush = minecart.getDeltaMovement().z;
            }

        }

        if (changed) {
            resetCooldown();
            changed_flag = true;
        }

        if (comparator_output == ComparatorOutputType.done_loading) return changed? 0.0f : 1.0f;
        else {
            float fullness = Math.min(minecart.fuel / (32000.0f - 3600.0f), 1.0f);
            if (comparator_output == ComparatorOutputType.cart_full) fullness = (float)Math.floor(fullness);
            return fullness;
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("Minecart Loader");
    }

}
