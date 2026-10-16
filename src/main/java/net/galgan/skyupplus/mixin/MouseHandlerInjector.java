package net.galgan.skyupplus.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.galgan.skyupplus.config.SUPConfig;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public class MouseHandlerInjector {
    @WrapWithCondition(
            method = "turnPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V")
    )
    private boolean sup$mouseLock(LocalPlayer player, double yRot, double xRot) {
        return !SUPConfig.get().automation.mouseLockToggle;
    }
}