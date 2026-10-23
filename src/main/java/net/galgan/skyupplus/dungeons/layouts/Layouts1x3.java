package net.galgan.skyupplus.dungeons.layouts;

import java.util.Map;

import static net.galgan.skyupplus.dungeons.layouts.RoomData.*;

// 1x3 rooms: room(letter, doors), see RoomData.
// Door codes are from the sign's point of view, counted from 1 at the sign:
//   B<n> / T<n> = bottom / top wall, width tile n
//   L<n> / R<n> = left / right wall, length tile n
public final class Layouts1x3 {

    public static final Map<String, RoomType> ROOMS = Map.ofEntries(
            room("A", "B1 T1"),
            room("B", "B1 L2 R2 T1"),
            room("C", "L3 R1"),
            room("D", "B1 L3 R1 T1"),
            room("E", "L1 L3 R2"),
            room("F", "L1 R3"),
            room("G", "B1 L2 R3")
    );

    private Layouts1x3() {}
}