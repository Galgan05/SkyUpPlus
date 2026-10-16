package net.galgan.skyupplus.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;

public final class Chat {

    public static void send(Component text) {
        Minecraft mc = Minecraft.getInstance();

        Runnable r = () -> {
            ChatComponent chat = mc.gui.hud.getChat();
            chat.addClientSystemMessage(Component.empty()
                .append(Component.literal("Sky").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD))
                .append(Component.literal("UP").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD))
                .append(Component.literal("+ ").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD))
                .append(Component.literal("» ").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD))
                .append(text));
        };

        if (mc.isSameThread()) r.run(); else mc.execute(r);
    }

    public static void sendString(String string) {
        send(Component.literal(string));
    }
}