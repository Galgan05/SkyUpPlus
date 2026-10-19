package net.galgan.skyupplus.features;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.mixin.BossBarAccessor;
import net.galgan.skyupplus.util.Number;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Reads the job xp from the boss bar and shows xp/min and the time left until the next level
public class XPCounter {

    // In the "XP" display mode, the counter hides this long after the last xp gain
    private static final long HIDE_AFTER_MS = 30_000;

    // "Górnik (poziom 12) 1 234,5/5 000 xp" -> job, level, current xp, xp needed
    private static final Pattern SKILL_BAR = Pattern.compile(
            "^(\\w+) \\(poziom (\\d+)\\) ([\\d ,]+)/([\\d ]+) xp", Pattern.UNICODE_CHARACTER_CLASS);

    // The number is how many seconds without xp count as a pause, based on how often the job gives xp
    private static final Map<String, JobTracker.Config> SKILL_CONFIGS = Map.of(
            "Farmer",     JobTracker.Config.simple(3),
            "Górnik",     JobTracker.Config.simple(3),
            "Drwal",      JobTracker.Config.simple(5),
            "Budowniczy", JobTracker.Config.simple(5),
            "Czarodziej", JobTracker.Config.simple(10),
            "Rybak",      JobTracker.Config.simple(120),
            "Łowca",      new JobTracker.Config(40, 150, 4, 3, 20)
    );
    private static final JobTracker.Config DEFAULT_CONFIG = JobTracker.Config.simple(10);

    private static final Map<String, JobTracker> TRACKERS = new HashMap<>();

