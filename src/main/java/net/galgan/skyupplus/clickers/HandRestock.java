package net.galgan.skyupplus.clickers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.galgan.skyupplus.config.SUPConfig;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

public class HandRestock {
    private static final int OFFHAND_INV_SLOT = 40;
    private static final int OFFHAND_MENU_SLOT = 45;

    private static ItemStack lastMain = ItemStack.EMPTY;
    private static ItemStack lastOff = ItemStack.EMPTY;
    private static int lastSelected = -1;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            LocalPlayer player = mc.player;
            MultiPlayerGameMode gameMode = mc.gameMode;
            if (player == null || gameMode == null || mc.gui.screen() != null || !SUPConfig.get().automation.handRestockToggle) {
                resetTracking();
                return;
            }

            GameType mode = gameMode.getPlayerMode();
            if (mode != GameType.SURVIVAL && mode != GameType.ADVENTURE) {
                resetTracking();
                return;
            }

            Inventory inv = player.getInventory();
            int selected = inv.getSelectedSlot();
            ItemStack main = inv.getItem(selected);
            ItemStack off = inv.getItem(OFFHAND_INV_SLOT);

            boolean sameSlot = selected == lastSelected;
            boolean mainChanged = sameSlot && !ItemStack.matches(lastMain, main);
            boolean offChanged = !ItemStack.matches(lastOff, off);

            boolean swapped = mainChanged && offChanged;

            boolean mainUsed = sameSlot && !swapped && wasUsed(lastMain, main);
            boolean offUsed = !swapped && wasUsed(lastOff, off);

            lastMain = main.copy();
            lastOff = off.copy();
            lastSelected = selected;

            if (mainUsed && tryToRestock(gameMode, player, selected, 36 + selected)) return;
            if (offUsed) tryToRestock(gameMode, player, OFFHAND_INV_SLOT, OFFHAND_MENU_SLOT);
        });
    }

    private static boolean wasUsed(ItemStack before, ItemStack now) {
        return !before.isEmpty() && ItemStack.isSameItemSameComponents(before, now) && now.getCount() < before.getCount();
    }

    private static void resetTracking() {
        lastMain = ItemStack.EMPTY;
        lastOff = ItemStack.EMPTY;
        lastSelected = -1;
    }

    private static boolean tryToRestock(MultiPlayerGameMode gameMode, LocalPlayer player, int handInvSlot, int handMenuSlot) {
        Inventory inv = player.getInventory();
        ItemStack held = inv.getItem(handInvSlot);

        int max = held.getMaxStackSize();
        if (held.isEmpty() || max <= 1 || held.getCount() >= max / 2) return false;

        int selected = inv.getSelectedSlot();

        // Main inventory first (9-35), then the hotbar (0-8)
        for (int i = 9; i < 36; i++) {
            if (ItemStack.isSameItemSameComponents(held, inv.getItem(i))) {
                pullOut(gameMode, player, i, handMenuSlot);
                return true;
            }
        }
        for (int i = 0; i < 9; i++) {
            if (i == handInvSlot || i == selected) continue; // never take from the hand being refilled or the main hand
            if (ItemStack.isSameItemSameComponents(held, inv.getItem(i))) {
                pullOut(gameMode, player, 36 + i, handMenuSlot);
                return true;
            }
        }
        return false;
    }

    private static void pullOut(MultiPlayerGameMode gameMode, LocalPlayer player, int fromMenuSlot, int handMenuSlot) {
        int containerId = player.inventoryMenu.containerId;

        gameMode.handleContainerInput(containerId, fromMenuSlot, 0, ContainerInput.PICKUP, player);
        gameMode.handleContainerInput(containerId, handMenuSlot, 0, ContainerInput.PICKUP, player);

        if (!player.inventoryMenu.getCarried().isEmpty()) {
            gameMode.handleContainerInput(containerId, fromMenuSlot, 0, ContainerInput.PICKUP, player);
        }
    }
}