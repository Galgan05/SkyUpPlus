package net.galgan.skyupplus.dungeons.layouts;

import java.util.Map;

import static net.galgan.skyupplus.dungeons.layouts.RoomData.*;

// 1x1 rooms: room(letter, doors, variants...), see RoomData.
// Door codes are from the sign's point of view, counted from 1 at the sign:
//   B<n> / T<n> = bottom / top wall, width tile n
//   L<n> / R<n> = left / right wall, length tile n
public final class Layouts1x1 {

    public static final Map<String, RoomType> ROOMS = Map.ofEntries(
            room("A", "T1",
                    v(1).spawners(0).barrels(0).chests(0).pots(0),
                    v(2).spawners(0).barrels(0).chests(1).pots(0),
                    v(3).spawners(0).barrels(1).chests(0).pots(0),
                    v(4).spawners(0).barrels(1).chests(1).pots(1),
                    v(5).spawners(0).barrels(1).chests(0).pots(1),
                    v(6).spawners(0).barrels(2).chests(0).pots(0),
                    v(7).spawners(0).barrels(1).chests(0).pots(0),
                    v(8).spawners(0).barrels(0).chests(1).pots(1),
                    v(9).spawners(0).barrels(0).chests(1).pots(0),
                    v(10).spawners(0).barrels(2).chests(0).pots(1),
                    v(11).spawners(0).barrels(0).chests(0).pots(9),
                    v(12).spawners(0).barrels(6).chests(2).pots(3)),
            room("B", "R1 T1",
                    v(1).spawners(0).barrels(0).chests(0).pots(0),
                    v(2).spawners(0).barrels(0).chests(0).pots(0),
                    v(3).spawners(0).barrels(0).chests(1).pots(0),
                    v(4).spawners(1).barrels(3).chests(5).pots(1)),
            room("C", "B1 R1 T1",
                    v(1).spawners(0).barrels(0).chests(1).pots(2),
                    v(2).spawners(0).barrels(0).chests(1).pots(0),
                    v(3).spawners(0).barrels(0).chests(0).pots(0),
                    v(4).spawners(0).barrels(4).chests(3).pots(7)),
            room("D", "B1 L1 R1 T1",
                    v(1).spawners(0).barrels(0).chests(0).pots(1),
                    v(2).spawners(0).barrels(0).chests(1).pots(0),
                    v(3).spawners(0).barrels(0).chests(0).pots(0),
                    v(4).spawners(0).barrels(0).chests(1).pots(2)),
            room("E", "B1 T1",
                    v(1).spawners(0).barrels(0).chests(0).pots(0),
                    v(2).spawners(0).barrels(0).chests(0).pots(0),
                    v(3).spawners(0).barrels(0).chests(1).pots(1),
                    v(4).spawners(0).barrels(0).chests(0).pots(0))
    );

    private Layouts1x1() {}
}