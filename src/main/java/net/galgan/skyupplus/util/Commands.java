package net.galgan.skyupplus.util;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.galgan.skyupplus.config.SUPConfig;

public class Commands {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            if(!ServerRestrictor.isAllowed()) return;

            dispatcher.register(ClientCommands.literal("sup").executes(ctx -> {
                SUPConfig.openGui();
                return 1;
            }));
        });
    }
}