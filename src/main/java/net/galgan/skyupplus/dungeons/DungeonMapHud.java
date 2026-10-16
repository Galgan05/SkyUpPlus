package net.galgan.skyupplus.dungeons;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.galgan.skyupplus.config.HudPosition;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.dungeons.layouts.RoomData;
import net.galgan.skyupplus.util.HudEditor;
import net.galgan.skyupplus.util.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class DungeonMapHud {

    private static final String MOD_ID = "skyupplus";

    // --- Map ---
    private static final int MAP_SIZE = 160;              // the following map is MAP_SIZE x MAP_SIZE GUI pixels
    private static final int CELL_PX = 13;                // one tile = 13 GUI pixels, so 1 pixel per block
    private static final int BACKGROUND = 0x80000000;
    private static final int MAP_FRAME_COLOR = 0xFF555555;

    // --- Room colors ---
    private static final int ROOM_ACTIVE = 0xFFFF5555;    // red: no spawner beaten yet
    private static final int ROOM_PARTIAL = 0xFFFFAA00;   // gold: some spawners beaten, not all
    private static final int ROOM_CLEARED = 0xFF555555;   // gray: every spawner beaten
    private static final int ROOM_SAFE = 0xFF55FF55;      // green: no spawners at all
    private static final int ROOM_START = 0xFF00AA00;     // dark green
    private static final int ROOM_MINIBOSS = 0xFFAA0000;  // dark red: a miniboss lives here
    private static final float OUTLINE_DARKEN = 0.7f;     // room outline = room color x this

    // --- Doors ---
    private static final int DOOR_COLOR = 0xFFFFFF55;
    private static final int DOOR_BLOCKS = 3;  // door width in blocks
    private static final int DOOR_DEPTH = 2;   // how far the mark reaches across the gap between rooms

    // --- Room icons ---
    private static final int ICON_TEXTURE_SIZE = 16;
    private static final int ICON_MAX_SIZE = 16;
    private static final int ICON_PADDING = 1;           // space kept free inside the room's edge
    private static final int ICON_GAP = 1;               // space between icons
    private static final int[] ICON_SIZES = {8, 4};      // allowed sizes, biggest first
    private static final Identifier START_ICON = Identifier.withDefaultNamespace("textures/item/campfire.png");
    private static final String[] SHERDS = {
            "angler", "archer", "arms_up", "blade", "brewer", "burn", "danger", "explorer",
            "flow", "friend", "guster", "heart", "heartbreak", "howl", "miner", "mourner",
            "plenty", "prize", "scrape", "sheaf", "shelter", "skull", "snort"
    };

    // --- Container list ---
    private static final int ITEM_SIZE = 16;
    private static final int CONTAINER_TEXT_COLOR = 0xFFFFFFFF;
    private static final int CONTAINER_LIST_MARGIN = 3;   // gap between the map and the list
    private static final int CONTAINER_ICON_TEXT_GAP = 1;
    private static final int CONTAINER_ENTRY_GAP = 1;

    // --- Shadows ---
    private static final int SHADOW_OFFSET = 1;           // shadows sit this far down-right
    private static final int SHADOW_COLOR = 0x80000000;

    // --- Footsteps ---
    private static final int FOOTSTEP_INTERVAL_TICKS = 10;    // one every 0.5 s
    private static final int FOOTSTEP_LIFETIME_TICKS = 40;    // fades out over 2 s
    private static final int FOOTSTEP_START_ALPHA = 0x80;
    private static final double FOOTSTEP_MIN_DISTANCE = 0.5;  // no new footsteps while standing still
    private static final int FOOTSTEP_RGB = 0xFFFFFF;
    private static final int FOOTSTEP_SIZE = 2;

    // --- Player ---
    private static final int HEAD_SIZE = 6;  // even numbers stay centred

    private record Footstep(double x, double z, int tick) {}

    // A rectangle on screen, from (x1, y1) to (x2, y2)
    private record Rect(int x1, int y1, int x2, int y2) {
        int width() {
            return x2 - x1;
        }

        int height() {
            return y2 - y1;
        }
    }

    private record Size(int width, int height) {}

    private record GridBounds(int minGx, int minGz, int columns, int rows) {}

    private record ContainerEntry(ItemStack icon, String text) {}

    // How many spawners are cleared in each room, and which minibosses live in which room
    private record RoomStatus(Map<DungeonGrid.Room, Integer> clearedSpawners,
                              Map<DungeonGrid.Room, List<String>> bosses) {

        int clearedIn(DungeonGrid.Room room) {
            return clearedSpawners.getOrDefault(room, 0);
        }

        boolean hasBoss(DungeonGrid.Room room) {
            return bosses.containsKey(room);
        }

        List<String> bossesIn(DungeonGrid.Room room) {
            return bosses.get(room);
        }
    }

    private static final List<Footstep> FOOTSTEPS = new ArrayList<>();
    private static int tickCount;  // client ticks spent in the dungeon

    // Created on first use, after the game has loaded its items
    private static ItemStack barrelIcon;
    private static ItemStack chestIcon;
    private static ItemStack potIcon;

    private DungeonMapHud() {}

    public static void register() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "dungeon_map"),
                DungeonMapHud::extract);

        ClientTickEvents.END_CLIENT_TICK.register(DungeonMapHud::updateFootsteps);
    }

    // =====================================================================
    // Public size and preview (used by HudPosition and the HudEditor)
    // =====================================================================

    public static int boxWidth() {
        return (int) Math.ceil(mapSize().width() * scale());
    }

    // The container list sits outside this box
    public static int boxHeight() {
        return (int) Math.ceil(mapSize().height() * scale());
    }

    // What the HudEditor shows while you move the map: the real map in a dungeon, otherwise an empty frame
    public static void drawPreview(GuiGraphicsExtractor graphics) {
        if (isMapVisible()) {
            drawMap(graphics, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
            return;
        }

        HudPosition position = config().dungeonMapPosition;
        int x = position.x(graphics.guiWidth(), boxWidth());
        int y = position.y(graphics.guiHeight(), boxHeight());
        Size size = mapSize();

        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(scale(), scale());
        graphics.fill(0, 0, size.width(), size.height(), BACKGROUND);
        graphics.outline(0, 0, size.width(), size.height(), MAP_FRAME_COLOR);
        graphics.pose().popMatrix();
    }

    // =====================================================================
    // Settings and size
    // =====================================================================

    private static SUPConfig.DungeonMap config() {
        return SUPConfig.get().dungeons.dungeonMap;
    }

    private static boolean isOverview() {
        return config().dungeonMapType == SUPConfig.DungeonMapType.FULL;
    }

    // dungeonMapScale relative to the GUI scale, so a map scale equal to the GUI scale is 1
    private static float scale() {
        int mapScale = config().dungeonMapScale;
        float guiScale = (float) Minecraft.getInstance().getWindow().getGuiScale();

        if (mapScale <= 0 || guiScale <= 0) {
            return 1f;
        }
        return mapScale / guiScale;
    }

    // Size of the map itself (without the container list), before scaling
    private static Size mapSize() {
        if (isOverview()) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.level != null && SignScanner.isInDungeon(mc.level)) {
                GridBounds bounds = overviewBounds(DungeonGrid.current());
                if (bounds != null) {
                    return new Size(bounds.columns() * CELL_PX, bounds.rows() * CELL_PX);
                }
            }
        }

        // The square map, or an overview with nothing to show yet
        return new Size(MAP_SIZE, MAP_SIZE);
    }

    // The part of the grid covered by known rooms, or null if there are none
    private static GridBounds overviewBounds(DungeonGrid grid) {
        if (grid.rooms().isEmpty()) {
            return null;
        }

        int minGx = Integer.MAX_VALUE;
        int minGz = Integer.MAX_VALUE;
        int maxGx = Integer.MIN_VALUE;
        int maxGz = Integer.MIN_VALUE;

        for (DungeonGrid.Cell cell : grid.cells().keySet()) {
            minGx = Math.min(minGx, cell.gx());
            minGz = Math.min(minGz, cell.gz());
            maxGx = Math.max(maxGx, cell.gx());
            maxGz = Math.max(maxGz, cell.gz());
        }

        return new GridBounds(minGx, minGz, maxGx - minGx + 1, maxGz - minGz + 1);
    }

    // =====================================================================
    // Drawing the map
    // =====================================================================

    private static boolean isMapVisible() {
        Minecraft mc = Minecraft.getInstance();

        if (!config().dungeonMapToggle) {
            return false;
        }
        if (mc.level == null || mc.player == null || !SignScanner.isInDungeon(mc.level)) {
            return false;
        }

        // An empty overview draws nothing
        return !isOverview() || !DungeonGrid.current().rooms().isEmpty();
    }

    // While the HudEditor moves the map, it draws the map itself (above its blur)
    private static boolean isBeingEdited() {
        return Minecraft.getInstance().gui.screen() instanceof HudEditor editor
                && editor.element() == HudElement.DUNGEON_MAP;
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (isBeingEdited()) {
            return;
        }
        drawMap(graphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    private static void drawMap(GuiGraphicsExtractor graphics, float partialTick) {
        Minecraft mc = Minecraft.getInstance();

        if (!config().dungeonMapToggle) {
            return;
        }
        if (mc.level == null || mc.player == null || !SignScanner.isInDungeon(mc.level)) {
            return;
        }

        DungeonGrid grid = DungeonGrid.current();
        HudPosition position = config().dungeonMapPosition;
        int screenX = position.x(graphics.guiWidth(), boxWidth());
        int screenY = position.y(graphics.guiHeight(), boxHeight());

        // Blend between last tick and this tick, so the player moves smoothly
        double playerX = Mth.lerp(partialTick, mc.player.xo, mc.player.getX());
        double playerZ = Mth.lerp(partialTick, mc.player.zo, mc.player.getZ());
        float yaw = Mth.rotLerp(partialTick, mc.player.yRotO, mc.player.getYRot());

        graphics.pose().pushMatrix();
        graphics.pose().translate(screenX, screenY);
        graphics.pose().scale(scale(), scale());

        if (isOverview()) {
            drawOverview(graphics, mc, grid, playerX, playerZ, yaw, partialTick);
        } else {
            drawFollowing(graphics, mc, grid, playerX, playerZ, yaw, partialTick);
        }

        graphics.pose().popMatrix();
    }

    // A square map centred on the player, scrolling as they move
    private static void drawFollowing(GuiGraphicsExtractor graphics, Minecraft mc, DungeonGrid grid,
                                      double playerX, double playerZ, float yaw, float partialTick) {
        Rect mapArea = new Rect(0, 0, MAP_SIZE, MAP_SIZE);
        int centerX = MAP_SIZE / 2;
        int centerY = MAP_SIZE / 2;

        graphics.fill(mapArea.x1(), mapArea.y1(), mapArea.x2(), mapArea.y2(), BACKGROUND);

        if (!grid.rooms().isEmpty()) {
            drawScrollingRooms(graphics, grid, playerX, playerZ, partialTick, centerX, centerY, mapArea);
        }

        drawFrame(graphics, mapArea);
        drawPlayerHead(graphics, centerX + 0.5f, centerY + 0.5f, yaw);
        drawContainerList(graphics, mc, MAP_SIZE, MAP_SIZE);
    }

    private static void drawScrollingRooms(GuiGraphicsExtractor graphics, DungeonGrid grid,
                                           double playerX, double playerZ, float partialTick,
                                           int centerX, int centerY, Rect mapArea) {
        // The player's position on the map, in pixels. Rooms are drawn at whole pixels, then the whole
        // map is shifted by the leftover fraction, so it scrolls smoothly instead of jumping.
        double playerPxX = grid.toGridX(playerX) * CELL_PX;
        double playerPxY = grid.toGridZ(playerZ) * CELL_PX;
        int wholePxX = (int) Math.floor(playerPxX);
        int wholePxY = (int) Math.floor(playerPxY);
        float fractionX = (float) (playerPxX - wholePxX);
        float fractionY = (float) (playerPxY - wholePxY);

        // Where grid cell (0, 0) is drawn
        int originX = centerX - wholePxX;
        int originY = centerY - wholePxY;

        RoomStatus status = collectRoomStatus(grid);

        graphics.enableScissor(mapArea.x1(), mapArea.y1(), mapArea.x2(), mapArea.y2());
        graphics.pose().pushMatrix();
        graphics.pose().translate(-fractionX, -fractionY);

        drawRoomsAndDoors(graphics, grid, status, originX, originY, mapArea);

        if (config().dungeonMapFootsteps) {
            // Footsteps use exact positions, so they skip the fraction shift
            float footstepOriginX = (float) (centerX + 0.5f - playerPxX);
            float footstepOriginY = (float) (centerY + 0.5f - playerPxY);

            graphics.pose().pushMatrix();
            graphics.pose().translate(fractionX, fractionY);
            drawFootsteps(graphics, grid, partialTick, footstepOriginX, footstepOriginY);
            graphics.pose().popMatrix();
        }

        drawRoomIcons(graphics, grid, status, originX, originY, mapArea);

        graphics.pose().popMatrix();
        graphics.disableScissor();
    }

    // Every known room at once, fixed in place and without a background. Only the player head moves.
    private static void drawOverview(GuiGraphicsExtractor graphics, Minecraft mc, DungeonGrid grid,
                                     double playerX, double playerZ, float yaw, float partialTick) {
        GridBounds bounds = overviewBounds(grid);
        if (bounds == null) {
            return;
        }

        // Where grid cell (0, 0) is drawn, so the top-left room ends up at (0, 0)
        int originX = -bounds.minGx() * CELL_PX;
        int originY = -bounds.minGz() * CELL_PX;

        RoomStatus status = collectRoomStatus(grid);

        drawRoomsAndDoors(graphics, grid, status, originX, originY, null);
        if (config().dungeonMapFootsteps) {
            drawFootsteps(graphics, grid, partialTick, originX, originY);
        }
        drawRoomIcons(graphics, grid, status, originX, originY, null);

        float headX = (float) (originX + grid.toGridX(playerX) * CELL_PX);
        float headY = (float) (originY + grid.toGridZ(playerZ) * CELL_PX);
        drawPlayerHead(graphics, headX, headY, yaw);

        drawContainerList(graphics, mc, bounds.columns() * CELL_PX, bounds.rows() * CELL_PX);
    }

    // 1 px outline with a shadow. Only the part of the shadow that sticks out (right and bottom) is drawn.
    private static void drawFrame(GuiGraphicsExtractor graphics, Rect area) {
        graphics.fill(area.x2(), area.y1() + SHADOW_OFFSET, area.x2() + SHADOW_OFFSET, area.y2() + SHADOW_OFFSET, SHADOW_COLOR);
        graphics.fill(area.x1() + SHADOW_OFFSET, area.y2(), area.x2(), area.y2() + SHADOW_OFFSET, SHADOW_COLOR);
        graphics.outline(area.x1(), area.y1(), area.width(), area.height(), MAP_FRAME_COLOR);
    }

    // =====================================================================
    // Rooms and doors
    // =====================================================================

    private static RoomStatus collectRoomStatus(DungeonGrid grid) {
        Map<DungeonGrid.Room, Integer> clearedSpawners = new HashMap<>();

        for (SpawnerScanner.SpawnerPos spawner : SpawnerScanner.getCleared()) {
            DungeonGrid.Room room = grid.roomAtWorld(spawner.x() + 0.5, spawner.z() + 0.5);
            if (room != null) {
                clearedSpawners.merge(room, 1, Integer::sum);
            }
        }

        // A miniboss belongs to the room it was first seen in. It's forgotten once that room is cleared.
        // Rooms without spawners count as cleared: the miniboss must have wandered there,
        // and it gets pinned to a room again the next time it's seen.
        Map<DungeonGrid.Room, List<String>> bosses = new HashMap<>();
        Set<MinibossScanner.MobPos> toForget = new HashSet<>();

        for (MinibossScanner.MobPos home : MinibossScanner.getHomes()) {
            DungeonGrid.Room room = grid.roomAtWorld(home.x(), home.z());
            if (room == null) {
                continue;
            }

            int cleared = clearedSpawners.getOrDefault(room, 0);
            if (cleared >= room.spawners()) {
                toForget.add(home);
            } else {
                bosses.computeIfAbsent(room, r -> new ArrayList<>()).add(home.name());
            }
        }

        if (!toForget.isEmpty()) {
            MinibossScanner.forgetIf(toForget::contains);
        }

        return new RoomStatus(clearedSpawners, bosses);
    }

    // visibleArea: rooms completely outside it are skipped. null = draw everything.
    private static void drawRoomsAndDoors(GuiGraphicsExtractor graphics, DungeonGrid grid, RoomStatus status,
                                          int originX, int originY, Rect visibleArea) {
        // Shadows first, so no shadow ends up on top of a room. Door shadows too:
        // the rooms cover most of them, so they only show in the gaps between rooms.
        for (DungeonGrid.Room room : grid.rooms()) {
            Rect roomRect = roomRect(room, originX, originY);
            if (!isVisible(roomRect, visibleArea)) {
                continue;
            }

            fillShadow(graphics, roomRect);
            for (DungeonGrid.Door door : room.doors()) {
                fillShadow(graphics, doorRect(door, originX, originY));
            }
        }

        // Rooms, with a 1 px outline in a darker version of their color
        for (DungeonGrid.Room room : grid.rooms()) {
            Rect roomRect = roomRect(room, originX, originY);
            if (!isVisible(roomRect, visibleArea)) {
                continue;
            }

            int color = roomColor(room, status.clearedIn(room), status.hasBoss(room));
            graphics.fill(roomRect.x1(), roomRect.y1(), roomRect.x2(), roomRect.y2(), color);
            graphics.outline(roomRect.x1(), roomRect.y1(), roomRect.width(), roomRect.height(), darker(color));
        }

        // Doors on top, bridging the gap between two rooms
        for (DungeonGrid.Room room : grid.rooms()) {
            for (DungeonGrid.Door door : room.doors()) {
                Rect doorRect = doorRect(door, originX, originY);
                if (!isVisible(doorRect, visibleArea)) {
                    continue;
                }

                graphics.fill(doorRect.x1(), doorRect.y1(), doorRect.x2(), doorRect.y2(), DOOR_COLOR);
            }
        }
    }

    // A campfire in the start room, otherwise miniboss icons,
    // otherwise a pottery sherd if nothing in the room has been looted yet
    private static void drawRoomIcons(GuiGraphicsExtractor graphics, DungeonGrid grid, RoomStatus status,
                                      int originX, int originY, Rect visibleArea) {
        for (DungeonGrid.Room room : grid.rooms()) {
            Rect roomRect = roomRect(room, originX, originY);
            if (!isVisible(roomRect, visibleArea)) {
                continue;
            }

            if (room.isStart()) {
                drawIcons(graphics, roomRect, List.of(START_ICON));
                continue;
            }

            List<String> bosses = status.bossesIn(room);
            if (bosses != null) {
                drawBossIcons(graphics, roomRect, bosses);
            } else if (config().containerIcons && isUntouched(room)) {
                drawIcons(graphics, roomRect, List.of(sherdFor(room)));
            }
        }
    }

    private static int roomColor(DungeonGrid.Room room, int clearedSpawners, boolean hasMiniboss) {
        if (room.isStart()) {
            return ROOM_START;
        }

        int spawners = room.spawners();

        // Rooms without spawners are always safe, even with a miniboss inside
        if (spawners == 0) {
            return ROOM_SAFE;
        }
        if (hasMiniboss) {
            return ROOM_MINIBOSS;
        }
        if (clearedSpawners >= spawners) {
            return ROOM_CLEARED;
        }
        if (clearedSpawners > 0) {
            return ROOM_PARTIAL;
        }
        return ROOM_ACTIVE;
    }

    // The room on screen, 1 px smaller on every side so there's a gap between neighbouring rooms
    private static Rect roomRect(DungeonGrid.Room room, int originX, int originY) {
        int minGx = Integer.MAX_VALUE;
        int minGz = Integer.MAX_VALUE;
        int maxGx = Integer.MIN_VALUE;
        int maxGz = Integer.MIN_VALUE;

        for (DungeonGrid.Cell cell : room.cells()) {
            minGx = Math.min(minGx, cell.gx());
            minGz = Math.min(minGz, cell.gz());
            maxGx = Math.max(maxGx, cell.gx());
            maxGz = Math.max(maxGz, cell.gz());
        }

        return new Rect(
                originX + minGx * CELL_PX + 1,
                originY + minGz * CELL_PX + 1,
                originX + (maxGx + 1) * CELL_PX - 1,
                originY + (maxGz + 1) * CELL_PX - 1);
    }

    // A door mark: DOOR_BLOCKS long (to scale) and DOOR_DEPTH wide, centred on the wall
    private static Rect doorRect(DungeonGrid.Door door, int originX, int originY) {
        int length = Math.max(1, Math.round(DOOR_BLOCKS * CELL_PX / (float) DungeonGrid.CELL_SIZE));
        int halfLength = length / 2;
        int halfDepth = DOOR_DEPTH / 2;

        int cellLeft = originX + door.cell().gx() * CELL_PX;
        int cellTop = originY + door.cell().gz() * CELL_PX;

        if (door.dx() != 0) {
            // East or west wall
            int wallX = door.dx() > 0 ? cellLeft + CELL_PX : cellLeft;
            int x1 = wallX - halfDepth;
            int y1 = cellTop + CELL_PX / 2 - halfLength;
            return new Rect(x1, y1, x1 + DOOR_DEPTH, y1 + length);
        }

        // North or south wall
        int wallY = door.dz() > 0 ? cellTop + CELL_PX : cellTop;
        int y1 = wallY - halfDepth;
        int x1 = cellLeft + CELL_PX / 2 - halfLength;
        return new Rect(x1, y1, x1 + length, y1 + DOOR_DEPTH);
    }

    // False if the rect is completely outside the visible area. A null area means everything is visible.
    private static boolean isVisible(Rect rect, Rect visibleArea) {
        if (visibleArea == null) {
            return true;
        }

        return rect.x2() >= visibleArea.x1()
                && rect.y2() >= visibleArea.y1()
                && rect.x1() <= visibleArea.x2() + 1
                && rect.y1() <= visibleArea.y2() + 1;
    }

    private static void fillShadow(GuiGraphicsExtractor graphics, Rect rect) {
        graphics.fill(rect.x1() + SHADOW_OFFSET, rect.y1() + SHADOW_OFFSET,
                rect.x2() + SHADOW_OFFSET, rect.y2() + SHADOW_OFFSET, SHADOW_COLOR);
    }

    // The same color with red, green and blue multiplied by OUTLINE_DARKEN
    private static int darker(int argb) {
        int alpha = argb >>> 24;
        int red = (int) (((argb >> 16) & 0xFF) * OUTLINE_DARKEN);
        int green = (int) (((argb >> 8) & 0xFF) * OUTLINE_DARKEN);
        int blue = (int) ((argb & 0xFF) * OUTLINE_DARKEN);
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    // =====================================================================
    // Room icons
    // =====================================================================

    // One icon per miniboss type: duplicates and the Boni/Rotuś pair share one icon.
    // Sorted, so the icons don't swap places between frames.
    private static void drawBossIcons(GuiGraphicsExtractor graphics, Rect room, List<String> bossNames) {
        Map<String, Identifier> iconsByPath = new TreeMap<>();

        for (String name : bossNames) {
            Identifier icon = MinibossIcons.getIcon(name);
            if (icon != null) {
                iconsByPath.put(icon.toString(), icon);
            }
        }

        drawIcons(graphics, room, new ArrayList<>(iconsByPath.values()));
    }

    // Draws icons centred in a room, in whichever grid lets them be biggest.
    // On a tie the most square grid wins. A shorter row goes on top, so 3 icons form a pyramid.
    private static void drawIcons(GuiGraphicsExtractor graphics, Rect room, List<Identifier> icons) {
        if (icons.isEmpty()) {
            return;
        }

        int count = icons.size();
        int freeWidth = room.width() - 2 * ICON_PADDING;
        int freeHeight = room.height() - 2 * ICON_PADDING;

        int bestColumns = 1;
        int bestSize = 0;
        int bestSquareness = Integer.MAX_VALUE;

        for (int columns = 1; columns <= count; columns++) {
            int rows = Math.ceilDiv(count, columns);
            int fitsWidth = (freeWidth - (columns - 1) * ICON_GAP) / columns;
            int fitsHeight = (freeHeight - (rows - 1) * ICON_GAP) / rows;
            int size = iconSize(Math.min(Math.min(fitsWidth, fitsHeight), ICON_MAX_SIZE));
            int squareness = Math.abs(columns - rows);  // 0 = square

            if (size > bestSize || (size == bestSize && squareness < bestSquareness)) {
                bestSize = size;
                bestColumns = columns;
                bestSquareness = squareness;
            }
        }

        if (bestSize == 0) {
            return;
        }

        int size = bestSize;
        int columns = bestColumns;
        int rows = Math.ceilDiv(count, columns);
        int firstRowCount = count - (rows - 1) * columns;
        int step = size + ICON_GAP;
        int startY = room.y1() + (room.height() - (rows * step - ICON_GAP)) / 2;

        int index = 0;
        for (int row = 0; row < rows; row++) {
            int iconsInRow = row == 0 ? firstRowCount : columns;
            int startX = room.x1() + (room.width() - (iconsInRow * step - ICON_GAP)) / 2;

            for (int column = 0; column < iconsInRow; column++) {
                drawIcon(graphics, icons.get(index), startX + column * step, startY + row * step, size);
                index++;
            }
        }
    }

    // The biggest size from ICON_SIZES that fits, or the space there is if none does
    private static int iconSize(int space) {
        for (int size : ICON_SIZES) {
            if (size <= space) {
                return size;
            }
        }
        return space;
    }

    // The icon, with a dark see-through copy of itself as its shadow
    private static void drawIcon(GuiGraphicsExtractor graphics, Identifier icon, int x, int y, int size) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, icon,
                x + SHADOW_OFFSET, y + SHADOW_OFFSET,
                0, 0, size, size,
                ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE,
                SHADOW_COLOR);
        graphics.blit(RenderPipelines.GUI_TEXTURED, icon,
                x, y,
                0, 0, size, size,
                ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE);
    }

    // True if the room still has as many containers as the layout files say. A room that was never
    // counted is untouched too. Rooms without container data, or with nothing to loot, never are.
    private static boolean isUntouched(DungeonGrid.Room room) {
        if (room.isStart()) {
            return false;
        }

        RoomData.RoomVariant info = room.info();
        if (!info.containersCounted() || info.totalContainers() == 0) {
            return false;
        }

        RoomContainerCounter.Counts counts = RoomContainerCounter.getKnown(room);
        return counts == null || counts.total() == info.totalContainers();
    }

    // A "random" sherd based on the room's position, so it stays the same and doesn't flicker
    private static Identifier sherdFor(DungeonGrid.Room room) {
        int hash = room.anchor().hashCode() * 0x9E3779B1;
        hash ^= hash >>> 15;

        String name = SHERDS[Math.floorMod(hash, SHERDS.length)];
        return Identifier.withDefaultNamespace("textures/item/" + name + "_pottery_sherd.png");
    }

    // =====================================================================
    // Container list
    // =====================================================================

    // "[icon] current/expected" for each container type left in the room the player is in
    private static void drawContainerList(GuiGraphicsExtractor graphics, Minecraft mc, int mapWidth, int mapHeight) {
        if (!config().containerCounters) {
            return;
        }

        DungeonGrid.Room room = RoomContainerCounter.getRoom();
        RoomContainerCounter.Counts counts = RoomContainerCounter.getCounts();
        if (room == null || counts == null) {
            return;
        }

        if (barrelIcon == null) {
            barrelIcon = new ItemStack(Items.BARREL);
            chestIcon = new ItemStack(Items.CHEST);
            potIcon = new ItemStack(Items.DECORATED_POT);
        }

        RoomData.RoomVariant info = room.info();
        List<ContainerEntry> entries = new ArrayList<>();
        addEntry(entries, barrelIcon, counts.barrels(), expectedCount(info, info.barrels()));
        addEntry(entries, chestIcon, counts.chests(), expectedCount(info, info.chests()));
        addEntry(entries, potIcon, counts.pots(), expectedCount(info, info.pots()));

        if (entries.isEmpty()) {
            return;
        }

        HudPosition position = config().dungeonMapPosition;
        boolean onRightSide = position.rightSide;

        // In the bottom half of the screen the list goes above the map
        boolean aboveMap = position.y > 0.5f;

        int listHeight = entries.size() * ITEM_SIZE + (entries.size() - 1) * CONTAINER_ENTRY_GAP;
        int y;
        if (aboveMap) {
            y = -CONTAINER_LIST_MARGIN - listHeight;
        } else {
            y = mapHeight + CONTAINER_LIST_MARGIN;
        }

        for (ContainerEntry entry : entries) {
            int entryWidth = ITEM_SIZE + CONTAINER_ICON_TEXT_GAP + mc.font.width(entry.text());
            int x = onRightSide ? mapWidth - entryWidth : 0;

            graphics.item(entry.icon(), x, y);
            // + 4 centres the 8 px tall text next to the 16 px icon
            graphics.text(mc.font, entry.text(), x + ITEM_SIZE + CONTAINER_ICON_TEXT_GAP, y + 4, CONTAINER_TEXT_COLOR, true);

            y += ITEM_SIZE + CONTAINER_ENTRY_GAP;
        }
    }

    private static void addEntry(List<ContainerEntry> entries, ItemStack icon, int current, String expected) {
        if (current > 0) {
            entries.add(new ContainerEntry(icon, current + "/" + expected));
        }
    }

    private static String expectedCount(RoomData.RoomVariant info, int count) {
        if (!info.containersCounted()) {
            return "?";
        }
        return String.valueOf(count);
    }

    // =====================================================================
    // Player head and footsteps
    // =====================================================================

    // Your own face (with the hat layer), like in the tab list. North is up, and the top of the head
    // points where you look.
    private static void drawPlayerHead(GuiGraphicsExtractor graphics, float x, float y, float yaw) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        // Yaw 180 = north, which is up on the map
        float angle = (float) Math.toRadians(yaw - 180.0f);
        int half = HEAD_SIZE / 2;

        // The shadow is moved down-right on the screen, so it falls the same way however the head is turned
        graphics.pose().pushMatrix();
        graphics.pose().translate(x + SHADOW_OFFSET, y + SHADOW_OFFSET);
        graphics.pose().rotate(angle);
        graphics.fill(-half, -half, half, half, SHADOW_COLOR);
        graphics.pose().popMatrix();

        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().rotate(angle);
        PlayerFaceExtractor.extractRenderState(graphics, mc.player.getSkin(), -half, -half, HEAD_SIZE);
        graphics.pose().popMatrix();
    }

    // Drops a footstep every FOOTSTEP_INTERVAL_TICKS, and forgets the ones that have faded out
    private static void updateFootsteps(Minecraft mc) {
        if (mc.level == null || mc.player == null || !SignScanner.isInDungeon(mc.level)) {
            FOOTSTEPS.clear();
            return;
        }

        tickCount++;
        FOOTSTEPS.removeIf(step -> tickCount - step.tick() >= FOOTSTEP_LIFETIME_TICKS);

        if (tickCount % FOOTSTEP_INTERVAL_TICKS != 0) {
            return;
        }

        double x = mc.player.getX();
        double z = mc.player.getZ();

        if (!FOOTSTEPS.isEmpty() && isNear(FOOTSTEPS.getLast(), x, z)) {
            return;
        }

        FOOTSTEPS.add(new Footstep(x, z, tickCount));
    }

    private static boolean isNear(Footstep step, double x, double z) {
        double dx = x - step.x();
        double dz = z - step.z();
        return dx * dx + dz * dz < FOOTSTEP_MIN_DISTANCE * FOOTSTEP_MIN_DISTANCE;
    }

    // (originX, originY) is where grid cell (0, 0) is drawn
    private static void drawFootsteps(GuiGraphicsExtractor graphics, DungeonGrid grid, float partialTick,
                                      float originX, float originY) {
        for (Footstep step : FOOTSTEPS) {
            // Includes the part of the current tick, so the fade is smooth
            float age = tickCount - step.tick() + partialTick;
            float lifeLeft = 1f - age / FOOTSTEP_LIFETIME_TICKS;

            int alpha = Math.round(FOOTSTEP_START_ALPHA * lifeLeft);
            if (alpha <= 0) {
                continue;
            }

            float x = (float) (originX + grid.toGridX(step.x()) * CELL_PX);
            float y = (float) (originY + grid.toGridZ(step.z()) * CELL_PX);

            graphics.pose().pushMatrix();
            graphics.pose().translate(x - FOOTSTEP_SIZE / 2f, y - FOOTSTEP_SIZE / 2f);
            graphics.fill(SHADOW_OFFSET, SHADOW_OFFSET, FOOTSTEP_SIZE + SHADOW_OFFSET, FOOTSTEP_SIZE + SHADOW_OFFSET, (alpha / 2) << 24);
            graphics.fill(0, 0, FOOTSTEP_SIZE, FOOTSTEP_SIZE, (alpha << 24) | FOOTSTEP_RGB);
            graphics.pose().popMatrix();
        }
    }
}