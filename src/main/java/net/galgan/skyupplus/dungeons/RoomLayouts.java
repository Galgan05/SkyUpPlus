package net.galgan.skyupplus.dungeons;

import net.galgan.skyupplus.dungeons.layouts.Layouts1x1;
import net.galgan.skyupplus.dungeons.layouts.Layouts1x2;
import net.galgan.skyupplus.dungeons.layouts.Layouts1x3;
import net.galgan.skyupplus.dungeons.layouts.Layouts2x2;
import net.galgan.skyupplus.dungeons.layouts.Layouts5x5;
import net.galgan.skyupplus.dungeons.layouts.RoomData.RoomType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Looks up room doors in the layout files. Spawner and container counts come from scanning (see LearnedRooms).
public final class RoomLayouts {

    private static final Map<String, Map<String, RoomType>> ROOMS_BY_SIZE = Map.of(
            "1x1", Layouts1x1.ROOMS,
            "1x2", Layouts1x2.ROOMS,
            "1x3", Layouts1x3.ROOMS,
            "2x2", Layouts2x2.ROOMS,
            "5x5", Layouts5x5.ROOMS
    );

    public enum Side { BOTTOM, TOP, LEFT, RIGHT }

    // A door from the sign's point of view: which wall, and which tile it's on
    public record LocalDoor(Side side, int widthTile, int lengthTile) {}

    private RoomLayouts() {}

    public static List<LocalDoor> doors(int width, int length, String letter) {
        List<LocalDoor> doors = new ArrayList<>();

        RoomType type = findRoomType(width, length, letter);
        if (type == null) {
            return doors;
        }

        for (String code : type.doors().trim().split("\\s+")) {
            LocalDoor door = parseDoor(code, width, length);

            if (door != null && isInsideRoom(door, width, length)) {
                doors.add(door);
            }
        }

        return doors;
    }

    private static RoomType findRoomType(int width, int length, String letter) {
        Map<String, RoomType> roomsOfThisSize = ROOMS_BY_SIZE.get(width + "x" + length);

        if (roomsOfThisSize == null) {
            return null;
        }

        return roomsOfThisSize.get(letter);
    }

    // "L2" -> left wall, second tile. null if the code is broken.
    private static LocalDoor parseDoor(String code, int width, int length) {
        int tile;
        try {
            // Door codes count from 1, tiles from 0
            tile = Integer.parseInt(code.substring(1)) - 1;
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return null;
        }

        char side = Character.toUpperCase(code.charAt(0));

        return switch (side) {
            case 'B' -> new LocalDoor(Side.BOTTOM, tile, 0);
            case 'T' -> new LocalDoor(Side.TOP, tile, length - 1);
            case 'L' -> new LocalDoor(Side.LEFT, 0, tile);
            case 'R' -> new LocalDoor(Side.RIGHT, width - 1, tile);
            default -> null;
        };
    }

    private static boolean isInsideRoom(LocalDoor door, int width, int length) {
        boolean widthOk = door.widthTile() >= 0 && door.widthTile() < width;
        boolean lengthOk = door.lengthTile() >= 0 && door.lengthTile() < length;
        return widthOk && lengthOk;
    }
}