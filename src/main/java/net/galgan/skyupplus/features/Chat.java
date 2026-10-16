package net.galgan.skyupplus.features;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.galgan.skyupplus.config.SUPConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import java.util.Set;

public class Chat {

    public static void register() {

        ClientReceiveMessageEvents.ALLOW_GAME.register((Component message, boolean overlay) -> {
            String msg = message.getString();

            if (SUPConfig.get().chat.disableAnnouncements && ANNOUNCEMENTS.contains(msg)) {
                return false;
            }

            if (SUPConfig.get().chat.disableCrouchingInfo && CROUCHING.contains(msg)) {
                return false;
            }

            if (SUPConfig.get().chat.disableAFKInfo && AFK.contains(msg)) {
                return false;
            }

            if (SUPConfig.get().chat.shortenAuctions && AUCTIONS.contains(msg)) {
                return false;
            }

            if(SUPConfig.get().chat.disableForeignElementium && msg.startsWith("SkyCase » ") && msg.contains("otworzył Elementium i wygrał")) {
                Minecraft mc = Minecraft.getInstance();
                LocalPlayer player = mc.player;

                if (player == null) {
                    return true;
                }

                return msg.startsWith("SkyCase » " + player.getName().getString());
            }

            return true;
        });
    }

    private static final Set<String> AUCTIONS = Set.of(
            "Aby zalicytować wpisz /licytuj [kwota/max].",
            "Aby wyłączyć spam wpisz /a spam.",
            "Aby ukryć tą aukcję wpisz /a ukryj."
    );

    private static final Set<String> AFK = Set.of(
            "AFK » Jesteś teraz AFK!",
            "AFK » Nie jesteś już AFK!"
    );

    private static final Set<String> CROUCHING = Set.of(
            "Zegarek » Kucnij, by zebrać zegarek!",
            "Magazyn » Kucnij, aby zebrać magazyn!",
            "Piece » Musisz kucnąć, by podnieść piec.",
            "SkyUP » Kucnij, aby wydobyć ametyst!"
    );

    private static final Set<String> ANNOUNCEMENTS = Set.of(
            "                 +-----------------------+",
            "                                » SkyUP «",
            "                                       ",
            "                      Pomóż nam rozwijać serwer!",
            "              Doładuj Tokeny i wydaj je pod /token!",
            "                              skyup.pl/sklep",
            "           Macro, skrypty i cheaty są surowo zakazane",
            "                  i karane nawet usunięciem wyspy!",
            "             Tokeny mozesz wydac w TokenShopie pod",
            "                                   /token!",
            "                      Bądź na bieżąco i zdobywaj",
            "                       SkyPunkty na Discordzie!",
            "                                dc.skyup.pl",
            "                     Głosuj i zgarniaj SkyPunkty!",
            "                                  /glosuj",
            "                    Zbuduj ładną wyspę i zdobądź",
            "                    nagrody w Galerii architektów!",
            "                          skyup.pl/wiki/galeria",
            "              Rekrutacja na Helpera i Budowniczego!",
            "                               /rekrutacja",
            "                  Szczegółowy regulamin serwera?",
            "                                /regulamin",
            "                              Masz problem?",
            "                               /zgloszenia!",
            "                                  UWAGA!",
            "                  Kradzieże i oszustwa są zakazane!",
            "               Potrzebujesz informacji o komendach,",
            "                            bądź rozgrywce?",
            "                    Skorzystaj z komendy /pomoc",
            "                  Sprawdź panel gracza na stronie!",
            "                              skyup.pl/panel",
            "                    Nie wiesz jak coś wytworzyć?!",
            "                            Sprawdź /recipe!",
            "                     Szukasz informacji o grze?",
            "                           Sprawdź naszą wiki!",
            "                               skyup.pl/wiki",
            "              Sprawdź listę dozwolonych dodatków na",
            "                           skyup.pl/regulamin!"
    );
}