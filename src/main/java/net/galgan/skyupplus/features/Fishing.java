package net.galgan.skyupplus.features;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.config.SUPData;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class Fishing {
    public static boolean isFishing;
    public static List<Component> body = new ArrayList<>();

    public static void register() {

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if(!ServerRestrictor.isAllowed() || mc.player == null) return;

            ItemStack mainHand = mc.player.getMainHandItem();
            ItemStack offHand = mc.player.getOffhandItem();

            isFishing = mainHand.is(Items.FISHING_ROD) || offHand.is(Items.FISHING_ROD);

            body = getBody();
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!ServerRestrictor.isAllowed()) return;

            var savedData = SUPData.get();

            String msgString = message.getString();

            if (!overlay && (msgString.startsWith("Wędkarstwo » ") || msgString.startsWith("Enchanty » "))) {

                if (msgString.startsWith("Wędkarstwo » Wyłowiono: §7Niew")) {
                    savedData.niewielkaCount++;
                }
                if (msgString.startsWith("Wędkarstwo » Wyłowiono: §ePrze")) {
                    savedData.przecietnaCount++;
                }
                if (msgString.startsWith("Wędkarstwo » Wyłowiono: §aWymi")) {
                    savedData.wymiarowaCount++;
                }
                if (msgString.startsWith("Wędkarstwo » Wyłowiono: §6Ogro")) {
                    savedData.ogromnaCount++;
                }
                if (msgString.startsWith("Wędkarstwo » Wyłowiono: §5Mamu")) {
                    savedData.mamuciaCount++;
                }
                if (msgString.startsWith("Enchanty » Oprócz ryby")) {
                    savedData.sashimiCount++;
                }
                if (msgString.startsWith("Wędkarstwo » Sprz")) {
                    String price = msgString.split("za ")[1].split(" SC")[0];
                    price = price.replace(",", ".");
                    price = price.replace(" ", "");
                    double earned = Double.parseDouble(price);

                    savedData.totalEarned += earned;
                }
                if (msgString.startsWith("Wędkarstwo » Wyłowiono:")) {
                    String mass = msgString.split(" \\(")[1].split("g, ")[0];
                    mass = mass.replace(",", ".");
                    mass = mass.replace(" ", "");
                    double weight = Double.parseDouble(mass);

                    savedData.totalWeight += weight;

                    if (weight > savedData.biggestWeight || savedData.biggestWeight == 0) savedData.biggestWeight = weight;
                    if (weight < savedData.smallestWeight || savedData.smallestWeight == 0) savedData.smallestWeight = weight;
                }

                savedData.totalCount = savedData.niewielkaCount + savedData.przecietnaCount + savedData.wymiarowaCount
                        + savedData.ogromnaCount + savedData.mamuciaCount;

                SUPData.save();
            }
        });
    }

    public static List<Component> getBody() {
        List<Component> bodyText = new ArrayList<>();

        if (SUPConfig.get().fishing.fishingDisplayBehavior == SUPConfig.FishingDisplayBehavior.ALWAYS || (Fishing.isFishing && SUPConfig.get().fishing.fishingDisplayBehavior == SUPConfig.FishingDisplayBehavior.FISHING)) {

            if (SUPConfig.get().fishing.fish.toggleNiewielka) {
                bodyText.add(Component.empty()
                        .append(Component.literal("Niewielka: ").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(String.valueOf(SUPData.get().niewielkaCount)).withStyle(ChatFormatting.WHITE)));
            }
            if (SUPConfig.get().fishing.fish.togglePrzecietna) {
                bodyText.add(Component.empty()
                        .append(Component.literal("Przeciętna: ").withStyle(ChatFormatting.YELLOW))
                        .append(Component.literal(String.valueOf(SUPData.get().przecietnaCount)).withStyle(ChatFormatting.WHITE)));
            }
            if (SUPConfig.get().fishing.fish.toggleWymiarowa) {
                bodyText.add(Component.empty()
                        .append(Component.literal("Wymiarowa: ").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(String.valueOf(SUPData.get().wymiarowaCount)).withStyle(ChatFormatting.WHITE)));
            }
            if (SUPConfig.get().fishing.fish.toggleOgromna) {
                bodyText.add(Component.empty()
                        .append(Component.literal("Ogromna: ").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(String.valueOf(SUPData.get().ogromnaCount)).withStyle(ChatFormatting.WHITE)));
            }
            if (SUPConfig.get().fishing.fish.toggleMamucia) {
                bodyText.add(Component.empty()
                        .append(Component.literal("Mamucia: ").withStyle(ChatFormatting.DARK_PURPLE))
                        .append(Component.literal(String.valueOf(SUPData.get().mamuciaCount)).withStyle(ChatFormatting.WHITE)));
            }
            if (SUPConfig.get().fishing.fish.toggleSuma) {
                bodyText.add(Component.empty()
                        .append(Component.literal("Suma: ").withStyle(ChatFormatting.BLUE))
                        .append(Component.literal(String.valueOf(SUPData.get().totalCount)).withStyle(ChatFormatting.WHITE)));
            }
            if (SUPConfig.get().fishing.fish.toggleZarobek) {
                bodyText.add(Component.empty()
                        .append(Component.literal("Zarobek: ").withStyle(ChatFormatting.AQUA))
                        .append(Component.literal(String.format("%.2f", SUPData.get().totalEarned)).append(" SC").withStyle(ChatFormatting.WHITE)));
            }
            if (SUPConfig.get().fishing.fish.toggleWaga) {
                if (SUPData.get().totalWeight < 1000) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Waga: ").withStyle(ChatFormatting.LIGHT_PURPLE))
                            .append(Component.literal(String.format("%.2f", SUPData.get().totalWeight)).append("g").withStyle(ChatFormatting.WHITE)));
                } else if (SUPData.get().totalWeight < 1000000) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Waga: ").withStyle(ChatFormatting.LIGHT_PURPLE))
                            .append(Component.literal(String.format("%.2f", SUPData.get().totalWeight/1000)).append("kg").withStyle(ChatFormatting.WHITE)));
                } else if (SUPData.get().totalWeight >= 1000000) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Waga: ").withStyle(ChatFormatting.LIGHT_PURPLE))
                            .append(Component.literal(String.format("%.3f", SUPData.get().totalWeight/1000000)).append("t").withStyle(ChatFormatting.WHITE)));
                }
            }
            if (SUPConfig.get().fishing.fish.toggleNajwieksza) {
                bodyText.add(Component.empty()
                        .append(Component.literal("Max: ").withStyle(ChatFormatting.DARK_GREEN))
                        .append(Component.literal(String.format("%.2f", SUPData.get().biggestWeight)).append("g").withStyle(ChatFormatting.WHITE)));
            }
            if (SUPConfig.get().fishing.fish.toggleNajmniejsza) {
                bodyText.add(Component.empty()
                        .append(Component.literal("Min: ").withStyle(ChatFormatting.DARK_RED))
                        .append(Component.literal(String.format("%.2f", SUPData.get().smallestWeight)).append("g").withStyle(ChatFormatting.WHITE)));
            }
            if (SUPConfig.get().fishing.fish.toggleSashimi) {
                bodyText.add(Component.empty()
                        .append(Component.literal("Sashimi: ").withStyle(ChatFormatting.DARK_AQUA))
                        .append(Component.literal(String.valueOf(SUPData.get().sashimiCount)).withStyle(ChatFormatting.WHITE)));
            }
        }

        return new ArrayList<>(bodyText);
    }
}