package com.alc.moreminecarts.misc;


import com.alc.moreminecarts.MMConstants;
import com.alc.moreminecarts.MoreMinecartsMod;
import com.alc.moreminecarts.entities.HSMinecartEntities;
import com.alc.moreminecarts.entities.PistonPushcartEntity;
import com.alc.moreminecarts.items.CouplerItem;
import com.alc.moreminecarts.registry.MMItems;
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
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedList;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MMCreativeTabs {

    public static final ResourceLocation BASE_TAB_ID = new ResourceLocation(MMConstants.modid, "base");

    private static CreativeModeTab tab;

    @SubscribeEvent
    public static void onCreativeModeTabRegister(CreativeModeTabEvent.Register event) {

        CreativeModeTab spawnEggsTab = CreativeModeTabs.SPAWN_EGGS;
        LinkedList spawnEggsTabList = new LinkedList();
        spawnEggsTabList.add(spawnEggsTab);

        tab = event.registerCreativeModeTab(BASE_TAB_ID, new LinkedList<>(), spawnEggsTabList, builder -> {
            builder.icon(() -> new ItemStack(MMItems.PISTON_PUSHCART_ITEM.get()))
                    .title(Component.translatable("More Minecarts and Rails"))
                    .displayItems(new DisplayItemsGenerator())
                    .build();
        });

    }

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

}
