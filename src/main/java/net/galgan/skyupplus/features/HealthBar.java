package net.galgan.skyupplus.features;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;

import java.awt.*;

public class HealthBar {

    private static final Identifier EMPTY_HEALTH_BAR =
            Identifier.fromNamespaceAndPath("skyupplus", "textures/gui/health_bar/empty_health_bar.png");
    private static final Identifier NORMAL_HEALTH_BAR =
            Identifier.fromNamespaceAndPath("skyupplus", "textures/gui/health_bar/normal_health_bar.png");
    private static final Identifier ABSORPTION_HEALTH_BAR =
            Identifier.fromNamespaceAndPath("skyupplus", "textures/gui/health_bar/absorption_health_bar.png");

    private static final int TEX_W = 80;
    private static final int TEX_H = 9;

    public static void register() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.HEALTH_BAR, Identifier.fromNamespaceAndPath("skyupplus", "health_bar"), (graphics, deltaTracker) -> {
            if (!ServerRestrictor.isAllowed()) return;
            if (!SUPConfig.get().main.healthBar.customHealthBar) return;

            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            float health = Math.max(0f, mc.player.getHealth());
            float maxHealth = Math.max(1f, mc.player.getMaxHealth());
            float absorption = Math.max(0f, mc.player.getAbsorptionAmount());
            float total = maxHealth + absorption;

            int redWidth = Math.round(TEX_W * Math.min(health, maxHealth) / total);
            int yellowEnd = Math.round(TEX_W * (Math.min(health, maxHealth) + absorption) / total);

            int x = graphics.guiWidth() / 2 - 91;
            int y = graphics.guiHeight() - 39;

            // BACKGROUND
            graphics.blit(RenderPipelines.GUI_TEXTURED, EMPTY_HEALTH_BAR,
                    x, y, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H);

            // NORMAL HEALTH
            if (redWidth > 0) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, NORMAL_HEALTH_BAR,
                        x, y, 0, 0, redWidth, TEX_H, TEX_W, TEX_H);
            }

            // ABSORPTION
            if (yellowEnd > redWidth) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, ABSORPTION_HEALTH_BAR,
                        x + redWidth, y, redWidth, 0, yellowEnd - redWidth, TEX_H, TEX_W, TEX_H);
            }

            // NUMBERS
            if (!SUPConfig.get().main.healthBar.healthNumber) return;

            Component text;

            if (absorption > 0) {
                text = Component.literal(String.valueOf((int) health + (int) absorption)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xD4AF37)))
                        .append(Component.literal("/" + (int) maxHealth).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF1313))));
            } else {
                text = Component.literal(String.valueOf((int) health)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF1313)))
                        .append(Component.literal("/" + (int) maxHealth).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF1313))));
            }

            int textX = x - mc.font.width(text) - 1;
            graphics.text(mc.font, text, textX, y + 1, 0xFFFF5555, true);
            }
        );
    }
}