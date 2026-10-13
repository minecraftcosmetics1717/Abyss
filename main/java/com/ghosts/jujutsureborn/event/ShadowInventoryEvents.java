package com.ghosts.jujutsureborn.event;

import com.ghosts.jujutsureborn.JujutsuReborn;
import com.ghosts.jujutsureborn.capability.InnerselfInventoryCapability;
import com.ghosts.jujutsureborn.capability.ModCapabilities;
import com.ghosts.jujutsureborn.capability.ShadowInventoryCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JujutsuReborn.MODID)
public class ShadowInventoryEvents {

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(
                    new ResourceLocation(JujutsuReborn.MODID, "shadow_inventory"),
                    new ShadowInventoryCapability()
            );
            event.addCapability(
                    new ResourceLocation(JujutsuReborn.MODID, "innerself_inventory"),
                    new InnerselfInventoryCapability()
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();

        // Shadow Inventory
        event.getOriginal().getCapability(ModCapabilities.SHADOW_INVENTORY).ifPresent(oldCap -> {
            event.getEntity().getCapability(ModCapabilities.SHADOW_INVENTORY).ifPresent(newCap -> {
                newCap.getInventory().deserializeNBT(oldCap.getInventory().serializeNBT());
            });
        });

        // Innerself Inventory
        event.getOriginal().getCapability(ModCapabilities.INNERSELF_INVENTORY).ifPresent(oldCap -> {
            event.getEntity().getCapability(ModCapabilities.INNERSELF_INVENTORY).ifPresent(newCap -> {
                newCap.getInventory().deserializeNBT(oldCap.getInventory().serializeNBT());
            });
        });

        event.getOriginal().invalidateCaps();
    }
}