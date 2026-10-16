package net.galgan.skyupplus.clickers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.mixin.MinecraftInvoker;


public class LeftClicker {
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (!SUPConfig.get().automation.leftClickerToggle || mc.player == null || mc.gui.screen() != null) return;

            if (++tickCounter >= SUPConfig.get().automation.leftClickInterval) {
                tickCounter = 0;
                ((MinecraftInvoker) mc).sup$startAttack();
            }
        });
    }
}
