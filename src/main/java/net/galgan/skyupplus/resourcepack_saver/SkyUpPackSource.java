package net.galgan.skyupplus.resourcepack_saver;

import com.mojang.logging.LogUtils;
import net.galgan.skyupplus.config.SUPConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

// Adds our saved SkyUP pack to the resource pack list. Runs at game start and on every resource reload.
public class SkyUpPackSource implements RepositorySource {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void loadPacks(Consumer<Pack> addPack) {
        LOGGER.info("[SkyUP+] Pack source running: enabled={}, newest saved pack={}",
                SUPConfig.get().main.automaticResourcePack, ServerPackStorage.findNewestPack());

        ServerPackStorage.setLoadedPackHash(null);

        if (!SUPConfig.get().main.automaticResourcePack) {
            return;
        }

        Path packFile = ServerPackStorage.findNewestPack();
        if (packFile == null) {
            return;
        }

        PackLocationInfo info = new PackLocationInfo(
                "skyupplus_server_pack",
                Component.literal("SkyUP Resource Pack"),
                PackSource.SERVER,
                Optional.empty()
        );

        // Always enabled, on top of the other packs, can't be moved - like a server pack
        PackSelectionConfig selection = new PackSelectionConfig(true, Pack.Position.TOP, true);

        Pack pack = Pack.readMetaAndCreate(
                info,
                new FilePackResources.FileResourcesSupplier(packFile),
                PackType.CLIENT_RESOURCES,
                selection
        );

        if (pack == null) {
            LOGGER.error("[SkyUP+] Could not load the saved resource pack {}", packFile);
            return;
        }

        addPack.accept(pack);
        ServerPackStorage.setLoadedPackHash(ServerPackStorage.getHash(packFile));
        ServerPackStorage.deleteOldPacks(packFile);
    }
}
