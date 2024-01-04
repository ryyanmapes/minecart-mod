package com.alc.moreminecarts.misc;


import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.MoreMinecartsMod;
import com.alc.moreminecarts.entities.HSMinecartEntities;
import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.alc.moreminecarts.items.CouplerItem;
import com.alc.moreminecarts.registry.MMBlocks;
import com.alc.moreminecarts.registry.MMItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedList;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MMCreativeTabs {

    public static final ResourceLocation BASE_TAB_ID = new ResourceLocation(MMConstants.modid, "base");

    private static class DisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {

        @Override
        public void accept(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
            output.acceptAll(
                    MMItems.ITEMS.getEntries().stream().map(
                            (obj) -> new ItemStack( obj.get() )
                    ).collect(Collectors.toList()));
            output.accept(new ItemStack( MMItems.ARITHMETIC_RAIL_ITEM.get()));
        }
    }

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MMConstants.modid);

    public static final RegistryObject<CreativeModeTab> tab = TABS.register("more_minecarts_tab", () ->
           CreativeModeTab.builder()
                   .icon(() -> new ItemStack(MMItems.PISTON_PUSHCART_ITEM.get()))
                   .title(Component.translatable("More Minecarts and Rails"))
                   .displayItems(new DisplayItemsGenerator())
                   .build());

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }

}
