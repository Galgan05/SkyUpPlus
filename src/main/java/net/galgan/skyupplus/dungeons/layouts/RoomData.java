package net.galgan.skyupplus.dungeons.layouts;

import java.util.Map;

// The building blocks of the layout files, which only list each room's doors:
//   room("A", "B1 T1")
// Spawner and container counts come from scanning the room (see LearnedRooms).
public final class RoomData {

    // What's in one version of a room. containersCounted becomes true as soon as any container count is set.
    public record RoomVariant(int number, int spawners, boolean containersCounted, int chests, int barrels, int pots) {

        // Used until a room has been counted: 1 spawner, containers unknown
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

    // doors is a list of door codes like "B1 L2 T1" (see the layout files). Every version of a room shares them.
    public record RoomType(String letter, String doors) {}

    private RoomData() {}

    public static Map.Entry<String, RoomType> room(String letter, String doors) {
        return Map.entry(letter, new RoomType(letter, doors));
    }
}