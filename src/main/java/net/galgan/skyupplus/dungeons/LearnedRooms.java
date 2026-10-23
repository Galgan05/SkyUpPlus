package net.galgan.skyupplus.dungeons;

import net.galgan.skyupplus.dungeons.layouts.RoomData.RoomVariant;

import java.util.HashMap;
import java.util.Map;

// Spawner and container counts for each room code,
// taken from the first time the player walks into a room with that code.
// Only kept for the current dungeon.
public final class LearnedRooms {

    // Keyed by the room code on the sign, e.g. "1x2-A-3"
    private static final Map<String, RoomVariant> LEARNED = new HashMap<>();

    private LearnedRooms() {}

    // What was learned about a room code, or null if nothing yet
    public static RoomVariant get(String label) {
        return LEARNED.get(label);
    }

    // Containers are only taken from the first count, since later counts are lower after looting.
    // A spawner's hologram can still be out of sight when the player walks in,
    // so the spawner count keeps the highest number seen.
    public static void learn(String label, int number, int spawners, RoomContainerCounter.Counts counts) {
        RoomVariant known = LEARNED.get(label);

        if (known == null) {
            LEARNED.put(label, RoomVariant.defaults(number)
                    .spawners(spawners)
                    .chests(counts.chests())
                    .barrels(counts.barrels())
                    .pots(counts.pots()));
        } else if (spawners > known.spawners()) {
            LEARNED.put(label, known.spawners(spawners));
        }
    }

    public static void clear() {
        LEARNED.clear();
    }
}
