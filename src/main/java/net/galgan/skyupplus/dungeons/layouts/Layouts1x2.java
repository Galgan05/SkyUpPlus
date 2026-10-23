package net.galgan.skyupplus.dungeons.layouts;

import java.util.Map;

import static net.galgan.skyupplus.dungeons.layouts.RoomData.*;

// 1x2 rooms: room(letter, doors), see RoomData.
// Door codes are from the sign's point of view, counted from 1 at the sign:
//   B<n> / T<n> = bottom / top wall, width tile n
//   L<n> / R<n> = left / right wall, length tile n
public final class Layouts1x2 {

    public static final Map<String, RoomType> ROOMS = Map.ofEntries(
            room("A", "B1 T1"),
            room("B", "L1 L2"),
            room("C", "B1 L1 R2 T1"),
            room("D", "L1 R2"),
            room("E", "L2 R1")
    );

    private Layouts1x2() {}
}