    public static List<Component> body = new ArrayList<>();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(XPCounter::tick);
    }

    private static void tick(Minecraft mc) {
        if (!ServerRestrictor.isAllowed()) {
            return;
        }
        if (mc.player == null) {
            return;
        }

        long now = System.currentTimeMillis();
        BossHealthOverlay bossBars = mc.gui.hud.getBossOverlay();

        for (LerpingBossEvent bar : ((BossBarAccessor) bossBars).getBossBarsMap().values()) {
            readJobBar(bar.getName().getString(), now);
        }

        body = getBody();
    }

    private static void readJobBar(String title, long now) {
        if (!isJob(title)) {
            return;
        }

        Matcher matcher = SKILL_BAR.matcher(title);
        if (!matcher.find()) {
            return;
        }

        String job = matcher.group(1);
        int level = Integer.parseInt(matcher.group(2));
        double xp = parseNumber(matcher.group(3));
        double xpNeeded = parseNumber(matcher.group(4));

        JobTracker tracker = TRACKERS.get(job);
        if (tracker == null) {
            JobTracker.Config config = SKILL_CONFIGS.getOrDefault(job, DEFAULT_CONFIG);
            TRACKERS.put(job, new JobTracker(job, config, level, xp, xpNeeded, now));
        } else {
            tracker.update(now, level, xp, xpNeeded);
        }
    }

    private static boolean isJob(String title) {
        for (String job : SKILL_CONFIGS.keySet()) {
            if (title.startsWith(job)) {
                return true;
            }
        }
        return false;
    }

    private static double parseNumber(String text) {
        return Double.parseDouble(text.trim().replace(" ", "").replace(",", "."));
    }

    public static List<Component> getBody() {
        SUPConfig.XPCounterDisplayBehavior displayBehavior = SUPConfig.get().xpcounter.xpCounterDisplayBehavior;

        if (displayBehavior == SUPConfig.XPCounterDisplayBehavior.NEVER) {
            return new ArrayList<>();
        }

        JobTracker tracker = mostRecentTracker();
        if (tracker == null) {
            return new ArrayList<>();
        }

        long now = System.currentTimeMillis();

        long hideAfter = Math.max(HIDE_AFTER_MS, tracker.pauseLimitMs());
        long sinceLastXp = now - tracker.lastUpdateTime;
        if (displayBehavior == SUPConfig.XPCounterDisplayBehavior.XP && sinceLastXp > hideAfter) {
            return new ArrayList<>();
        }

        double xpLeft = Math.max(0, tracker.targetXP - tracker.currentXP);

        // Get the rate before isStable(): getting it can finish a merged xp gain, which changes isStable()
        double xpPerSecond = tracker.displayRatePerSecond(now);
        boolean paused = tracker.isPaused(now);
        boolean estimate = !tracker.isStable();

        List<Component> lines = new ArrayList<>();
        if (SUPConfig.get().xpcounter.xpLines.jobName) {
            lines.add(line("Praca: ", tracker.name));
        }
        if (SUPConfig.get().xpcounter.xpLines.nextLevel) {
            lines.add(line("Następny Poziom: ", String.valueOf(tracker.level + 1)));
        }
        if (SUPConfig.get().xpcounter.xpLines.remainingXP) {
            lines.add(line("Pozostałe XP: ", Number.format(xpLeft)));
        }

        if (Double.isNaN(xpPerSecond) || xpPerSecond <= 0) {
            if (SUPConfig.get().xpcounter.xpLines.xpPerMin) {
                lines.add(line("XP/min: ", "-"));
            }
            if (SUPConfig.get().xpcounter.xpLines.timeRemaining) {
                lines.add(line("Przewidywany Czas: ", "-"));
            }
            return lines;
        }

        // Gray while paused or still estimating. Estimates also get a "~" in front.
        ChatFormatting valueColor = ChatFormatting.WHITE;
        if (paused || estimate) {
            valueColor = ChatFormatting.GRAY;
        }
        String prefix = estimate ? "~" : "";
        int secondsLeft = (int) (xpLeft / xpPerSecond);

        MutableComponent rateLine = line("xp/min: ", prefix + Number.format(xpPerSecond * 60), valueColor);
        if (paused) {
            rateLine.append(Component.literal(" (pauza)").withStyle(ChatFormatting.DARK_GRAY));
        }

        if (SUPConfig.get().xpcounter.xpLines.xpPerMin) {
            lines.add(rateLine);
        }
        if (SUPConfig.get().xpcounter.xpLines.timeRemaining) {
            lines.add(line("Przewidywany czas: ", prefix + Number.formatTime(secondsLeft), valueColor));
        }
        return lines;
    }

    // The job that gained xp most recently
    private static JobTracker mostRecentTracker() {
        JobTracker mostRecent = null;

        for (JobTracker tracker : TRACKERS.values()) {
            if (mostRecent == null || tracker.lastUpdateTime > mostRecent.lastUpdateTime) {
                mostRecent = tracker;
            }
        }

        return mostRecent;
    }

    private static MutableComponent line(String label, String value) {
        return line(label, value, ChatFormatting.WHITE);
    }

    private static MutableComponent line(String label, String value, ChatFormatting valueColor) {
        return Component.empty()
                .append(Component.literal(label).withStyle(ChatFormatting.AQUA))
                .append(Component.literal(value).withStyle(valueColor));
    }

    // Tracks one job and works out a steady xp/second.
    // Only active time counts, so breaks don't drag the rate down.
    private static final class JobTracker {

        // pauseAfterSec:             at least this long without xp counts as a pause
        // minWindowSec, minEvents:   the rate is averaged over at least this much active time and this many gains
        // mergeGapSec, maxGroupSec:  gains less than mergeGapSec apart count as one gain, up to maxGroupSec long (0 = off)
        record Config(double pauseAfterSec, double minWindowSec, int minEvents, double mergeGapSec, double maxGroupSec) {

            static Config simple(double pauseAfterSec) {
                return new Config(pauseAfterSec, 60, 8, 0, 0);
            }
        }

        // activeTime: how many active seconds into tracking the gain happened
        private record Gain(double activeTime, double xp) {}

        // Several gains being merged into one
        private static final class Group {
            final double start;
            double end;
            double xp;

            Group(double start, double xp) {
                this.start = start;
                this.end = start;
                this.xp = xp;
            }
        }

        // The shown rate eases toward the real rate over about this many seconds
        private static final double SMOOTHING_SEC = 2.0;

        private final Config config;

        final String name;
        int level;
        double currentXP;
        double targetXP;
        long lastUpdateTime;

        private final List<Gain> gains = new ArrayList<>();
        private double xpInWindow = 0;           // total xp of everything in `gains`
        private double activeSeconds = 0;
        private long lastGainMs = -1;            // -1 = no gain yet
        private double averageGap = -1;          // seconds between gains, -1 = unknown yet
        private double lastRate = Double.NaN;    // kept while paused
        private Group openGroup = null;

        private double shownRate = Double.NaN;
        private long lastShownMs = -1;

        JobTracker(String name, Config config, int level, double currentXP, double targetXP, long now) {
            this.name = name;
            this.config = config;
            this.level = level;
            this.currentXP = currentXP;
            this.targetXP = targetXP;
            this.lastUpdateTime = now;
        }

        void update(long now, int newLevel, double newXP, double newTarget) {
            double gain = 0;

            if (newLevel > level) {
                // Leveled up: the rest of the old level, plus what's already in the new one
                gain = Math.max(0, targetXP - currentXP) + newXP;
            } else if (newLevel == level) {
                gain = newXP - currentXP;
            }

            level = newLevel;
            currentXP = newXP;
            targetXP = newTarget;

            if (gain > 0) {
                addGain(now, gain);
            }
            closeGroupIfDone(now);
        }

        private void addGain(long now, double xp) {
            double gap = -1;  // seconds since the previous gain, -1 = this is the first one

            if (lastGainMs >= 0) {
                gap = (now - lastGainMs) / 1000.0;

                if (gap <= pauseLimitSec()) {
                    activeSeconds += gap;
                    updateAverageGap(gap);
                } else {
                    // After a pause, count a normal gap instead of the whole break
                    activeSeconds += Math.max(averageGap, 0);
                }
            }

            lastGainMs = now;
            lastUpdateTime = now;

            if (config.mergeGapSec() <= 0) {
                addToWindow(activeSeconds, xp);
                return;
            }

            boolean joinsGroup = openGroup != null
                    && gap >= 0
                    && gap <= config.mergeGapSec()
                    && activeSeconds - openGroup.start < config.maxGroupSec();

            if (joinsGroup) {
                openGroup.xp += xp;
                openGroup.end = activeSeconds;
            } else {
                closeGroup();
                openGroup = new Group(activeSeconds, xp);
            }
        }

        private void updateAverageGap(double gap) {
            if (averageGap < 0) {
                averageGap = gap;
            } else {
                averageGap = 0.8 * averageGap + 0.2 * gap;
            }
        }

        private void closeGroupIfDone(long now) {
            if (openGroup == null || lastGainMs < 0) {
                return;
            }

            double secondsSinceGain = (now - lastGainMs) / 1000.0;
            if (secondsSinceGain > config.mergeGapSec()) {
                closeGroup();
            }
        }

        private void closeGroup() {
            if (openGroup == null) {
                return;
            }

            Group group = openGroup;
            openGroup = null;
            addToWindow(group.end, group.xp);
        }

        private void addToWindow(double activeTime, double xp) {
            gains.add(new Gain(activeTime, xp));
            xpInWindow += xp;
            trimWindow(activeTime);
        }

        // Drops the oldest gains, but keeps at least minEvents of them and at least minWindowSec of active time
        private void trimWindow(double latestTime) {
            int keepAtLeast = Math.max(config.minEvents(), 2);

            while (gains.size() > keepAtLeast) {
                // Without the oldest gain, the second one becomes the start of the window
                double windowWithoutOldest = latestTime - gains.get(1).activeTime();
                if (windowWithoutOldest < config.minWindowSec()) {
                    break;
                }

                Gain oldest = gains.removeFirst();
                xpInWindow -= oldest.xp();
            }
        }

        private double pauseLimitSec() {
            return Math.max(config.pauseAfterSec(), 2 * Math.max(averageGap, 0));
        }

        long pauseLimitMs() {
            return (long) (pauseLimitSec() * 1000);
        }

        boolean isPaused(long now) {
            return lastGainMs < 0 || now - lastGainMs > pauseLimitMs();
        }

        // False while there's too little data, so the rate is only an estimate
        boolean isStable() {
            return gains.size() >= config.minEvents();
        }

        // Average xp per active second over the window. While paused, the last rate is kept.
        private double ratePerSecond(long now) {
            closeGroupIfDone(now);

            if (gains.size() < 2 || isPaused(now)) {
                return lastRate;
            }

            Gain first = gains.getFirst();
            Gain last = gains.getLast();
            double timeSpan = last.activeTime() - first.activeTime();
            if (timeSpan <= 0) {
                return lastRate;
            }

            // The window starts at the first gain, so its xp was earned before the window
            double xpInSpan = xpInWindow - first.xp();
            lastRate = xpInSpan / timeSpan;
            return lastRate;
        }

        // The rate shown on screen: eases toward the real rate, so the number doesn't jump around
        double displayRatePerSecond(long now) {
            double rate = ratePerSecond(now);

            if (Double.isNaN(rate)) {
                shownRate = Double.NaN;
                lastShownMs = -1;
                return Double.NaN;
            }

            if (Double.isNaN(shownRate) || lastShownMs < 0 || isPaused(now)) {
                shownRate = rate;
            } else {
                double secondsPassed = (now - lastShownMs) / 1000.0;
                double blend = 1 - Math.exp(-secondsPassed / SMOOTHING_SEC);
                shownRate += blend * (rate - shownRate);
            }

            lastShownMs = now;
            return shownRate;
        }
    }
}