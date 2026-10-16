package net.galgan.skyupplus.clickers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.galgan.skyupplus.config.SUPConfig;
import net.minecraft.client.KeyMapping;

public class RightHold {

    private static boolean wasHolding = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            KeyMapping use = mc.options.keyUse;

            if (SUPConfig.get().automation.rightHoldToggle && mc.gui.screen() == null) {
                use.setDown(true);
                wasHolding = true;
            } else if (wasHolding) {
                use.setDown(false);
                wasHolding = false;
            }
        });
    }
}
