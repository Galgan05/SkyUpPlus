package net.galgan.skyupplus.features;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.galgan.skyupplus.config.HudPosition;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.config.SUPData;
import net.galgan.skyupplus.util.DrawUtil;
import net.galgan.skyupplus.util.HudElement;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class Crates {

    private static String playerName;
    public static List<Component> body = new ArrayList<>();
    public static int color = 0xFFFFFFFF;
    
    public static void register() {

        ScreenEvents.BEFORE_INIT.register((mc, screen, w, h) -> {
            if(!ServerRestrictor.isAllowed()) return;

            var elementium = SUPConfig.get().crates.elementium;
            var platinum = SUPConfig.get().crates.platinum;
            var other = SUPConfig.get().crates.other;
            var savedData = SUPData.get();

            List<Component> bodyText = new ArrayList<>();

            if (!(screen instanceof AbstractContainerScreen<?> handled)) {
                body = bodyText;
                color = 0xFFFFFFFF;
                return;
            }

            String title = handled.getTitle().getString();

            //ELEMENTIUM
            if (title.contains("\uE001\uE080\uE002") || title.contains("\uE001\uE170\uE002")) {
                color = 0xFF55FFFF;

                if (elementium.karambitToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Karambit ").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.karambitDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.perunToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Perun ").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.perunDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.cymofanToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Cymofan ").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.cymofanDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.mlotThoraToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Młot Thora ").withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.mlotThoraDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.urizelToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Urizel ").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.urizelDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.azadaToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Azada ").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.azadaDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.spinelToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Spinel ").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.spinelDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.karpiolapToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Karpiołap ").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.karpiolapDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.ethericaToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Etherica ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xA956FC)).withBold(true)))
                            .append(Component.literal("x" + savedData.ethericaDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.lukLegolasaToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Łuk Legolasa ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x56FCA9)).withBold(true)))
                            .append(Component.literal("x" + savedData.lukLegolasaDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.arbaletToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Arbalet ").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.arbaletDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.powrotOdysaToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Powrót Odysa ").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.powrotOdysaDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.cassisToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Cassis ").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.cassisDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.cuirassToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Cuirass ").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.cuirassDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.cuissotToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Cuissot ").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.cuissotDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.cossetToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Cosset ").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.cossetDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.maskaGlebinowcaToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Kaptur sztormowy ").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.kapturSztormowyDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.plaszczLesnikaToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Kamizelka biwakowa ").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.kamizelkaBiwakowaDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.nogawiceStrazaka) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Nogawice ognioodporne ").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.nogawiceOgnioodporneDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.kapcieLotnikaToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Trapery terenowe ").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.traperyTerenoweDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.rivendellToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Rivendell ").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.rivendellDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.impetToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Impet ").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.impetDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.phloxToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Phlox ").withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.phloxDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.majsterToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Majster ").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.majsterDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.magiczneWiaderkoToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Magiczne wiaderko ").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.magiczneWiaderkoDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.statTrackerToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("StatTracker ").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.statTrackerDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.jajkoNiespodziankaToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Jajko niespodzianka ").withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.jajkoNiespodziankaDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.kontrolerMagazynowToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Kontroler magazynów ").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.kontrolerMagazynowDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.kapeluszPracusiaToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Kapelusz pracusia ").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.kapeluszPracusiaDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.tajemniceSkyUPaToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Tajemnice SkyUPa v.5").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.tajemniceSkyUPaDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (elementium.klejnotKupieckiToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Klejnot kupiecki ").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.klejnotKupieckiDropped).withStyle(ChatFormatting.WHITE)));
                }
                body = bodyText;

            //PLATINUM
            } else if (title.contains("\uE001\uE081\uE002") || title.contains("\uE001\uE171\uE002")) {
                color = 0xFF55FF55;

                if (platinum.elementiumToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Elementium ").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.elementiumDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (platinum.mendingToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Mending ").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.mendingDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (platinum.emeraldBlockToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Blok szmaragdu ").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.emeraldBlockDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (platinum.goldBlockToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Blok złota ").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.goldBlockDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (platinum.ironBlockToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Blok żelaza ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xDCDCDC)).withBold(true)))
                            .append(Component.literal("x" + savedData.ironBlockDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (platinum.redstoneBlockToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Blok redstone ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xB01905)).withBold(true)))
                            .append(Component.literal("x" + savedData.redstoneBlockDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (platinum.dirtToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Ziemia ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x866043)).withBold(true)))
                            .append(Component.literal("x" + savedData.dirtDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (platinum.grassBlockToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Blok trawy ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x2FA81F)).withBold(true)))
                            .append(Component.literal("x" + savedData.grassBlockDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (platinum.sandToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Piasek ").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.sandDropped).withStyle(ChatFormatting.WHITE)));
                }
                body = bodyText;

            //GÓRNICZA
            } else if (title.contains("\uE001\uE082\uE002") || title.contains("\uE001\uE172\uE002")) {
                color = 0xFFAAAAAA;

                if (other.czapkiToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Kask Górnika ").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.kaskGornikaDropped).withStyle(ChatFormatting.WHITE)));
                }
                body = bodyText;

            //DRWALA
            } else if (title.contains("\uE001\uE083\uE002") || title.contains("\uE001\uE173\uE002")) {
                color = 0xFF00AA00;

                if (other.czapkiToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Czapka Drwala ").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.czapkaDrwalaDropped).withStyle(ChatFormatting.WHITE)));
                }
                body = bodyText;

            //FARMERSKA
            } else if (title.contains("\uE001\uE084\uE002") || title.contains("\uE001\uE174\uE002")) {
                color = 0xFFFFFF55;

                if (other.czapkiToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Kapelusz Farmera ").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.kapeluszFarmeraDropped).withStyle(ChatFormatting.WHITE)));
                }
                body = bodyText;

            //ŁOWIECKA
            } else if (title.contains("\uE001\uE085\uE002") || title.contains("\uE001\uE175\uE002")) {
                color = 0xFFFF5555;

                if (other.czapkiToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Bandana Łowcy ").withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.bandanaLowcyDropped).withStyle(ChatFormatting.WHITE)));
                }
                body = bodyText;

            //BUDOWLANA
            } else if (title.contains("\uE001\uE086\uE002") || title.contains("\uE001\uE176\uE002")) {
                color = 0xFF00AAAA;

                if (other.czapkiToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Kask Budowniczego ").withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.kaskBudowniczegoDropped).withStyle(ChatFormatting.WHITE)));
                }
                body = bodyText;

            //RYBACKA
            } else if (title.contains("\uE001\uE087\uE002") || title.contains("\uE001\uE177\uE002")) {
                color = 0xFF5555FF;

                if (other.czapkiToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Kapelusz Rybaka ").withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.kapeluszRybakaDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (other.oceanicznaRudaToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Oceaniczna ruda ").withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.oceanicznaRudaDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (other.wybitnaPrzynetaToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Wybitna przynęta ").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.wybitnaPrzynetaDropped).withStyle(ChatFormatting.WHITE)));
                }
                if (other.dobraPrzynetaToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Dobra przynęta ").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.dobraPrzynetaDropped).withStyle(ChatFormatting.WHITE)));
                }
                body = bodyText;

            //CZARODZIEJSKA
            } else if (title.contains("\uE001\uE088\uE002") || title.contains("\uE001\uE178\uE002")) {
                color = 0xFFFF55FF;

                if (other.czapkiToggle) {
                    bodyText.add(Component.empty()
                            .append(Component.literal("Kapelusz Czarodzieja ").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD))
                            .append(Component.literal("x" + savedData.kapeluszCzarodziejaDropped).withStyle(ChatFormatting.WHITE)));
                }
                body = bodyText;

            //NEITHER
            } else {
                body = bodyText;
            }

            if (!body.isEmpty()) {
                ScreenEvents.afterExtract(screen).register((s, ctx, mouseX, mouseY, delta) -> DrawUtil.drawWidget(ctx, color, HudElement.CRATES.position(), body));
            }
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if(!ServerRestrictor.isAllowed()) return;

            String msgString = message.getString();

            var savedData = SUPData.get();

            if (!overlay && msgString.startsWith("SkyCase »")) {
                Minecraft mc = Minecraft.getInstance();
                LocalPlayer player = mc.player;

                if (player != null) playerName = player.getName().getString();

                //OTHERS
                if (msgString.startsWith("SkyCase » Wygrano: Kask Górnika")) {
                    savedData.kaskGornikaDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: Czapka Drwala")) {
                    savedData.czapkaDrwalaDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: Kapelusz Farmera")) {
                    savedData.kapeluszFarmeraDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: Bandana Łowcy")) {
                    savedData.bandanaLowcyDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: Kask Budowniczego")) {
                    savedData.kaskBudowniczegoDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: Kapelusz Rybaka")) {
                    savedData.kapeluszRybakaDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: Kapelusz Czarodzieja")) {
                    savedData.kapeluszCzarodziejaDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: Oceaniczna ruda")) {
                    savedData.oceanicznaRudaDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 2x Oceaniczna ruda")) {
                    savedData.oceanicznaRudaDropped += 2;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 3x Oceaniczna ruda")) {
                    savedData.oceanicznaRudaDropped += 3;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: Wybitna przynęta")) {
                    savedData.wybitnaPrzynetaDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: Dobra przynęta")) {
                    savedData.dobraPrzynetaDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 2x Dobra przynęta")) {
                    savedData.dobraPrzynetaDropped += 2;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 3x Dobra przynęta")) {
                    savedData.dobraPrzynetaDropped += 3;
                    SUPData.save();
                    return;
                }

                //PLATINUM
                if (msgString.startsWith("SkyCase » Wygrano: Klucz do Elementium")) {
                    savedData.elementiumDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: Zaklęta książka (Naprawa I)")) {
                    savedData.mendingDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: Blok szmaragdu")) {
                    savedData.emeraldBlockDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 2x Blok szmaragdu")) {
                    savedData.emeraldBlockDropped += 2;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 3x Blok szmaragdu")) {
                    savedData.emeraldBlockDropped += 3;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 4x Blok szmaragdu")) {
                    savedData.emeraldBlockDropped += 4;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 5x Blok szmaragdu")) {
                    savedData.emeraldBlockDropped += 5;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: Blok złota")) {
                    savedData.goldBlockDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 2x Blok złota")) {
                    savedData.goldBlockDropped += 2;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 3x Blok złota")) {
                    savedData.goldBlockDropped += 3;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 4x Blok złota")) {
                    savedData.goldBlockDropped += 4;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 5x Blok złota")) {
                    savedData.goldBlockDropped += 5;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 6x Blok złota")) {
                    savedData.goldBlockDropped += 6;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 7x Blok złota")) {
                    savedData.goldBlockDropped += 7;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: Blok żelaza")) {
                    savedData.ironBlockDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 2x Blok żelaza")) {
                    savedData.ironBlockDropped += 2;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 3x Blok żelaza")) {
                    savedData.ironBlockDropped += 3;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 4x Blok żelaza")) {
                    savedData.ironBlockDropped += 4;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 5x Blok żelaza")) {
                    savedData.ironBlockDropped += 5;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 6x Blok żelaza")) {
                    savedData.ironBlockDropped += 6;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: Blok redstone")) {
                    savedData.redstoneBlockDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 2x Blok redstone")) {
                    savedData.redstoneBlockDropped += 2;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 3x Blok redstone")) {
                    savedData.redstoneBlockDropped += 3;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 4x Blok redstone")) {
                    savedData.redstoneBlockDropped += 4;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 10x Ziemia")) {
                    savedData.dirtDropped += 10;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 15x Ziemia")) {
                    savedData.dirtDropped += 15;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 20x Ziemia")) {
                    savedData.dirtDropped += 20;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 25x Ziemia")) {
                    savedData.dirtDropped += 25;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 30x Ziemia")) {
                    savedData.dirtDropped += 30;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 10x Blok trawy")) {
                    savedData.grassBlockDropped += 10;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 20x Blok trawy")) {
                    savedData.grassBlockDropped += 20;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 30x Blok trawy")) {
                    savedData.grassBlockDropped += 30;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 10x Piasek")) {
                    savedData.sandDropped += 10;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 20x Piasek")) {
                    savedData.sandDropped += 20;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » Wygrano: 30x Piasek")) {
                    savedData.sandDropped += 30;
                    SUPData.save();
                    return;
                }

                //ELEMENTIUM
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Karambit ::")) {
                    savedData.karambitDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Perun ::")) {
                    savedData.perunDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Cymofan ::")) {
                    savedData.cymofanDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Młot Thora ::")) {
                    savedData.mlotThoraDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Urizel ::")) {
                    savedData.urizelDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Azada ::")) {
                    savedData.azadaDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Spinel ::")) {
                    savedData.spinelDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Karpiołap ::")) {
                    savedData.karpiolapDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Etherica ::")) {
                    savedData.ethericaDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Łuk Legolasa ::")) {
                    savedData.lukLegolasaDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Arbalet ::")) {
                    savedData.arbaletDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Powrót Odysa ::")) {
                    savedData.powrotOdysaDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Cassis ::")) {
                    savedData.cassisDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Cuirass ::")) {
                    savedData.cuirassDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Cuissot ::")) {
                    savedData.cuissotDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Cosset ::")) {
                    savedData.cossetDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Kaptur sztormowy ::")) {
                    savedData.kapturSztormowyDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Kamizelka biwakowa ::")) {
                    savedData.kamizelkaBiwakowaDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Nogawice ognioodporne ::")) {
                    savedData.nogawiceOgnioodporneDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Trapery terenowe ::")) {
                    savedData.traperyTerenoweDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Rivendell ::")) {
                    savedData.rivendellDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Impet ::")) {
                    savedData.impetDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Phlox ::")) {
                    savedData.phloxDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Majster ::")) {
                    savedData.majsterDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Magiczne wiaderko ::")) {
                    savedData.magiczneWiaderkoDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: StatTracker ::")) {
                    savedData.statTrackerDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Jajko niespodzianka ::")) {
                    savedData.jajkoNiespodziankaDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Kontroler magazynów ::")) {
                    savedData.kontrolerMagazynowDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Kapelusz pracusia ::")) {
                    savedData.kapeluszPracusiaDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Tajemnice SkyUPa v.5 ::")) {
                    savedData.tajemniceSkyUPaDropped += 1;
                    SUPData.save();
                    return;
                }
                if (msgString.startsWith("SkyCase » " + playerName + " otworzył Elementium i wygrał: :: Klejnot kupiecki uniwersalny ::")) {
                    savedData.klejnotKupieckiDropped += 1;
                    SUPData.save();
                }
            }
        });
    }
}