package net.galgan.skyupplus.util;

import net.galgan.skyupplus.config.HudPosition;
import net.galgan.skyupplus.config.SUPConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.List;

public class DrawUtil {

    public static float scaleFactor() {
        return SUPConfig.get().main.hudScale / (float) Minecraft.getInstance().getWindow().getGuiScale();
    }

    public static int getHeight(Font font, List<Component> body) {

        return (font.lineHeight + 2) * body.size();
    }

    public static int getWidth(Font font, List<Component> body) {

        int width = 0;

        for(Component line : body) {
            if (font.width(line) > width) {
                width = font.width(line);
            }
        }
        return width;
    }

    /** Full width of a widget including padding, border and shadow. */
    public static int boxWidth(Font font, List<Component> body) {
        return getWidth(font, body) + 6;
    }

    /** Full height of a widget including padding, border and shadow. */
    public static int boxHeight(Font font, List<Component> body) {
        return getHeight(font, body) + 4;
    }

    public static void background(GuiGraphicsExtractor graphics, int x, int y, int w, int h) {

        //Background
        graphics.fill(
                x + 1,
                y + 1,
                x + w + 4,
                y + h + 2,
                0x80000000
        );
    }

    public static void border(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int color) {

        //Shadow
        graphics.horizontalLine(x + 1, x + w + 5, y + 1, (color & 0xFCFCFC) >> 2 | (color & 0xFF000000));
        graphics.horizontalLine(x + 1, x + w + 5, y + h + 3, (color & 0xFCFCFC) >> 2 | (color & 0xFF000000));
        graphics.verticalLine(x + 1, y + 1, y + h + 3, (color & 0xFCFCFC) >> 2 | (color & 0xFF000000));
        graphics.verticalLine(x + w + 5, y + 1, y + h + 3, (color & 0xFCFCFC) >> 2 | (color & 0xFF000000));

        //Main
        graphics.horizontalLine(x, x + w + 4, y, color);
        graphics.horizontalLine(x, x + w + 4, y + h +2, color);
        graphics.verticalLine(x, y, y + h + 2, color);
        graphics.verticalLine(x + w + 4, y, y + h + 2, color);
    }

    public static void text(GuiGraphicsExtractor graphics, Font font, List<Component> body, int x, int y) {

        //Move the text to add space
        x+=3;
        y+=3;

        //Draw the text
        for(Component line : body) {
            graphics.text(font, line, x, y, 0xFFFFFFFF);
            y += font.lineHeight + 2;
        }
    }

    public static void drawWidget(GuiGraphicsExtractor graphics, int color, HudPosition position, List<Component> body) {
        Font font = Minecraft.getInstance().font;
        float scale = scaleFactor();

        //Get the dimensions of the text
        int w = getWidth(font, body);
        int h = getHeight(font, body);

        //Screen size in HUD units
        int screenWidth = (int) (graphics.guiWidth() / scale);
        int screenHeight = (int) (graphics.guiHeight() / scale);

        //Get the starting position
        int x = position.x(screenWidth, boxWidth(font, body));
        int y = position.y(screenHeight, boxHeight(font, body));

        //Draw the border and text at the HUD scale
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);

        background(graphics, x, y, w, h);
        border(graphics, x, y, w, h, color);
        text(graphics, font, body, x, y);

        graphics.pose().popMatrix();
    }
}