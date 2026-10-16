package net.galgan.skyupplus.features;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.galgan.skyupplus.util.HudElement;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.resources.Identifier;


public class HUD {

    public static void register() {

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("skyupplus", "hud"),
                HUD::render
        );
    }

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker counter) {
        if (!ServerRestrictor.isAllowed()) return;
        HudElement.renderAll(graphics);
    }
}
