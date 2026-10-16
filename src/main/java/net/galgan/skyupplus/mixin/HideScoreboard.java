package net.galgan.skyupplus.mixin;

import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class HideScoreboard {
    @Inject(
            method = "displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/scores/Objective;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideScoreboard(GuiGraphicsExtractor graphics, Objective objective, CallbackInfo ci) {
        if (SUPConfig.get().scoreboard.customScoreboard && ServerRestrictor.isAllowed()) {
            ci.cancel();
        }
    }
}