package net.galgan.skyupplus.util;

import net.galgan.skyupplus.mixin.AbstractContainerScreenAccessor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

public class Inventory {
    public static Slot getSlotAt(AbstractContainerScreen<?> screen, double x, double y) {
        return ((AbstractContainerScreenAccessor) screen).invokeGetHoveredSlot(x, y);
    }
}
