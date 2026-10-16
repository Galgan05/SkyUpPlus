package net.galgan.skyupplus.mixin;

import net.galgan.skyupplus.features.Abilities;
import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class OverlayMessageHook {

    @Inject(method = "setOverlayMessage", at = @At("HEAD"))
    private void onOverlay(Component message, boolean tinted, CallbackInfo ci) {
        String s = message.getString();
        if (s.startsWith("Prace » Aktywowano Pług")) {
            Abilities.lastPlugActivation = System.currentTimeMillis()/1000;
        }
    }
}
