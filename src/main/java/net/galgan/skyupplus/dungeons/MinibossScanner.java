package net.galgan.skyupplus.dungeons;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

// Remembers where each miniboss was first seen
public final class MinibossScanner {

    // Miniboss names are written in dark red
    private static final int MINIBOSS_NAME_COLOR = 0xAA0000;

    // What a real player name looks like. Player entities with any other name are NPCs.
    private static final Pattern PLAYER_NAME = Pattern.compile("[A-Za-z0-9_]{1,16}");

    // The first place the miniboss was seen, so it stays in the same room when it wanders around
    public record MobPos(double x, double z, String name) {}

    private static final Map<UUID, MobPos> HOMES = new LinkedHashMap<>();

    private MinibossScanner() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(MinibossScanner::tick);
    }

    public static Collection<MobPos> getHomes() {
        return Collections.unmodifiableCollection(HOMES.values());
    }

    public static void forgetIf(Predicate<MobPos> condition) {
        HOMES.values().removeIf(condition);
    }

    private static void tick(Minecraft mc) {
        if (!shouldRun(mc)) {
            HOMES.clear();
            return;
        }

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof Mob) && !isNpc(mc, entity)) {
                continue;
            }

            Component customName = entity.getCustomName();
            if (customName == null) {
                continue;
            }

            String bossName = minibossName(customName);
            if (bossName.isEmpty()) {
                continue;
            }

            if (entity instanceof LivingEntity living && living.isDeadOrDying()) {
                continue;
            }

            HOMES.putIfAbsent(entity.getUUID(), new MobPos(entity.getX(), entity.getZ(), bossName));
        }
    }

    private static boolean shouldRun(Minecraft mc) {
        if (mc.level == null) {
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

    private static boolean isNpc(Minecraft mc, Entity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }

        if (player == mc.player) {
            return false;
        }

        return !PLAYER_NAME.matcher(player.getGameProfile().name()).matches();
    }

    // Keeps only the dark red part of the name, starting at its first letter:
    // "828.0 ❤ | 🦂 Mistyczny Czyken" -> "Mistyczny Czyken"
    private static String minibossName(Component name) {
        StringBuilder darkRedText = new StringBuilder();

        name.visit((style, text) -> {
            if (style.getColor() != null && style.getColor().getValue() == MINIBOSS_NAME_COLOR) {
                darkRedText.append(text);
            }
            return Optional.empty();
        }, Style.EMPTY);

        return darkRedText.toString().replaceFirst("^[^\\p{L}]+", "").trim();
    }
}