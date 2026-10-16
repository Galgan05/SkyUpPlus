package net.galgan.skyupplus.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.multiplayer.ServerData;

public class ServerRestrictor {
    private static boolean allowedServer = false;

    public static void register() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, mc) -> {
            ServerData server = mc.getCurrentServer();
            if (server != null) {
                allowedServer = server.ip.contains("skyup.pl");
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, mc) -> allowedServer = false);
    }

    public static boolean isAllowed() {
        return allowedServer;
    }
}