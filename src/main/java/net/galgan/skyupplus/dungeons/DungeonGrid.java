package net.galgan.skyupplus.dungeons;

import net.galgan.skyupplus.dungeons.layouts.RoomData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Turns the signs from SignScanner into a grid of rooms.
// One cell = one 13x13 block tile. Grid X grows to the east and grid Z to the south, like in Minecraft.
// The start room's sign is in cell (0, 0).
public final class DungeonGrid {

    public static final int CELL_SIZE = 13;
    private static final String START_LABEL = "5x5-start-1";

    public record Cell(int gx, int gz) {}

    // A door on the wall of `cell` facing (dx, dz), e.g. (1, 0) = east wall
    public record Door(Cell cell, int dx, int dz) {}

    // width and length are in cells, as written on the sign ("1x2" = 1 wide, 2 long).
    // cells and doors are already turned to match the room's rotation.
    public record Room(Cell anchor, int width, int length, int rotation, String label, int number,
                       List<Cell> cells, List<Door> doors) {

        public boolean isStart() {
            return label.equals(START_LABEL);
        }

        // What was counted in this room code during this dungeon,
        // otherwise a guess: 1 spawner, containers not counted yet.
        // The start room never has spawners or loot.
        public RoomData.RoomVariant info() {
            if (isStart()) {
                return RoomData.RoomVariant.defaults(number).spawners(0).empty();
            }

            RoomData.RoomVariant learned = LearnedRooms.get(label);
            if (learned != null) {
                return learned;
            }

            return RoomData.RoomVariant.defaults(number);
        }

        public boolean hasSpawner() {
            return info().hasSpawner();
        }

        public int spawners() {
            return info().spawners();
        }
    }

    private record RoomSize(int width, int length) {}

    private final Map<Cell, Room> grid;
    private final List<Room> rooms;
    private final boolean hasStart;
    private final int offsetX;
    private final int offsetZ;
    private final int originGx;
    private final int originGz;

    private DungeonGrid(Map<Cell, Room> grid, List<Room> rooms, boolean hasStart,
                        int offsetX, int offsetZ, int originGx, int originGz) {
        this.grid = grid;
        this.rooms = rooms;
        this.hasStart = hasStart;
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
        this.originGx = originGx;
        this.originGz = originGz;
    }

    private static DungeonGrid cached;
    private static int cachedVersion = -1;

    // Only rebuilds when SignScanner found new signs, so it's cheap to call every frame
    public static DungeonGrid current() {
        int version = SignScanner.getVersion();

        if (cached == null || version != cachedVersion) {
            cached = build(SignScanner.getSigns());
            cachedVersion = version;
        }

        return cached;
    }

    // World X of the west-most block of grid column gx
    public int cellMinX(int gx) {
        return (gx + originGx) * CELL_SIZE + offsetX;
    }

    // World Z of the north-most block of grid row gz
    public int cellMinZ(int gz) {
        return (gz + originGz) * CELL_SIZE + offsetZ;
    }

    public double toGridX(double worldX) {
        return (worldX - offsetX) / CELL_SIZE - originGx;
    }

    public double toGridZ(double worldZ) {
        return (worldZ - offsetZ) / CELL_SIZE - originGz;
    }

    // The room at a world position, or null if no known room is there
    public Room roomAtWorld(double worldX, double worldZ) {
        int gx = (int) Math.floor(toGridX(worldX));
        int gz = (int) Math.floor(toGridZ(worldZ));
        return roomAt(gx, gz);
    }

    public Room roomAt(int gx, int gz) {
        return grid.get(new Cell(gx, gz));
    }

    // Every room, once each
    public List<Room> rooms() {
        return rooms;
    }

    // Every filled cell and the room covering it
    public Map<Cell, Room> cells() {
        return grid;
    }

    // False until the start sign has been seen. Until then, positions are relative to some other sign.
    public boolean hasStart() {
        return hasStart;
    }

    public static DungeonGrid build(Collection<SignScanner.SignEntry> signs) {
        SignScanner.SignEntry reference = findReferenceSign(signs);
        if (reference == null) {
            return new DungeonGrid(Map.of(), List.of(), false, 0, 0, 0, 0);
        }


        // Every sign stands on its room's corner block, so any sign shows where the grid lines are
        Corner referenceCorner = corner(reference.rotation());
        int cornerX = referenceCorner.westEdge ? reference.x() : reference.x() - (CELL_SIZE - 1);
        int cornerZ = referenceCorner.northEdge ? reference.z() : reference.z() - (CELL_SIZE - 1);

        int offsetX = Math.floorMod(cornerX, CELL_SIZE);
        int offsetZ = Math.floorMod(cornerZ, CELL_SIZE);
        int originGx = Math.floorDiv(reference.x() - offsetX, CELL_SIZE);
        int originGz = Math.floorDiv(reference.z() - offsetZ, CELL_SIZE);

        Map<Cell, Room> grid = new HashMap<>();
        List<Room> rooms = new ArrayList<>();

        for (SignScanner.SignEntry sign : signs) {
            Cell anchor = new Cell(
                    Math.floorDiv(sign.x() - offsetX, CELL_SIZE) - originGx,
                    Math.floorDiv(sign.z() - offsetZ, CELL_SIZE) - originGz);

            Room room = buildRoom(sign, anchor);
            if (room == null) {
                continue;
            }

            rooms.add(room);

            // Rooms shouldn't overlap. If they do, the first one wins.
            for (Cell cell : room.cells()) {
                grid.putIfAbsent(cell, room);
            }
        }

        boolean hasStart = reference.line3().equals(START_LABEL);
        return new DungeonGrid(Collections.unmodifiableMap(grid), Collections.unmodifiableList(rooms), hasStart, offsetX, offsetZ, originGx, originGz);
    }

