package net.galgan.skyupplus.clickers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.component.ItemLore;

import java.util.Set;

import static net.galgan.skyupplus.util.Inventory.getSlotAt;

public class SellClicker {
    private static AbstractContainerScreen<?> clickScreen;
    private static Slot slot = null;
    private static boolean shouldClick = false;
    private static int tickCounter = 0;

    public static void register() {

        ScreenEvents.BEFORE_INIT.register((mc, screen, w, h) -> {
            if(!ServerRestrictor.isAllowed()) return;

            if (!(screen instanceof AbstractContainerScreen<?> handled)) {
                clearVariables();
                return;
            }
            if (!SHOP_TITLES.contains(handled.getTitle().getString())) {
                clearVariables();
                return;
            }

            ScreenMouseEvents.beforeMouseClick(screen).register((s, click) -> {

                if (click.button()!= 2) return;

                if(shouldClick) {
                    clearVariables();
                    return;
                }

                slot = getSlotAt(handled, click.x(), click.y());

                if (slot == null) {
                    clearVariables();
                    return;
                }

                ItemLore lore = slot.getItem().get(DataComponents.LORE);

                if (lore == null) {
                    clearVariables();
                    return;
                }

                boolean validItem = false;

                for(Component line : lore.lines()) {
                    if (line.getString().startsWith("▪ PPM")) {
                        validItem = true;
                        break;
                    }
                }

                if (validItem) {
                    shouldClick = true;
                    tickCounter = 0;
                    clickScreen = handled;
                } else {
                    clearVariables();
                }
            });

        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if(!ServerRestrictor.isAllowed()) return;

            if (message.getString().startsWith("Sklep » Nie masz wystarczająco") || message.getString().startsWith("Sklep » Sprzedano 0")) {
                clearVariables();
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (!shouldClick) return;

            if (mc.player == null || mc.gameMode == null || !ServerRestrictor.isAllowed() || slot == null || mc.gui.screen() != clickScreen) {
                clearVariables();
                return;
            }

            tickCounter++;
            if (tickCounter % 3 != 0) return;

            mc.gameMode.handleContainerInput(
                    clickScreen.getMenu().containerId,
                    slot.index,
                    1,
                    ContainerInput.PICKUP,
                    mc.player
            );
        });
    }

    private static void clearVariables() {
        shouldClick = false;
        slot = null;
        clickScreen = null;
        tickCounter = 0;
    }

    private static final Set<String> SHOP_TITLES = Set.of(
            "\uE004\uE200\uE002Sklepy",
            "\uE001\uE190\uE002Sklep podstawowy",
            "\uE001\uE191\uE002Sklep budowniczego",
            "\uE001\uE192\uE002Sklep drwala",
            "\uE001\uE19A\uE002Sklep górnika",
            "\uE001\uE193\uE002Sklep jubilera",
            "\uE001\uE198\uE002Sklep hodowcy",
            "\uE001\uE195\uE002Sklep łowcy",
            "\uE001\uE196\uE002Sklep farmera",
            "\uE001\uE199\uE002Sklep rybaka",
            "\uE001\uE194\uE002Sklep szamanki",
            "\uE001\uE202\uE002Dodatkowe opcje"
    );
}
