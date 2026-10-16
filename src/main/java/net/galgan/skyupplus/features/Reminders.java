package net.galgan.skyupplus.features;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.config.SUPData;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reminders {

    public static List<Component> body = new ArrayList<>();

    public static void register() {

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {

            List<Component> bodyText = new ArrayList<>();

            ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Europe/Warsaw"));
            int weekTime = (zdt.getDayOfWeek().getValue() - 1) * 86400 + zdt.getHour() * 3600 + zdt.getMinute() * 60 + zdt.getSecond();

            //ZAWODY
            if (SUPConfig.get().reminders.toggleZawody) {
                String zawodyDate;

                if (weekTime <= 75600) {
                    zawodyDate = "Poniedziałek 21:00";
                } else if (weekTime <= 151200) {
                    zawodyDate = "Wtorek 18:00";
                } else if (weekTime <= 241200) {
                    zawodyDate = "Środa 19:00";
                } else if (weekTime <= 331200) {
                    zawodyDate = "Czwartek 20:00";
                } else if (weekTime <= 421200) {
                    zawodyDate = "Piątek 21:00";
                } else if (weekTime <= 475200) {
                    zawodyDate = "Sobota 12:00";
                } else if (weekTime <= 493200) {
                    zawodyDate = "Sobota 17:00";
                } else if (weekTime <= 568800) {
                    zawodyDate = "Niedziela 14:00";
                } else if (weekTime <= 594000) {
                    zawodyDate = "Niedziela 21:00";
                } else {
                    zawodyDate = "Poniedziałek 21:00";
                }

                bodyText.add(Component.empty().append(Component.literal("Zawody: ").withStyle(ChatFormatting.DARK_AQUA)).append(Component.literal(zawodyDate)));
            }

            //EVENTY
            if (SUPConfig.get().reminders.toggleEventy) {
                String eventyDate;

                if (weekTime <= 68400) {
                    eventyDate = "Poniedziałek 19:00"; //MON 19:00
                } else if (weekTime <= 158400) {
                    eventyDate = "Wtorek 20:00"; //TUE 20:00
                } else if (weekTime <= 248400) {
                    eventyDate = "Środa 21:00"; //WED 21:00
                } else if (weekTime <= 324000) {
                    eventyDate = "Czwartek 18:00"; //THU 18:00
                } else if (weekTime <= 414000) {
                    eventyDate = "Piątek 19:00"; //FRI 19:00
                } else if (weekTime <= 482400) {
                    eventyDate = "Sobota 14:00"; //SAT 14:00
                } else if (weekTime <= 500400) {
                    eventyDate = "Sobota 19:00"; //SAT 19:00
                } else if (weekTime <= 561600) {
                    eventyDate = "Niedziela 12:00"; //SUN 12:00
                } else if (weekTime <= 579600) {
                    eventyDate = "Niedziela 17:00"; //SUN 17:00
                } else {
                    eventyDate = "Poniedziałek 19:00"; //MON 19:00
                }

                bodyText.add(Component.empty().append(Component.literal("Eventy: ").withStyle(ChatFormatting.DARK_PURPLE)).append(Component.literal(eventyDate)));
            }

            //DAILY QUEST
            if (zdt.getDayOfYear() != SUPData.get().currentDay) {
                SUPData.get().hasSeenDaily = false;
                SUPData.get().currentDay = zdt.getDayOfYear();
                SUPData.save();
            }

            if (SUPConfig.get().reminders.toggleDaily && !SUPData.get().hasSeenDaily) {
                bodyText.add(Component.empty().append(Component.literal("Daily: ").withStyle(ChatFormatting.YELLOW)).append(Component.literal("Dostępne")));
            }

            //GENERATE BODY
            body = bodyText;
        });
    }
}
