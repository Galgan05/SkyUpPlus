package net.galgan.skyupplus.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.galgan.skyupplus.SkyUpPlusClient;
import net.galgan.skyupplus.config.SUPConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class Keybinds {

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(SkyUpPlusClient.MOD_ID, "main")
    );

    public static KeyMapping LEFT_CLICKER;
    public static KeyMapping LEFT_HOLD;
    public static KeyMapping RIGHT_CLICKER;
    public static KeyMapping RIGHT_HOLD;
    public static KeyMapping MOUSE_LOCK;
    public static KeyMapping HAND_RESTOCK;

    public static void register() {
        LEFT_CLICKER = create("left_clicker", InputConstants.UNKNOWN.getValue());
        LEFT_HOLD = create("left_hold", InputConstants.UNKNOWN.getValue());
        RIGHT_CLICKER = create("right_clicker", InputConstants.UNKNOWN.getValue());
        RIGHT_HOLD = create("right_hold", InputConstants.UNKNOWN.getValue());
        MOUSE_LOCK = create("mouse_lock", InputConstants.UNKNOWN.getValue());
        HAND_RESTOCK = create("hand_restock", InputConstants.UNKNOWN.getValue());

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null) return;

            var automation = SUPConfig.get().automation;
            boolean changed = false;

            while (LEFT_CLICKER.consumeClick()) {
                automation.leftClickerToggle = !automation.leftClickerToggle;
                showToggle(mc, "Clicker LPM", automation.leftClickerToggle);
                changed = true;
            }
            while (LEFT_HOLD.consumeClick()) {
                automation.leftHoldToggle = !automation.leftHoldToggle;
                showToggle(mc, "Trzymaj LPM", automation.leftHoldToggle);
                changed = true;
            }
            while (RIGHT_CLICKER.consumeClick()) {
                automation.rightClickerToggle = !automation.rightClickerToggle;
                showToggle(mc, "Clicker PPM", automation.rightClickerToggle);
                changed = true;
            }
            while (RIGHT_HOLD.consumeClick()) {
                automation.rightHoldToggle = !automation.rightHoldToggle;
                showToggle(mc, "Trzymaj PPM", automation.rightHoldToggle);
                changed = true;
            }
            while (MOUSE_LOCK.consumeClick()) {
                automation.mouseLockToggle = !automation.mouseLockToggle;
                showToggle(mc, "Blokowanie Myszki", automation.mouseLockToggle);
                changed = true;
            }
            while (HAND_RESTOCK.consumeClick()) {
                automation.handRestockToggle = !automation.handRestockToggle;
                showToggle(mc, "Uzupełnianie Łapki", automation.handRestockToggle);
                changed = true;
            }

            if (changed) SUPConfig.save();
        });
    }

    private static KeyMapping create(String name, int defaultKey) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key." + SkyUpPlusClient.MOD_ID + "." + name,
                InputConstants.Type.KEYBOARD,
                defaultKey,
                CATEGORY
        ));
    }

    private static void showToggle(Minecraft mc, String name, boolean enabled) {
        mc.player.sendOverlayMessage(sendWithLogo(
                Component.literal(name).withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED)));
    }

    private static Component sendWithLogo(Component message) {
        return Component.empty()
                .append(Component.literal("Sky").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD))
                .append(Component.literal("UP").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD))
                .append(Component.literal("+ ").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD))
                .append(Component.literal("» ").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD))
                .append(message);
    }
}