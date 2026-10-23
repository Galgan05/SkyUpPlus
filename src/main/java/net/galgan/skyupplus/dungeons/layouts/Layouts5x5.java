package net.galgan.skyupplus.dungeons.layouts;

import java.util.Map;

import static net.galgan.skyupplus.dungeons.layouts.RoomData.*;

// 5x5 rooms: room(letter, doors), see RoomData.
// Door codes are from the sign's point of view, counted from 1 at the sign:
//   B<n> / T<n> = bottom / top wall, width tile n
//   L<n> / R<n> = left / right wall, length tile n
public final class Layouts5x5 {

    public static final Map<String, RoomType> ROOMS = Map.ofEntries(
            // Entrance in the middle of the left wall (tile 3 of 5)
            room("START", "L3")
    );

    private Layouts5x5() {}
}