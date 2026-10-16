package net.galgan.skyupplus.dungeons.layouts;

import java.util.HashMap;
import java.util.Map;

// The building blocks of the layout files:
//   room("A", "B1 T1",
//           v(1).spawners(1).barrels(0).chests(0).pots(0),
//           v(2).spawners(0).barrels(3).chests(0).pots(4))
public final class RoomData {

    // One version of a room. containersCounted becomes true as soon as any container count is set.
    public record RoomVariant(int number, int spawners, boolean containersCounted, int chests, int barrels, int pots) {

        // Used for rooms we don't have data for: 1 spawner, containers unknown
        public static RoomVariant defaults(int number) {
            return new RoomVariant(number, 1, false, 0, 0, 0);
        }

        public RoomVariant spawners(int count) {
            return new RoomVariant(number, count, containersCounted, chests, barrels, pots);
        }

        public RoomVariant chests(int count) {
            return new RoomVariant(number, spawners, true, count, barrels, pots);
        }

        public RoomVariant barrels(int count) {
            return new RoomVariant(number, spawners, true, chests, count, pots);
        }

        public RoomVariant pots(int count) {
            return new RoomVariant(number, spawners, true, chests, barrels, count);
        }

        public RoomVariant empty() {
            return new RoomVariant(number, spawners, true, 0, 0, 0);
        }

        public boolean hasSpawner() {
            return spawners > 0;
        }

        public int totalContainers() {
            return chests + barrels + pots;
        }
    }

    // doors is a list of door codes like "B1 L2 T1" (see the layout files)
    public record RoomType(String letter, String doors, Map<Integer, RoomVariant> variants) {

        public RoomVariant variant(int number) {
            return variants.getOrDefault(number, RoomVariant.defaults(number));
        }
    }

    private RoomData() {}

    public static RoomVariant v(int number) {
        return RoomVariant.defaults(number);
    }

    public static Map.Entry<String, RoomType> room(String letter, String doors, RoomVariant... variants) {
        Map<Integer, RoomVariant> variantsByNumber = new HashMap<>();

        for (RoomVariant variant : variants) {
            variantsByNumber.put(variant.number(), variant);
        }

        return Map.entry(letter, new RoomType(letter, doors, Map.copyOf(variantsByNumber)));
    }
}