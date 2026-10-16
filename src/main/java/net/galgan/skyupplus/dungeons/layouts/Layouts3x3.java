package net.galgan.skyupplus.dungeons.layouts;

import java.util.Map;

import static net.galgan.skyupplus.dungeons.layouts.RoomData.*;

// 3x3 rooms: room(letter, doors, variants...), see RoomData.
// Door codes are from the sign's point of view, counted from 1 at the sign:
//   B<n> / T<n> = bottom / top wall, width tile n
//   L<n> / R<n> = left / right wall, length tile n
public final class Layouts3x3 {

    public static final Map<String, RoomType> ROOMS = Map.ofEntries(
            room("START", "L2",
                    v(1).spawners(0))
    );

    private Layouts3x3() {}
}