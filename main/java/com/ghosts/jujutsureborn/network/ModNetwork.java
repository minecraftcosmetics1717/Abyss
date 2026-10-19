package com.ghosts.jujutsureborn.network;

import com.ghosts.jujutsureborn.JujutsuReborn;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(JujutsuReborn.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int id = 0;

    public static void register() {
        CHANNEL.registerMessage(id++, OpenShadowInventoryPacket.class,
                OpenShadowInventoryPacket::encode,
                OpenShadowInventoryPacket::new,
                OpenShadowInventoryPacket::handle);
        CHANNEL.registerMessage(id++, OpenLifeCharmsPacket.class,
                OpenLifeCharmsPacket::encode,
                OpenLifeCharmsPacket::new,
                OpenLifeCharmsPacket::handle);
        CHANNEL.registerMessage(id++, PacketSyncShadowDive.class,
                PacketSyncShadowDive::encode,
                PacketSyncShadowDive::new,
                PacketSyncShadowDive::handle);

        // Phone teleport
        CHANNEL.registerMessage(id++, RequestTeleportPacket.class,
                RequestTeleportPacket::encode,
                RequestTeleportPacket::new,
                RequestTeleportPacket::handle);
        CHANNEL.registerMessage(id++, TeleportRequestNotifyPacket.class,
                TeleportRequestNotifyPacket::encode,
                TeleportRequestNotifyPacket::new,
                TeleportRequestNotifyPacket::handle);
        CHANNEL.registerMessage(id++, RespondTeleportPacket.class,
                RespondTeleportPacket::encode,
                RespondTeleportPacket::new,
                RespondTeleportPacket::handle);
    }
}