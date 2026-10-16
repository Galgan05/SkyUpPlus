package net.galgan.skyupplus.clickers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.galgan.skyupplus.config.SUPConfig;
import net.minecraft.client.KeyMapping;

public class LeftHold {
    private static boolean wasHolding = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            KeyMapping attack = mc.options.keyAttack;

            if (SUPConfig.get().automation.leftHoldToggle && mc.gui.screen() == null) {
                attack.setDown(true);
                wasHolding = true;
            } else if (wasHolding) {
                attack.setDown(false);
                wasHolding = false;
            }
        });
    }
}
