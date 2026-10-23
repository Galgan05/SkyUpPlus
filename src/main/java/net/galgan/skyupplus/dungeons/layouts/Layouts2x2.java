package net.galgan.skyupplus.dungeons.layouts;

import java.util.Map;

import static net.galgan.skyupplus.dungeons.layouts.RoomData.*;

// 2x2 rooms: room(letter, doors), see RoomData.
// Door codes are from the sign's point of view, counted from 1 at the sign:
//   B<n> / T<n> = bottom / top wall, width tile n
//   L<n> / R<n> = left / right wall, length tile n
public final class Layouts2x2 {

    public static final Map<String, RoomType> ROOMS = Map.ofEntries(
            room("A", "B1 B2 T1 T2"),
            room("B", "B1 L1 R2 T2"),
            room("C", "B1 L1 T1"),
            room("D", "L1 R2"),
            room("E", "L2 R1"),
            room("F", "T1 T2"),
            room("G", "T1"),
            room("H", "T2"),
            room("I", "L1 T2"),
            room("J", "B1 T1"),
            room("K", "B1 L1 R1 T1"),
            room("L", "B1 R1 T2"),
            room("M", "B2 L1 R2 T1"),
            room("N", "L1 T1")
    );

    private Layouts2x2() {}
}