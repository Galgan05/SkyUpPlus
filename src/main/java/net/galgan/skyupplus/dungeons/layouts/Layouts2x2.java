package net.galgan.skyupplus.dungeons.layouts;

import java.util.Map;

import static net.galgan.skyupplus.dungeons.layouts.RoomData.*;

// 2x2 rooms: room(letter, doors, variants...), see RoomData.
// Door codes are from the sign's point of view, counted from 1 at the sign:
//   B<n> / T<n> = bottom / top wall, width tile n
//   L<n> / R<n> = left / right wall, length tile n
public final class Layouts2x2 {

    public static final Map<String, RoomType> ROOMS = Map.ofEntries(
            room("A", "B1 B2 T1 T2",
                    v(1).spawners(1).barrels(0).chests(1).pots(1),
                    v(2).spawners(1).barrels(2).chests(2).pots(4)),
            room("B", "B1 L1 R2 T2",
                    v(1).spawners(1).barrels(0).chests(0).pots(2),
                    v(2).spawners(1).barrels(5).chests(0).pots(10),
                    v(3).spawners(1).barrels(6).chests(4).pots(13)),
            room("C", "B1 L1 T1",
                    v(1).spawners(1).barrels(0).chests(0).pots(2),
                    v(2).spawners(1).barrels(5).chests(0).pots(5)),
            room("D", "L1 R2",
                    v(1).spawners(1).barrels(3).chests(0).pots(0),
                    v(2).spawners(0).barrels(3).chests(0).pots(6),
                    v(3).spawners(1).barrels(8).chests(0).pots(6),
                    v(4).spawners(1).barrels(1).chests(3).pots(35),
                    v(5).spawners(2).barrels(6).chests(0).pots(5)),
            room("E", "L2 R1",
                    v(1).spawners(1).barrels(0).chests(0).pots(1),
                    v(2).spawners(1).barrels(1).chests(0).pots(10),
                    v(3).spawners(2).barrels(16).chests(0).pots(10),
                    v(4).spawners(2).barrels(3).chests(1).pots(8),
                    v(5).spawners(2).barrels(13).chests(8).pots(10)),
            room("F", "T1 T2",
                    v(1).spawners(1).barrels(2).chests(0).pots(1),
                    v(3).spawners(2).barrels(10).chests(0).pots(17),
                    v(4).spawners(1).barrels(2).chests(0).pots(6),
                    v(5).spawners(1).barrels(5).chests(2).pots(4)),
            room("G", "T1",
                    v(1).spawners(1).barrels(0).chests(0).pots(0),
                    v(2).spawners(1).barrels(2).chests(0).pots(2)),
            room("H", "T2",
                    v(1).spawners(1).barrels(2).chests(0).pots(2),
                    v(2).spawners(1).barrels(0).chests(0).pots(6)),
            room("I", "L1 T2",
                    v(1).spawners(1).barrels(0).chests(0).pots(3),
                    v(2).spawners(1).barrels(6).chests(0).pots(3),
                    v(3).spawners(1).barrels(0).chests(2).pots(2)),
            room("J", "B1 T1",
                    v(1).spawners(1).barrels(2).chests(0).pots(1),
                    v(2).spawners(1).barrels(5).chests(0).pots(8),
                    v(3).spawners(1).barrels(2).chests(0).pots(4)),
            room("K", "B1 L1 R1 T1",
                    v(1).spawners(1).barrels(3).chests(0).pots(16),
                    v(2).spawners(1).barrels(2).chests(0).pots(8),
                    v(3).spawners(3).barrels(12).chests(4).pots(2)),
            room("L", "B1 R1 T2",
                    v(1).spawners(1).barrels(3).chests(0).pots(14),
                    v(2).spawners(1).barrels(8).chests(0).pots(11),
                    v(3).spawners(2).barrels(2).chests(0).pots(4)),
            room("M", "B2 L1 R2 T1",
                    v(1).spawners(1).barrels(1).chests(0).pots(4),
                    v(2).spawners(1).barrels(3).chests(0).pots(5)),
            room("N", "L1 T1",
                    v(1).spawners(1).barrels(2).chests(2).pots(6),
                    v(2).spawners(1).barrels(4).chests(0).pots(3),
                    v(3).spawners(1).barrels(10).chests(0).pots(9))
    );

    private Layouts2x2() {}
}