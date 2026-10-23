package net.galgan.skyupplus.dungeons.layouts;

import java.util.Map;

import static net.galgan.skyupplus.dungeons.layouts.RoomData.*;

// 1x1 rooms: room(letter, doors), see RoomData.
// Door codes are from the sign's point of view, counted from 1 at the sign:
//   B<n> / T<n> = bottom / top wall, width tile n
//   L<n> / R<n> = left / right wall, length tile n
public final class Layouts1x1 {

    public static final Map<String, RoomType> ROOMS = Map.ofEntries(
            room("A", "T1"),
            room("B", "R1 T1"),
            room("C", "B1 R1 T1"),
            room("D", "B1 L1 R1 T1"),
            room("E", "B1 T1")
    );

    private Layouts1x1() {}
}