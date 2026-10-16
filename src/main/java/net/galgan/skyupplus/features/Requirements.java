package net.galgan.skyupplus.features;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.galgan.skyupplus.config.SUPData;
import net.galgan.skyupplus.mixin.AbstractContainerScreenAccessor;
import net.galgan.skyupplus.util.Chat;
import net.galgan.skyupplus.util.Inventory;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.inventory.Slot;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Requirements {
    public static Component name;
    public static List<Component> filteredDescription;
    public static boolean isDisplayed;
    public static List<Component> body = new ArrayList<>();

    public static void register() {

        ScreenEvents.BEFORE_INIT.register((mc, screen, w, h) -> {
            if(!ServerRestrictor.isAllowed()) return;

            if (!(screen instanceof AbstractContainerScreen<?> handled)) return;
            if (!CONTAINER_TITLES.contains(handled.getTitle().getString())) return;

            ScreenMouseEvents.beforeMouseClick(screen).register((s, click) -> {

                if (click.button()!= 2) return;

                Slot slot = Inventory.getSlotAt(handled, click.x(), click.y());

                if (slot == null) {
                    nullVariables();
                    return;
                }

                ItemLore lore = slot.getItem().get(DataComponents.LORE);

                if (lore == null) {
                    nullVariables();
                    return;
                }

                name = slot.getItem().getHoverName();

                for(Component line : lore.lines()) {
                    if (line.getString().startsWith("▪ Zadanie ukończone!") && !name.getString().equals("» Zadanie codzienne «")) {
                        nullVariables();
                        return;
                    }
                }

                if (!name.getString().startsWith("» ") || DISALLOWED_NAMES.contains(name.getString())) {
                    nullVariables();
                    return;
                }

                isDisplayed = true;

                if (name.getString().equals("» Zadanie codzienne «")) {
                    SUPData.get().hasSeenDaily = true;
                    SUPData.save();
                }

                Chat.send(Component.empty().append(Component.literal("Wybrano: ").withStyle(ChatFormatting.DARK_AQUA)).append(name));

                filteredDescription = loreFilter(lore);
                body = getBody();
            });
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if(!ServerRestrictor.isAllowed()) return;

            if (!overlay && message.getString().startsWith("Wyzwania » Ukończono zadanie")) {
                isDisplayed = false;
                body = new ArrayList<>();

                if (message.getString().contains("Zadanie codzienne")) {
                    SUPData.get().hasSeenDaily = true;
                    SUPData.save();
                }
            }
        });
    }

    private static void nullVariables() {
        name = null;
        filteredDescription = null;
        body = new ArrayList<>();
        isDisplayed = false;
    }

    public static List<Component> loreFilter(ItemLore lore) {
        List<Component> filtered = new ArrayList<>();
        boolean addNext = false;

        for (Component line : lore.lines()) {
            String s = line.getString().trim();

            if (!s.isEmpty()) {
                if (s.startsWith("▪")) {
                    if (SKIPPED_CATEGORIES.stream().anyMatch(s::startsWith)) {
                        addNext = false;
                    } else {
                        filtered.add(line);
                        addNext = true;
                    }
                } else {
                    if (addNext) filtered.add(line);
                }
            }
        }
        return filtered;
    }

    private static List<Component> getBody() {
        List<Component> bodyText = new ArrayList<>();

        if (!isDisplayed) return bodyText;

        for (Component line : filteredDescription) {
            List<Component> siblings = line.getSiblings();

            if (siblings.isEmpty()) {
                bodyText.add(line);
                continue;
            }

            // Skip the "▪" bullet, or the prefix + "»" pair, or just the first sibling
            int startIndex = 1;
            if (!siblings.get(0).getString().trim().startsWith("▪")
                    && siblings.size() > 1
                    && siblings.get(1).getString().trim().startsWith("»")) {
                startIndex = 2;
            }

            MutableComponent result = Component.empty();
            for (int i = startIndex; i < siblings.size(); i++) {
                result.append(siblings.get(i).copy());
            }
            bodyText.add(result);
        }

        return bodyText;
    }

    public static final Set<String> DISALLOWED_NAMES = Set.of(
            "» Informacje «",
            "» Wróć «",
            "» Wyjście «",
            "» Nowicjusz «",
            "» Wtajemniczony «",
            "» Zaawansowany «",
            "» Znawca «",
            "» Ekspert «",
            "» Mistrz «",
            "» Guru «",
            "» Legenda «",
            "» Wirtuoz «",
            "» Bonus «"
    );

    public static final Set<String> SKIPPED_CATEGORIES = Set.of(
            "▪ Opis",
            "▪ Zalecane«",
            "▪ Za ukończenie",
            "▪ LPM",
            "▪ Uwaga",
            "▪ Kategoria",
            "▪ Ukończone",
            "▪ Następny"
    );

    private static final Set<String> CONTAINER_TITLES = Set.of(
            "\uE003\uE150\uE002Wyzwania",
            "\uE001\uE151\uE002Wyzwania » Nowicjusz",
            "\uE001\uE152\uE002Wyzwania » Wtajemniczony",
            "\uE001\uE153\uE002Wyzwania » Zaawansowany",
            "\uE001\uE154\uE002Wyzwania » Znawca",
            "\uE001\uE155\uE002Wyzwania » Ekspert",
            "\uE001\uE156\uE002Wyzwania » Mistrz",
            "\uE001\uE157\uE002Wyzwania » Guru",
            "\uE001\uE158\uE002Wyzwania » Legenda",
            "\uE001\uE159\uE002Wyzwania » Wirtuoz",
            "\uE001\uE15A\uE002Wyzwania » Bonus",
            "\uE001\uE039\uE002          Szamaństwo"
    );
}

