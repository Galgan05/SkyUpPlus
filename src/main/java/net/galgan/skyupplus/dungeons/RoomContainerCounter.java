package net.galgan.skyupplus.dungeons;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Counts the chests, barrels and pots left in each room
public final class RoomContainerCounter {

    // Containers only exist between these heights
    private static final int MIN_Y = 0;
    private static final int MAX_Y = 32;

    private static final int RECOUNT_TICKS = 10;     // the room you're in: every 0.5 s
    private static final int ALL_ROOMS_TICKS = 100;  // every other room: once per 5 s
    private static final int TRUST_DISTANCE = 80;    // only rooms this close are fully loaded

    public record Counts(int chests, int barrels, int pots) {
        public int total() {
            return chests + barrels + pots;
        }
    }

    // A room's area in world blocks
    private record Box(int minX, int minZ, int maxX, int maxZ) {
        boolean contains(int x, int z) {
            return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
        }
    }

    private static DungeonGrid.Room currentRoom;
    private static Counts currentCounts;
    private static int ticksUntilRecount;

    // Where the slow sweep over all rooms continues next tick
    private static int nextRoomIndex;

    // Last counts of every room, keyed by its anchor cell (stays the same when the grid is rebuilt)
    private static final Map<DungeonGrid.Cell, Counts> KNOWN = new HashMap<>();

    private RoomContainerCounter() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(RoomContainerCounter::tick);
    }

    public static DungeonGrid.Room getRoom() {
        return currentRoom;
    }

    public static Counts getCounts() {
        return currentCounts;
    }

    public static Counts getKnown(DungeonGrid.Room room) {
        return KNOWN.get(room.anchor());
    }

    private static void tick(Minecraft mc) {
        if (!shouldRun(mc)) {
            currentRoom = null;
            currentCounts = null;
            KNOWN.clear();
            return;
        }

        DungeonGrid grid = DungeonGrid.current();
        countSomeRooms(mc, grid);
        countCurrentRoom(mc, grid);
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

    // Counts a few rooms per tick, so every room gets counted within ALL_ROOMS_TICKS without lagging
    private static void countSomeRooms(Minecraft mc, DungeonGrid grid) {
        List<DungeonGrid.Room> rooms = grid.rooms();
        if (rooms.isEmpty()) {
            return;
        }

        int roomsPerTick = Math.ceilDiv(rooms.size(), ALL_ROOMS_TICKS);

        for (int i = 0; i < roomsPerTick; i++) {
            if (nextRoomIndex >= rooms.size()) {
                nextRoomIndex = 0;
            }

            DungeonGrid.Room room = rooms.get(nextRoomIndex);
            nextRoomIndex++;

            if (room.isStart()) {
                continue;
            }

            Box box = boxOf(grid, room);
            if (isCloseEnough(box, mc.player.getX(), mc.player.getZ()) && allChunksLoaded(mc.level, box)) {
                KNOWN.put(room.anchor(), count(mc.level, box));
            }
        }
    }

    private static void countCurrentRoom(Minecraft mc, DungeonGrid grid) {
        DungeonGrid.Room roomNow = grid.roomAtWorld(mc.player.getX(), mc.player.getZ());

        if (roomNow == null || roomNow.isStart()) {
            currentRoom = null;
            currentCounts = null;
            return;
        }

        // Walked into another room: count it right away
        if (!roomNow.equals(currentRoom)) {
            currentRoom = roomNow;
            ticksUntilRecount = 0;
        }

        ticksUntilRecount--;
        if (ticksUntilRecount > 0) {
            return;
        }

        ticksUntilRecount = RECOUNT_TICKS;
        currentCounts = count(mc.level, boxOf(grid, currentRoom));
        KNOWN.put(currentRoom.anchor(), currentCounts);
    }

    private static Box boxOf(DungeonGrid grid, DungeonGrid.Room room) {
        int minX = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (DungeonGrid.Cell cell : room.cells()) {
            int cellX = grid.cellMinX(cell.gx());
            int cellZ = grid.cellMinZ(cell.gz());

            minX = Math.min(minX, cellX);
            minZ = Math.min(minZ, cellZ);
            maxX = Math.max(maxX, cellX + DungeonGrid.CELL_SIZE - 1);
            maxZ = Math.max(maxZ, cellZ + DungeonGrid.CELL_SIZE - 1);
        }

        return new Box(minX, minZ, maxX, maxZ);
    }

    // True if the whole room is within TRUST_DISTANCE blocks of the player on both axes
    private static boolean isCloseEnough(Box box, double playerX, double playerZ) {
        double farthestX = Math.max(Math.abs(box.minX() - playerX), Math.abs(box.maxX() + 1 - playerX));
        double farthestZ = Math.max(Math.abs(box.minZ() - playerZ), Math.abs(box.maxZ() + 1 - playerZ));

        return farthestX <= TRUST_DISTANCE && farthestZ <= TRUST_DISTANCE;
    }

    // ">> 4" turns a block coordinate into a chunk coordinate
    private static boolean allChunksLoaded(ClientLevel level, Box box) {
        for (int chunkX = box.minX() >> 4; chunkX <= box.maxX() >> 4; chunkX++) {
            for (int chunkZ = box.minZ() >> 4; chunkZ <= box.maxZ() >> 4; chunkZ++) {
                if (!level.hasChunk(chunkX, chunkZ)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static Counts count(ClientLevel level, Box box) {
        int chests = 0;
        int barrels = 0;
        int pots = 0;

        for (int chunkX = box.minX() >> 4; chunkX <= box.maxX() >> 4; chunkX++) {
            for (int chunkZ = box.minZ() >> 4; chunkZ <= box.maxZ() >> 4; chunkZ++) {
                // Returns an empty chunk if it isn't loaded
                LevelChunk chunk = level.getChunk(chunkX, chunkZ);

                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    BlockPos pos = blockEntity.getBlockPos();

                    if (pos.getY() < MIN_Y || pos.getY() > MAX_Y) {
                        continue;
                    }
                    if (!box.contains(pos.getX(), pos.getZ())) {
                        continue;
                    }

                    // Trapped chests count as chests, and each half of a double chest counts
                    switch (blockEntity) {
                        case ChestBlockEntity itemStacks -> chests++;
                        case BarrelBlockEntity itemStacks -> barrels++;
                        case DecoratedPotBlockEntity itemStacks -> pots++;
                        default -> {
                        }
                    }
                }
            }
        }

        return new Counts(chests, barrels, pots);
    }
}