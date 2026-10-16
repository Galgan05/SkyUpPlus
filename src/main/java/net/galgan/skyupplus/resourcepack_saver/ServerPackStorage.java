package net.galgan.skyupplus.resourcepack_saver;

import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket.Action;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Stream;

// Saved packs live in config/skyupplus/resourcepack/pack-<sha1>.zip.
// Each version gets its own file, because the old one stays open until the next resource reload.
public class ServerPackStorage {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Path FOLDER = FabricLoader.getInstance().getConfigDir().resolve("skyupplus/resourcepack");

    private static String loadedPackHash = null;

    public static String getLoadedPackHash() {
        return loadedPackHash;
    }

    public static void setLoadedPackHash(String hash) {
        loadedPackHash = hash;
    }

    // --- Saved packs ---

    public static Path findNewestPack() {
        return newest(listSavedPacks());
    }

    public static void deleteOldPacks(Path packToKeep) {
        for (Path file : listSavedPacks()) {
            if (file.equals(packToKeep)) {
                continue;
            }

            try {
                Files.delete(file);
            } catch (IOException e) {
                // Still in use, it will be deleted on a later reload
            }
        }
    }

    // "pack-abc123.zip" -> "abc123"
    public static String getHash(Path packFile) {
        String name = packFile.getFileName().toString();
        return name.substring("pack-".length(), name.length() - ".zip".length());
    }

    private static List<Path> listSavedPacks() {
        List<Path> packs = new ArrayList<>();

        for (Path file : listFiles(FOLDER)) {
            String name = file.getFileName().toString();

            if (name.startsWith("pack-") && name.endsWith(".zip")) {
                packs.add(file);
            }
        }

        return packs;
    }

    // --- Copying a new pack ---

    public static void onPackStatusSent(Action status) {
        if (!ServerPackCache.waitingForDownload) {
            return;
        }

        // Still downloading or loading
        if (status == Action.ACCEPTED || status == Action.DOWNLOADED) {
            return;
        }

        ServerPackCache.waitingForDownload = false;

        if (status != Action.SUCCESSFULLY_LOADED) {
            LOGGER.warn("[SkyUP+] Resource pack was not loaded ({}), not saving it", status);
            return;
        }

        String expectedHash = ServerPackCache.expectedHash;
        Path downloadsFolder = Minecraft.getInstance().gameDirectory.toPath().resolve("downloads");

        Thread copyThread = new Thread(() -> copyPack(downloadsFolder, expectedHash), "SkyUP+ pack copy");
        copyThread.setDaemon(true);
        copyThread.start();
    }

    private static void copyPack(Path downloadsFolder, String expectedHash) {
        Path packFolder = downloadsFolder.resolve(ServerPackCache.SKYUP_PACK_ID.toString());
        Path downloadedFile = findDownloadedFile(packFolder, expectedHash);

        if (downloadedFile == null) {
            LOGGER.error("[SkyUP+] Could not find the downloaded resource pack in {}", packFolder);
            return;
        }

        // Copy to a temp file first, so a half-finished copy is never loaded as a pack
        Path tempFile = FOLDER.resolve("copying.tmp");

        try {
            Files.createDirectories(FOLDER);
            Files.copy(downloadedFile, tempFile, StandardCopyOption.REPLACE_EXISTING);

            String hash = sha1(tempFile);

            boolean serverSentHash = expectedHash != null && !expectedHash.isEmpty();
            if (serverSentHash && !hash.equalsIgnoreCase(expectedHash)) {
                LOGGER.error("[SkyUP+] Copied pack has the wrong hash (expected {}, got {})", expectedHash, hash);
                Files.delete(tempFile);
                return;
            }

            Path packFile = FOLDER.resolve("pack-" + hash + ".zip");
            Files.move(tempFile, packFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            LOGGER.info("[SkyUP+] Saved resource pack as {}", packFile.getFileName());

        } catch (IOException e) {
            LOGGER.error("[SkyUP+] Failed to save the resource pack", e);
        }
    }

    // Minecraft names downloaded packs after their SHA-1 hash
    private static Path findDownloadedFile(Path packFolder, String expectedHash) {
        if (expectedHash != null && !expectedHash.isEmpty()) {
            Path file = packFolder.resolve(expectedHash.toLowerCase());

            if (Files.isRegularFile(file)) {
                return file;
            }
        }

        return newest(listFiles(packFolder));
    }

    // --- Helpers ---

    private static List<Path> listFiles(Path folder) {
        if (!Files.isDirectory(folder)) {
            return List.of();
        }

        try (Stream<Path> files = Files.list(folder)) {
            return files.filter(Files::isRegularFile).toList();
        } catch (IOException e) {
            LOGGER.error("[SkyUP+] Could not list files in {}", folder, e);
            return List.of();
        }
    }

    private static Path newest(List<Path> files) {
        Path newest = null;

        for (Path file : files) {
            if (newest == null || lastModified(file) > lastModified(newest)) {
                newest = file;
            }
        }

        return newest;
    }

    private static long lastModified(Path file) {
        try {
            return Files.getLastModifiedTime(file).toMillis();
        } catch (IOException e) {
            return 0;
        }
    }

    private static String sha1(Path file) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }

        try (InputStream input = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        return HexFormat.of().formatHex(digest.digest());
    }
}
