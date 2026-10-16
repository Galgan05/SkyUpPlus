package net.galgan.skyupplus.config;

import com.google.gson.annotations.Expose;

public class HudPosition {

    public static final int MARGIN = 2;

    @Expose public boolean rightSide;
    @Expose public float y;

    public HudPosition(boolean rightSide, float y) {
        this.rightSide = rightSide;
        this.y = y;
    }

    public int x(int screenWidth, int boxWidth) {
        return rightSide ? screenWidth - boxWidth - MARGIN : MARGIN;
    }

    public int y(int screenHeight, int boxHeight) {
        return MARGIN + Math.round(y * range(screenHeight, boxHeight));
    }

    public static int range(int screenHeight, int boxHeight) {
        return Math.max(screenHeight - boxHeight - MARGIN * 2, 0);
    }
}