    // The start sign if we've seen it, otherwise the first sign with a usable rotation
    private static SignScanner.SignEntry findReferenceSign(Collection<SignScanner.SignEntry> signs) {
        SignScanner.SignEntry reference = null;

        for (SignScanner.SignEntry sign : signs) {
            if (corner(sign.rotation()) == null) {
                continue;
            }

            if (sign.line3().equals(START_LABEL)) {
                return sign;
            }

            if (reference == null) {
                reference = sign;
            }
        }

        return reference;
    }

    // Returns null if the sign has an unexpected rotation or label
    private static Room buildRoom(SignScanner.SignEntry sign, Cell anchor) {
        Corner corner = corner(sign.rotation());
        RoomSize size = parseSize(sign.line3());
        if (corner == null || size == null) {
            return null;
        }

        int width = size.width();
        int length = size.length();

        List<Cell> cells = new ArrayList<>();
        for (int w = 0; w < width; w++) {
            for (int l = 0; l < length; l++) {
                cells.add(corner.cellAt(anchor, w, l));
            }
        }

        String letter = parseLetter(sign.line3());

        List<Door> doors = new ArrayList<>();
        for (RoomLayouts.LocalDoor localDoor : RoomLayouts.doors(width, length, letter)) {
            doors.add(corner.turnDoor(anchor, localDoor));
        }

        return new Room(anchor, width, length, sign.rotation(), sign.line3(), parseNumber(sign.line3()),
                List.copyOf(cells), List.copyOf(doors));
    }

    // Where the sign sits in its room, and which way the room extends from it.
    // Seen from the sign, it's the room's bottom-left corner: width goes "right", length goes "up".
    private enum Corner {
        //          west   north   width dir   length dir
        SOUTH_WEST(true,  false,  1,  0,      0, -1),  // rotation 0:  width east,  length north
        NORTH_WEST(true,  true,   0,  1,      1,  0),  // rotation 4:  width south, length east
        NORTH_EAST(false, true,  -1,  0,      0,  1),  // rotation 8:  width west,  length south
        SOUTH_EAST(false, false,  0, -1,     -1,  0);  // rotation 12: width north, length west

        final boolean westEdge;
        final boolean northEdge;
        final int widthDx;
        final int widthDz;
        final int lengthDx;
        final int lengthDz;

        Corner(boolean westEdge, boolean northEdge, int widthDx, int widthDz, int lengthDx, int lengthDz) {
            this.westEdge = westEdge;
            this.northEdge = northEdge;
            this.widthDx = widthDx;
            this.widthDz = widthDz;
            this.lengthDx = lengthDx;
            this.lengthDz = lengthDz;
        }

        // The cell `widthTile` tiles along the width and `lengthTile` tiles along the length from the anchor
        Cell cellAt(Cell anchor, int widthTile, int lengthTile) {
            return new Cell(
                    anchor.gx() + widthTile * widthDx + lengthTile * lengthDx,
                    anchor.gz() + widthTile * widthDz + lengthTile * lengthDz);
        }

        // Turns a door from the layout files (written from the sign's point of view) into world directions
        Door turnDoor(Cell anchor, RoomLayouts.LocalDoor door) {
            Cell cell = cellAt(anchor, door.widthTile(), door.lengthTile());

            return switch (door.side()) {
                case LEFT -> new Door(cell, -widthDx, -widthDz);
                case RIGHT -> new Door(cell, widthDx, widthDz);
                case BOTTOM -> new Door(cell, -lengthDx, -lengthDz);
                case TOP -> new Door(cell, lengthDx, lengthDz);
            };
        }
    }

    private static Corner corner(int rotation) {
        return switch (rotation) {
            case 0 -> Corner.SOUTH_WEST;
            case 4 -> Corner.NORTH_WEST;
            case 8 -> Corner.NORTH_EAST;
            case 12 -> Corner.SOUTH_EAST;
            default -> null;
        };
    }

    // "1x2-A-3" -> 1 wide, 2 long. null if the label doesn't start with a size.
    private static RoomSize parseSize(String label) {
        String[] dimensions = label.split("-", 2)[0].split("x");
        if (dimensions.length != 2) {
            return null;
        }

        try {
            int width = Integer.parseInt(dimensions[0].trim());
            int length = Integer.parseInt(dimensions[1].trim());

            if (width <= 0 || length <= 0) {
                return null;
            }
            return new RoomSize(width, length);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // "1x2-a-3" -> "A". "" if there's no letter.
    private static String parseLetter(String label) {
        String[] parts = label.split("-");
        if (parts.length < 2) {
            return "";
        }
        return parts[1].trim().toUpperCase();
    }

    // "1x2-A-3" -> 3. 0 if there's no number.
    private static int parseNumber(String label) {
        String[] parts = label.split("-");
        if (parts.length < 3) {
            return 0;
        }

        try {
            return Integer.parseInt(parts[2].trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}