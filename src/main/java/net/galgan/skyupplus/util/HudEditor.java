package net.galgan.skyupplus.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.galgan.skyupplus.config.HudPosition;
import net.galgan.skyupplus.config.SUPConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class HudEditor extends Screen {
    private static final int SNAP_DISTANCE = 6;

    private final HudElement element;
    private final Screen parent;
    private boolean dragging = false;

    private HudEditor(HudElement element, Screen parent) {
        super(Component.literal("Move " + element.name));
        this.element = element;
        this.parent = parent;
    }

    public HudElement element() {
        return element;
    }

    public static void open(HudElement element) {
        Minecraft mc = Minecraft.getInstance();
        mc.gui.setScreen(new HudEditor(element, mc.gui.screen()));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        int w = graphics.guiWidth();
        int h = graphics.guiHeight();

        graphics.horizontalLine(0, w, h / 2, 0x30FFFFFF);
        graphics.verticalLine(w / 2, 0, h, 0x30FFFFFF);

        Component hint = Component.literal("Przytrzymaj LPM Aby Przesunąć • Naciśnij ESC Aby Zapisać");
        graphics.text(font, hint, (w - font.width(hint)) / 2, h / 2 - font.lineHeight - 4, 0xFFFFFFFF);

        element.drawPreview(graphics);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (click.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            dragging = true;
            moveTo(click.x(), click.y());
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
        if (dragging) {
            moveTo(click.x(), click.y());
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        if (click.button() == InputConstants.MOUSE_BUTTON_LEFT) dragging = false;
        return super.mouseReleased(click);
    }

    private void moveTo(double mouseX, double mouseY) {
        HudPosition position = element.position();
        int boxHeight = element.previewHeight(font);

        position.rightSide = mouseX > width / 2.0;

        int range = HudPosition.range(height, boxHeight);
        if (range == 0) {
            position.y = 0;
            return;
        }

        double top = mouseY - boxHeight / 2.0 - HudPosition.MARGIN;
        float fraction = (float) Math.clamp(top / range, 0.0, 1.0);

        for (float snap : new float[]{0f, 0.5f, 1f}) {
            if (Math.abs(fraction - snap) * range < SNAP_DISTANCE) {
                fraction = snap;
            }
        }
        position.y = fraction;
    }

    @Override
    public void onClose() {
        SUPConfig.save();
        Minecraft.getInstance().gui.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}