package net.galgan.skyupplus.dungeons.layouts;

import java.util.Map;

import static net.galgan.skyupplus.dungeons.layouts.RoomData.*;

// 1x3 rooms: room(letter, doors, variants...), see RoomData.
// Door codes are from the sign's point of view, counted from 1 at the sign:
//   B<n> / T<n> = bottom / top wall, width tile n
//   L<n> / R<n> = left / right wall, length tile n
public final class Layouts1x3 {

    public static final Map<String, RoomType> ROOMS = Map.ofEntries(
            room("A", "B1 T1",
                    v(1).spawners(1).barrels(0).chests(0).pots(0),
                    v(2).spawners(1).barrels(0).chests(1).pots(0),
                    v(3).spawners(1).barrels(1).chests(0).pots(9)),
            room("B", "B1 L2 R2 T1",
                    v(1).spawners(0).barrels(0).chests(0).pots(1),
                    v(2).spawners(1).barrels(0).chests(2).pots(0)),
            room("C", "L3 R1",
                    v(1).spawners(1).barrels(0).chests(0).pots(2),
                    v(2).spawners(1).barrels(2).chests(0).pots(10)),
            room("D", "B1 L3 R1 T1",
                    v(1).spawners(1).barrels(2).chests(0).pots(2)),
            room("E", "L1 L3 R2",
                    v(1).spawners(0).barrels(2).chests(0).pots(2)),
            room("F", "L1 R3",
                    v(1).spawners(2).barrels(13).chests(2).pots(3)),
            room("G", "B1 L2 R3",
                    v(1).spawners(1).barrels(6).chests(3).pots(9))
    );

    private Layouts1x3() {}
}