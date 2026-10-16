package net.galgan.skyupplus.util;

import net.galgan.skyupplus.config.HudPosition;
import net.galgan.skyupplus.dungeons.DungeonMapHud;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.features.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public enum HudElement {
    ABILITIES("Abilities", 0xFF55FF55,
            () -> Abilities.body,
            () -> SUPConfig.get().abilities.abilitiesPosition,
            () -> SUPConfig.get().abilities.customAbilities,
            List.of(
                    Component.literal("Sieci rybackie: ").withStyle(ChatFormatting.GREEN).append(Component.literal("00:00").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Rozbiórka: ").withStyle(ChatFormatting.GREEN).append(Component.literal("00:00").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Pług: ").withStyle(ChatFormatting.GREEN).append(Component.literal("00:00").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Wiertło: ").withStyle(ChatFormatting.GREEN).append(Component.literal("00:00").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Piła: ").withStyle(ChatFormatting.GREEN).append(Component.literal("00:00").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Nawałnica: ").withStyle(ChatFormatting.GREEN).append(Component.literal("00:00").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Eskalacja: ").withStyle(ChatFormatting.GREEN).append(Component.literal("00:00").withStyle(ChatFormatting.WHITE))
            )),
    REMINDERS("Reminders", 0xFFAA00AA,
            () -> Reminders.body,
            () -> SUPConfig.get().reminders.remindersPosition,
            () -> true,
            List.of(
                    Component.literal("Zawody: ").withStyle(ChatFormatting.DARK_AQUA).append(Component.literal("Wtorek 18:00").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Eventy: ").withStyle(ChatFormatting.DARK_PURPLE).append(Component.literal("Wtorek 20:00").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Daily: ").withStyle(ChatFormatting.YELLOW).append(Component.literal("Dostępne").withStyle(ChatFormatting.WHITE))
            )),
    FISHING("Fishing", 0xFF00AAAA,
            () -> Fishing.body,
            () -> SUPConfig.get().fishing.fishingPosition,
            () -> true,
            List.of(
                    Component.literal("Niewielka: ").withStyle(ChatFormatting.GRAY).append(Component.literal("0").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Przeciętna: ").withStyle(ChatFormatting.YELLOW).append(Component.literal("0").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Wymiarowa ").withStyle(ChatFormatting.GREEN).append(Component.literal("0").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Ogromna: ").withStyle(ChatFormatting.GOLD).append(Component.literal("0").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Mamucia: ").withStyle(ChatFormatting.DARK_PURPLE).append(Component.literal("0").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Suma: ").withStyle(ChatFormatting.BLUE).append(Component.literal("0").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Zarobek: ").withStyle(ChatFormatting.AQUA).append(Component.literal("0.00 SC").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Waga: ").withStyle(ChatFormatting.LIGHT_PURPLE).append(Component.literal("0.00g").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Max: ").withStyle(ChatFormatting.DARK_GREEN).append(Component.literal("0.00g").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Min: ").withStyle(ChatFormatting.DARK_RED).append(Component.literal("0.00g").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Sashimi: ").withStyle(ChatFormatting.DARK_AQUA).append(Component.literal("0").withStyle(ChatFormatting.WHITE))
            )),
    REQUIREMENTS("Requirements", 0xFFFFAA00,
            () -> Requirements.body,
            () -> SUPConfig.get().main.requirementsPosition,
            () -> true,
            List.of(
                    Component.literal("Stój obok:").withStyle(ChatFormatting.AQUA),
                    Component.literal("Pochodnia: ").withStyle(ChatFormatting.GOLD).append(Component.literal("64").withStyle(ChatFormatting.YELLOW)),
                    Component.literal("Magiczna latarnia: ").withStyle(ChatFormatting.GOLD).append(Component.literal("8").withStyle(ChatFormatting.YELLOW)),
                    Component.literal("Latarnia morska: ").withStyle(ChatFormatting.GOLD).append(Component.literal("64").withStyle(ChatFormatting.YELLOW)),
                    Component.literal("Ogień: ").withStyle(ChatFormatting.GOLD).append(Component.literal("10").withStyle(ChatFormatting.YELLOW)),
                    Component.literal("Jasnogłaz: ").withStyle(ChatFormatting.GOLD).append(Component.literal("64").withStyle(ChatFormatting.YELLOW)),
                    Component.literal("Netherrack: ").withStyle(ChatFormatting.GOLD).append(Component.literal("10").withStyle(ChatFormatting.YELLOW))
            )),
    SCOREBOARD("Scoreboard", 0xFFFFFF55,
            () -> Scoreboard.body,
            () -> SUPConfig.get().scoreboard.scoreboardPosition,
            () -> SUPConfig.get().scoreboard.customScoreboard,
            List.of(
                    Component.literal("Portfel: ").withStyle(ChatFormatting.RED).append(Component.literal("0 SC").withStyle(ChatFormatting.YELLOW)),
                    Component.literal("Punkty: ").withStyle(ChatFormatting.RED).append(Component.literal("0 SP").withStyle(ChatFormatting.YELLOW)),
                    Component.literal("Tokeny: ").withStyle(ChatFormatting.RED).append(Component.literal("0 ST").withStyle(ChatFormatting.YELLOW)),
                    Component.literal("Bank: ").withStyle(ChatFormatting.AQUA).append(Component.literal("0 SC").withStyle(ChatFormatting.YELLOW)),
                    Component.literal("Poziom: ").withStyle(ChatFormatting.AQUA).append(Component.literal("0").withStyle(ChatFormatting.YELLOW)),
                    Component.literal("Limit: ").withStyle(ChatFormatting.GREEN).append(Component.literal("0/0").withStyle(ChatFormatting.YELLOW)),
                    Component.literal("Reset: ").withStyle(ChatFormatting.GREEN).append(Component.literal("0m 0s").withStyle(ChatFormatting.YELLOW)),
                    Component.literal("• ").withStyle(ChatFormatting.DARK_GRAY).append(Component.literal("✔").withStyle(ChatFormatting.GREEN)).append(Component.literal(" • ").withStyle(ChatFormatting.DARK_GRAY)).append(Component.literal("✔").withStyle(ChatFormatting.GREEN)).append(Component.literal(" • ").withStyle(ChatFormatting.DARK_GRAY)).append(Component.literal("✔").withStyle(ChatFormatting.GREEN)).append(Component.literal(" • ").withStyle(ChatFormatting.DARK_GRAY)).append(Component.literal("✔").withStyle(ChatFormatting.GREEN)).append(Component.literal(" • ").withStyle(ChatFormatting.DARK_GRAY)).append(Component.literal("✔").withStyle(ChatFormatting.GREEN)),
                    Component.literal("• ").withStyle(ChatFormatting.DARK_GRAY).append(Component.literal("✔").withStyle(ChatFormatting.GREEN)).append(Component.literal(" • ").withStyle(ChatFormatting.DARK_GRAY)).append(Component.literal("✔").withStyle(ChatFormatting.GREEN)).append(Component.literal(" • ").withStyle(ChatFormatting.DARK_GRAY)).append(Component.literal("✔").withStyle(ChatFormatting.GREEN)).append(Component.literal(" • ").withStyle(ChatFormatting.DARK_GRAY)).append(Component.literal("✔").withStyle(ChatFormatting.GREEN)).append(Component.literal(" • ").withStyle(ChatFormatting.DARK_GRAY)).append(Component.literal("✔").withStyle(ChatFormatting.GREEN))
            )),
    CRATES("Crates", 0xFF55FF55,
            () -> Crates.body,
            () -> SUPConfig.get().crates.cratesPosition,
            () -> false,
            List.of(
                    Component.empty().append(Component.literal("Elementium ").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)).append(Component.literal("x0").withStyle(ChatFormatting.WHITE)),
                    Component.empty().append(Component.literal("Mending ").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD)).append(Component.literal("x0").withStyle(ChatFormatting.WHITE)),
                    Component.empty().append(Component.literal("Blok szmaragdu ").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)).append(Component.literal("x0").withStyle(ChatFormatting.WHITE)),
                    Component.empty().append(Component.literal("Blok złota ").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)).append(Component.literal("x0").withStyle(ChatFormatting.WHITE)),
                    Component.empty().append(Component.literal("Blok żelaza ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xDCDCDC)).withBold(true))).append(Component.literal("x0").withStyle(ChatFormatting.WHITE)),
                    Component.empty().append(Component.literal("Blok redstone ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xB01905)).withBold(true))).append(Component.literal("x0").withStyle(ChatFormatting.WHITE)),
                    Component.empty().append(Component.literal("Ziemia ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x866043)).withBold(true))).append(Component.literal("x0").withStyle(ChatFormatting.WHITE)),
                    Component.empty().append(Component.literal("Blok trawy ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x2FA81F)).withBold(true))).append(Component.literal("x0").withStyle(ChatFormatting.WHITE)),
                    Component.empty().append(Component.literal("Piasek ").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)).append(Component.literal("x0").withStyle(ChatFormatting.WHITE))
            )),
    SKILLS("XP Counter", 0xFF55FFFF,
            () -> XPCounter.body,
            () -> SUPConfig.get().xpcounter.skillsPosition,
            () -> true,
            List.of(
                    Component.literal("Praca: ").withStyle(ChatFormatting.AQUA).append(Component.literal("Górnik").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Następny poziom: ").withStyle(ChatFormatting.AQUA).append(Component.literal("0").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Pozostałe xp: ").withStyle(ChatFormatting.AQUA).append(Component.literal("0").withStyle(ChatFormatting.WHITE)),
                    Component.literal("xp/min: ").withStyle(ChatFormatting.AQUA).append(Component.literal("0").withStyle(ChatFormatting.WHITE)),
                    Component.literal("Przewidywany czas: ").withStyle(ChatFormatting.AQUA).append(Component.literal("0h 0m").withStyle(ChatFormatting.WHITE))
            )),
    DUNGEON_MAP("Dungeon Map", 0xFFAA0000,
            () -> SUPConfig.get().dungeons.dungeonMap.dungeonMapPosition,
            () -> SUPConfig.get().dungeons.dungeonMap.dungeonMapToggle,
            new Custom(DungeonMapHud::boxWidth, DungeonMapHud::boxHeight, DungeonMapHud::drawPreview));

    public record Custom(IntSupplier width, IntSupplier height, Consumer<GuiGraphicsExtractor> preview) {}

    public final String name;
    public final int color;
    private final Supplier<List<Component>> body;
    private final Supplier<HudPosition> position;
    private final BooleanSupplier enabled;
    private final List<Component> dummy;
    private final Custom custom; // null for text elements

    HudElement(String name, int color, Supplier<List<Component>> body, Supplier<HudPosition> position, BooleanSupplier enabled, List<Component> dummy) {
        this.name = name;
        this.color = color;
        this.body = body;
        this.position = position;
        this.enabled = enabled;
        this.dummy = dummy;
        this.custom = null;
    }

    HudElement(String name, int color, Supplier<HudPosition> position, BooleanSupplier enabled, Custom custom) {
        this.name = name;
        this.color = color;
        this.body = List::of;
        this.position = position;
        this.enabled = enabled;
        this.dummy = List.of();
        this.custom = custom;
    }

    public List<Component> body() {
        return body.get();
    }

    public HudPosition position() {
        return position.get();
    }

    public boolean isVisible() {
        if (custom != null) return enabled.getAsBoolean();
        return enabled.getAsBoolean() && !body().isEmpty();
    }

    public int previewHeight(Font font) {
        return custom != null ? custom.height().getAsInt() : DrawUtil.boxHeight(font, previewBody());
    }

    public void drawPreview(GuiGraphicsExtractor graphics) {
        if (custom != null) {
            custom.preview().accept(graphics);
        } else {
            DrawUtil.drawWidget(graphics, color, position(), previewBody());
        }
    }

    public List<Component> previewBody() {
        List<Component> lines = body();
        return lines.isEmpty() ? dummy : lines;
    }

    public static void renderAll(GuiGraphicsExtractor graphics) {
        for (HudElement element : values()) {
            if (element.custom != null) continue;
            if (element.isVisible()) {
                DrawUtil.drawWidget(graphics, element.color, element.position(), element.body());
            }
        }
    }
}