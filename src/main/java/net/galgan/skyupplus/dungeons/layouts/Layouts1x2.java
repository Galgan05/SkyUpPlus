package net.galgan.skyupplus.dungeons.layouts;

import java.util.Map;

import static net.galgan.skyupplus.dungeons.layouts.RoomData.*;

// 1x2 rooms: room(letter, doors, variants...), see RoomData.
// Door codes are from the sign's point of view, counted from 1 at the sign:
//   B<n> / T<n> = bottom / top wall, width tile n
//   L<n> / R<n> = left / right wall, length tile n
public final class Layouts1x2 {

    public static final Map<String, RoomType> ROOMS = Map.ofEntries(
            room("A", "B1 T1",
                    v(1).spawners(1).barrels(0).chests(0).pots(0),
                    v(2).spawners(0).barrels(3).chests(0).pots(4),
                    v(3).spawners(0).barrels(0).chests(0).pots(4),
                    v(4).spawners(1).barrels(7).chests(6).pots(5)),
            room("B", "L1 L2",
                    v(1).spawners(1).barrels(0).chests(0).pots(0),
                    v(2).spawners(0).barrels(0).chests(1).pots(1),
                    v(3).spawners(0).barrels(0).chests(0).pots(10),
                    v(4).spawners(1).barrels(8).chests(3).pots(7)),
            room("C", "B1 L1 R2 T1",
                    v(1).spawners(0).barrels(0).chests(0).pots(2),
                    v(2).spawners(0).barrels(0).chests(0).pots(0),
                    v(3).spawners(0).barrels(5).chests(0).pots(11)),
            room("D", "L1 R2",
                    v(1).spawners(1).barrels(10).chests(4).pots(5),
                    v(2).spawners(2).barrels(7).chests(3).pots(5),
                    v(3).spawners(1).barrels(1).chests(1).pots(3)),
            room("E", "L2 R1",
                    v(1).spawners(1).barrels(2).chests(0).pots(3),
                    v(2).spawners(1).barrels(5).chests(0).pots(5))
    );

    private Layouts1x2() {}
}