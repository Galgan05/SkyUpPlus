package net.galgan.skyupplus.resourcepack_saver;

import java.util.UUID;

public class ServerPackCache {

    public static final UUID SKYUP_PACK_ID = UUID.fromString("3f1cc76c-427e-3a38-934a-7b15e22654db");

    // Set while Minecraft downloads a new version, so we copy it once it's loaded
    public static boolean waitingForDownload = false;
    public static String expectedHash = "";

    public static boolean isSkyUpPack(UUID packId) {
        return packId.equals(SKYUP_PACK_ID);
    }

    public static boolean alreadyHavePack(String serverHash) {
        String loadedHash = ServerPackStorage.getLoadedPackHash();

        if (loadedHash == null) {
            return false;
        }

        // Without a hash from the server we can't tell if the pack changed
        if (serverHash == null || serverHash.isEmpty()) {
            return true;
        }

        return loadedHash.equalsIgnoreCase(serverHash);
    }
}
