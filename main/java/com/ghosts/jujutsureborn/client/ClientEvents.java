package com.ghosts.jujutsureborn.client;

import com.ghosts.jujutsureborn.JujutsuReborn;
import com.ghosts.jujutsureborn.menu.ModMenus;
import com.ghosts.jujutsureborn.network.ModNetwork;
import com.ghosts.jujutsureborn.network.OpenLifeCharmsPacket;
import com.ghosts.jujutsureborn.network.OpenShadowInventoryPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class ClientEvents {

    // B = Shadow Inventory (tech 6) OR Innerself (tech 15)
    public static final KeyMapping OPEN_SHADOW_INV = new KeyMapping(
            "key.jujutsureborn.shadow_inventory",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.jujutsureborn"
    );

    // P = Life Charms
    public static final KeyMapping OPEN_LIFE_CHARMS = new KeyMapping(
            "key.jujutsureborn.life_charms",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "key.categories.jujutsureborn"
    );

    @Mod.EventBusSubscriber(modid = JujutsuReborn.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(ModMenus.SHADOW_INVENTORY.get(), ShadowInventoryScreen::new);
                MenuScreens.register(ModMenus.LIFE_CHARMS.get(), LifeCharmsScreen::new);
                MenuScreens.register(ModMenus.INNERSELF_INVENTORY.get(), InnerselfInventoryScreen::new);
            });
        }

        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(OPEN_SHADOW_INV);
            event.register(OPEN_LIFE_CHARMS);
        }
    }

    @Mod.EventBusSubscriber(modid = JujutsuReborn.MODID, value = Dist.CLIENT)
    public static class ForgeBusEvents {

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            if (OPEN_SHADOW_INV.consumeClick()) {
                // Server decides: tech 6 → Shadow, tech 15 → Innerself
                ModNetwork.CHANNEL.sendToServer(new OpenShadowInventoryPacket());
            }

            if (OPEN_LIFE_CHARMS.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new OpenLifeCharmsPacket());
            }
        }
    }
}