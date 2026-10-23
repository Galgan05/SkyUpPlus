package net.galgan.skyupplus.dungeons;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Tracks which spawners are active and which are cleared, using the hologram above each spawner
public final class SpawnerScanner {

    private static final String ACTIVE_PREFIX = "Pozostałe moby:";
    private static final String CLEARED_PREFIX = "Posiłki za:";
    private static final Pattern NUMBER = Pattern.compile("(\\d+)");
    private static final long DEFAULT_RESPAWN_MS = 120_000;

    public record SpawnerPos(int x, int z) {}

    // respawnAt is only used while the spawner is cleared
    private record State(boolean active, long respawnAt) {}

    private static final Map<SpawnerPos, State> SPAWNERS = new HashMap<>();

    private SpawnerScanner() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(SpawnerScanner::tick);
    }

    private static void tick(Minecraft mc) {
        if (!shouldRun(mc)) {
            SPAWNERS.clear();
            return;
        }

        long now = Util.getMillis();
        Set<SpawnerPos> visible = scanHolograms(mc, now);
        respawnHiddenSpawners(visible, now);
    }

    private static boolean shouldRun(Minecraft mc) {
        if (mc.level == null || mc.player == null) {
            return false;
        }
        if (!SignScanner.isInDungeon(mc.level)) {
            return false;
        }
        if (!ServerRestrictor.isAllowed()) {
            return false;
        }
        return SUPConfig.get().dungeons.dungeonMap.dungeonMapToggle;
    }

    // Reads every spawner hologram in render distance and returns where they are
    private static Set<SpawnerPos> scanHolograms(Minecraft mc, long now) {
        Set<SpawnerPos> visible = new HashSet<>();

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof ArmorStand)) {
                continue;
            }

            Component customName = entity.getCustomName();
            if (customName == null) {
                continue;
            }

            String name = customName.getString();
            SpawnerPos pos = new SpawnerPos((int) Math.floor(entity.getX()), (int) Math.floor(entity.getZ()));

            if (name.startsWith(ACTIVE_PREFIX)) {
                SPAWNERS.put(pos, new State(true, 0));
                visible.add(pos);
            } else if (name.startsWith(CLEARED_PREFIX)) {
                SPAWNERS.put(pos, new State(false, now + readRespawnDelay(name)));
                visible.add(pos);
            }
        }

        return visible;
    }

    // The first number after "Posiłki za:" is the seconds left. Uses the default if there's none.
    private static long readRespawnDelay(String hologramName) {
        Matcher matcher = NUMBER.matcher(hologramName.substring(CLEARED_PREFIX.length()));

        if (matcher.find()) {
            return Long.parseLong(matcher.group(1)) * 1000;
        }

        return DEFAULT_RESPAWN_MS;
    }

    // A cleared spawner that's out of sight can't update itself, so it becomes active once its timer runs out
    private static void respawnHiddenSpawners(Set<SpawnerPos> visible, long now) {
        for (Map.Entry<SpawnerPos, State> entry : SPAWNERS.entrySet()) {
            boolean hidden = !visible.contains(entry.getKey());
            State state = entry.getValue();

            if (hidden && !state.active() && now >= state.respawnAt()) {
                entry.setValue(new State(true, 0));
            }
        }
    }

    // Every spawner seen so far, active or cleared
    public static Set<SpawnerPos> getAll() {
        return Collections.unmodifiableSet(SPAWNERS.keySet());
    }

    public static Set<SpawnerPos> getCleared() {
        Set<SpawnerPos> cleared = new HashSet<>();

        for (Map.Entry<SpawnerPos, State> entry : SPAWNERS.entrySet()) {
            if (!entry.getValue().active()) {
                cleared.add(entry.getKey());
            }
        }

        return cleared;
    }

    public static Set<SpawnerPos> getActive() {
        Set<SpawnerPos> active = new HashSet<>();

        for (Map.Entry<SpawnerPos, State> entry : SPAWNERS.entrySet()) {
            if (entry.getValue().active()) {
                active.add(entry.getKey());
            }
        }

        return active;
    }

    // Time left until the spawner respawns, or 0 if it's active or unknown
    public static long millisUntilRespawn(SpawnerPos pos) {
        State state = SPAWNERS.get(pos);

        if (state == null || state.active()) {
            return 0;
        }

        return Math.max(0, state.respawnAt() - Util.getMillis());
    }
}