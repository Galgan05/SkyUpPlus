package net.galgan.skyupplus.dungeons;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignTextSlot;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Finds the hidden signs that mark every room of the dungeon
public final class SignScanner {

    private static final String DUNGEON_DIMENSION = "minecraft:dungeon_zwykly";
    private static final String SIGN_MARKER = "<<DUNGEON_TC>>";
    private static final int SIGN_Y = 20;

    // line3 is the room label, e.g. "1x2-A-3"
    public record SignEntry(int x, int y, int z, int rotation, String line3) {}

    // Keyed by position, so loading the same chunk again doesn't add duplicates
    private static final Map<BlockPos, SignEntry> SIGNS = new LinkedHashMap<>();

    // Goes up every time SIGNS changes, so the map only rebuilds when needed
    private static int version;

    private static boolean wasInDungeon;

    private SignScanner() {}

    public static void register() {
        ClientChunkEvents.CHUNK_LOAD.register(SignScanner::scanChunk);
        ClientTickEvents.END_CLIENT_TICK.register(SignScanner::tick);
    }

    public static boolean isInDungeon(ClientLevel level) {
        return level.dimension().identifier().toString().equals(DUNGEON_DIMENSION);
    }

    public static int getVersion() {
        return version;
    }

    public static Collection<SignEntry> getSigns() {
        return Collections.unmodifiableCollection(SIGNS.values());
    }

    private static boolean isEnabled() {
        return ServerRestrictor.isAllowed() && SUPConfig.get().dungeons.dungeonMap.dungeonMapToggle;
    }

    // Forgets the signs as soon as the player leaves the dungeon.
    // Runs even when the map is off, so signs from an old dungeon never carry over to the next one.
    private static void tick(Minecraft client) {
        boolean inDungeon = client.level != null && isInDungeon(client.level);

        if (wasInDungeon && !inDungeon) {
            SIGNS.clear();
            version++;
        }

        wasInDungeon = inDungeon;
    }

    private static void scanChunk(ClientLevel level, LevelChunk chunk) {
        if (!isEnabled()) {
            return;
        }

        if (!isInDungeon(level)) {
            return;
        }

        for (Map.Entry<BlockPos, BlockEntity> blockEntity : chunk.getBlockEntities().entrySet()) {
            BlockPos pos = blockEntity.getKey();

            if (pos.getY() != SIGN_Y) {
                continue;
            }

            if (!(blockEntity.getValue() instanceof SignBlockEntity sign)) {
                continue;
            }

            BlockState state = sign.getBlockState();
            if (!state.is(Blocks.OAK_SIGN)) {
                continue;
            }

            List<Component> lines = sign.getText(SignTextSlot.FRONT).getMessages(false);
            if (!lines.getFirst().getString().equals(SIGN_MARKER)) {
                continue;
            }

            int rotation = state.getValue(BlockStateProperties.ROTATION_16);
            String label = lines.get(2).getString();
            SignEntry newSign = new SignEntry(pos.getX(), pos.getY(), pos.getZ(), rotation, label);

            SignEntry oldSign = SIGNS.put(pos, newSign);
            if (!newSign.equals(oldSign)) {
                version++;
            }
        }
    }
}