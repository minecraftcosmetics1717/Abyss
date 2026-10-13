package com.ghosts.jujutsureborn;

import com.ghosts.jujutsureborn.effect.ModEffects;
import com.ghosts.jujutsureborn.entity.RockEntity;
import com.ghosts.jujutsureborn.event.*;
import com.ghosts.jujutsureborn.init.JujutsuRebornEntities;
import com.ghosts.jujutsureborn.item.RockItem;
import com.ghosts.jujutsureborn.menu.ModMenus;
import com.ghosts.jujutsureborn.network.ModNetwork;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(JujutsuReborn.MODID)
public class JujutsuReborn {
    public static final String MODID = "jujutsureborn";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> ROCK = ITEMS.register("rock", () -> new RockItem(
            new Item.Properties().stacksTo(16)
    ));


    public static final GameRules.Key<GameRules.BooleanValue> STEVENSON_NOT_DESPAWN =
            GameRules.register("stevensonNotDespawn", GameRules.Category.MISC, GameRules.BooleanValue.create(false));

    public JujutsuReborn() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(modEventBus);
        JujutsuRebornEntities.ENTITY_TYPES.register(modEventBus);

        ModMenus.MENUS.register(modEventBus);
        ModNetwork.register();

        ModEffects.register(modEventBus);

        modEventBus.addListener(this::addCreativeTabs);
        modEventBus.addListener(this::onEntityAttributeCreation);

        MinecraftForge.EVENT_BUS.register(ShadowDiveTickHandler.class);
    }

    private void addCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ROCK);
        }
    }

    private void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(JujutsuRebornEntities.ROCK.get(), RockEntity.createAttributes().build());
    }
}