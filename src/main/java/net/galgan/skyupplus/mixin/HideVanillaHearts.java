package net.galgan.skyupplus.mixin;

import net.galgan.skyupplus.config.SUPConfig;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Hud.class)
public abstract class HideVanillaHearts {

    @Inject(method = "extractHearts", at = @At("HEAD"), cancellable = true)
    private void skyupplus$hideHearts(CallbackInfo ci) {
        if (SUPConfig.get().main.healthBar.customHealthBar) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "extractArmor", at = @At("HEAD"), argsOnly = true, name = "numHealthRows")
    private static int skyupplus$oneHealthRow(int numHealthRows) {
        if (SUPConfig.get().main.healthBar.customHealthBar) {
            return 1;
        } else {
            return numHealthRows;
        }
    }
}