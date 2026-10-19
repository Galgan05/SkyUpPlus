package net.galgan.skyupplus.features;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.util.Number;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scoreboard {

    public static List<Component> body = new ArrayList<>();

    private static List<String> cleanBody = new ArrayList<>();
    private static String sc = "-";
    private static String sp = "-";
    private static String st = "-";
    private static String bank = "-";
    private static String level = "-";
    private static String limit = "-";
    private static String reset = "-";
    private static String bonus = "-";
    private static List<Component> abilities = new ArrayList<>();

    private static boolean customScoreboard;

    private static final Set<Character> ABILITY_CHARS = Set.of('\uF011','\uF012','\uF013','\uF014','\uF015','\uF016','\uF017','\uF018','\uF019','\uF01A','\uF01B','\uF01C','\uF01D','\uF01E','\uF050','\uF051');

    private static final DecimalFormat INT_FORMAT;
    private static final DecimalFormat DOUBLE_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ROOT);
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');

        INT_FORMAT = new DecimalFormat("#,##0", symbols);
        DOUBLE_FORMAT = new DecimalFormat("#,##0.00", symbols);
    }

    public static void register() {

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if(!ServerRestrictor.isAllowed()) return;
            if (mc.player == null) return;

            cleanBody = getCustomBody(mc);

            customScoreboard = false;
            bonus = "-";

            for (String line : cleanBody) {
                if (line.startsWith("Portfel")) {
                    customScoreboard = true;
                    sc = parseDoubleField(line, "[\\d ,]+(?:\\.\\d+)?", " SC");
                }
                else if (line.startsWith("Punkty")) {
                    sp = parseIntField(line, "([\\d ]+)\\s*SP\\b", " SP");
                }
                else if (line.startsWith("Tokeny")) {
                    st = parseIntField(line, "([\\d ]+)\\s*ST\\b", " ST");
                }
                else if (line.startsWith("Bank")) {
                    bank = parseDoubleField(line, "[\\d ,]+(?:\\.\\d+)?", " SC");
                }
                else if (line.startsWith("Poziom")) {
                    level = parseIntField(line, "Poziom:\\s*(-?[\\d ]+)", "");
                }
                else if (line.startsWith("Limit")) {
                    Matcher m = Pattern.compile("([\\d, ]+/[\\d ]+)").matcher(line);
                    if (SUPConfig.get().scoreboard.shortenNumbers) {
                        limit = m.find() ? m.group(1).trim().replace(" ", "").replace(",", ".") : "-";
                    } else {
                        limit = m.find() ? m.group(1).trim().replace(",", ".") : "-";
                    }
                }
                else if (line.startsWith("Reset")) {
                    reset = parseStringField(line, "Reset limitu:\\s*(.+)");
                }
                else if (line.contains("BONUS")) {
                    int timeLeft = parseTimeToSeconds(line);
                    bonus = timeLeft > 0 ? Number.formatTimeNoSeconds(timeLeft) : "-";
                }
            }

            if (customScoreboard) {
                body = getCustomScoreboard();
            } else {
                body = getDefaultScoreboard(mc);
            }

            abilities.clear();
        });
    }

    private static List<Component> getCustomScoreboard() {
        List<Component> bodyText = new ArrayList<>();

        if (SUPConfig.get().scoreboard.scoreboardLines.balance) {
            bodyText.add(Component.empty()
                    .append(Component.literal("Portfel: ").withStyle(ChatFormatting.RED))
                    .append(Component.literal(sc).withStyle(ChatFormatting.YELLOW)));
        }
        if (SUPConfig.get().scoreboard.scoreboardLines.points) {
            bodyText.add(Component.empty()
                    .append(Component.literal("Punkty: ").withStyle(ChatFormatting.RED))
                    .append(Component.literal(sp).withStyle(ChatFormatting.YELLOW)));
        }
        if (SUPConfig.get().scoreboard.scoreboardLines.tokens) {
            bodyText.add(Component.empty()
                    .append(Component.literal("Tokeny: ").withStyle(ChatFormatting.RED))
                    .append(Component.literal(st).withStyle(ChatFormatting.YELLOW)));
        }
        if (SUPConfig.get().scoreboard.scoreboardLines.bank) {
            bodyText.add(Component.empty()
                    .append(Component.literal("Bank: ").withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(bank).withStyle(ChatFormatting.YELLOW)));
        }
        if (SUPConfig.get().scoreboard.scoreboardLines.level) {
            bodyText.add(Component.empty()
                    .append(Component.literal("Poziom: ").withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(level).withStyle(ChatFormatting.YELLOW)));
        }
        if (SUPConfig.get().scoreboard.scoreboardLines.limit) {
            bodyText.add(Component.empty()
                    .append(Component.literal("Limit: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(limit).withStyle(ChatFormatting.YELLOW)));
        }
        if (SUPConfig.get().scoreboard.scoreboardLines.reset) {
            bodyText.add(Component.empty()
                    .append(Component.literal("Reset: ").withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(reset).withStyle(ChatFormatting.YELLOW)));
        }

        if (!bonus.equals("-") && SUPConfig.get().scoreboard.scoreboardLines.bonus) {
            bodyText.add(Component.empty()
                    .append(Component.literal("Bonus: ").withStyle(ChatFormatting.GOLD))
                    .append(Component.literal(bonus).withStyle(ChatFormatting.YELLOW)));
        }

        if (abilities != null && !abilities.isEmpty() && SUPConfig.get().scoreboard.scoreboardLines.cooldowns) {
            bodyText.addAll(abilities);
        }

        return bodyText;
    }

    private static List<String> getCustomBody(Minecraft mc) {
        if (mc.level == null) return List.of();

        net.minecraft.world.scores.Scoreboard scoreboard = mc.level.getScoreboard();
        Objective sidebar = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);

        if (sidebar == null) return List.of();

        List<PlayerScoreEntry> entries = new ArrayList<>(scoreboard.listPlayerScores(sidebar));

        entries.sort(Comparator
                .comparingInt(PlayerScoreEntry::value).reversed()
                .thenComparing(PlayerScoreEntry::owner));

        List<String> cleanBody = new ArrayList<>();

        for (PlayerScoreEntry entry : entries) {
            if (entry.isHidden()) continue;

            PlayerTeam team = scoreboard.getPlayersTeam(entry.owner());
            Component line = PlayerTeam.formatNameForTeam(team, entry.ownerName());

            String lineString = line.getString();

            if (lineString.startsWith(" §a§5")) continue;

            lineString = lineString.replaceAll("§.", "");

            if (lineString.startsWith("  »") && !lineString.contains("BONUS")) continue;

            if (lineString.chars().anyMatch(c -> ABILITY_CHARS.contains((char) c))) {
                abilities.add(line);
            }

            cleanBody.add(lineString);
        }

        return cleanBody;
    }

    private static List<Component> getDefaultScoreboard(Minecraft mc) {
        if (mc.level == null) return List.of();

        net.minecraft.world.scores.Scoreboard scoreboard = mc.level.getScoreboard();
        Objective sidebar = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);

        if (sidebar == null) return List.of();

        List<PlayerScoreEntry> entries = new ArrayList<>(scoreboard.listPlayerScores(sidebar));

        entries.sort(Comparator
                .comparingInt(PlayerScoreEntry::value).reversed()
                .thenComparing(PlayerScoreEntry::owner));

        List<Component> lines = new ArrayList<>();

        for (PlayerScoreEntry entry : entries) {
            if (entry.isHidden()) continue;
            if (lines.size() >= 15) break;

            PlayerTeam team = scoreboard.getPlayersTeam(entry.owner());
            Component line = PlayerTeam.formatNameForTeam(team, entry.ownerName());

            if (line.getString().startsWith(" §a§5")) continue;

            lines.add(line);
        }

        return lines;
    }

    private static String parseDoubleField(String line, String regex, String suffix) {
        Matcher m = Pattern.compile(regex).matcher(line);
        if (m.find()) {
            try {
                String raw = m.group(m.groupCount() > 0 ? 1 : 0).trim().replace(",", ".").replace(" ", "");

                if (SUPConfig.get().scoreboard.shortenNumbers) {
                    return Number.format(Double.parseDouble(raw)) + suffix;
                } else {
                    return DOUBLE_FORMAT.format(Double.parseDouble(raw)) + suffix;
                }
            } catch (NumberFormatException ignored) {}
        }
        return "-";
    }

    private static String parseIntField(String line, String regex, String suffix) {
        Matcher m = Pattern.compile(regex).matcher(line);
        if (m.find()) {
            try {
                String raw = m.group(1).trim().replace(" ", "");
                if (SUPConfig.get().scoreboard.shortenNumbers) {
                    return Number.format(Integer.parseInt(raw)) + suffix;
                } else {
                    return INT_FORMAT.format(Integer.parseInt(raw)) + suffix;
                }
            } catch (NumberFormatException ignored) {}
        }
        return "-";
    }

    private static String parseStringField(String line, String regex) {
        Matcher m = Pattern.compile(regex).matcher(line);
        if (m.find()) {
            String v = m.group(1).trim();
            return v.isEmpty() ? "-" : v;
        }
        return "-";
    }

    private static int parseTimeToSeconds(String line) {
        int total = 0;
        Matcher m = Pattern.compile("(\\d+)\\s*([gms])").matcher(line);
        while (m.find()) {
            int value = Integer.parseInt(m.group(1));
            switch (m.group(2)) {
                case "g" -> total += value * 3600;
                case "m" -> total += value * 60;
                case "s" -> total += value;
            }
        }
        return total;
    }
}