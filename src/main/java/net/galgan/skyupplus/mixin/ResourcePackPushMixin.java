package net.galgan.skyupplus.mixin;

import com.mojang.logging.LogUtils;
import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.resourcepack_saver.ServerPackCache;
import net.galgan.skyupplus.resourcepack_saver.ServerPackStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket.Action;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ClientCommonPacketListenerImpl.class)
public abstract class ResourcePackPushMixin {

    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();

    @Shadow @Final protected Minecraft minecraft;
    @Shadow @Final protected Connection connection;
    @Shadow @Final protected ServerData serverData;

    @Inject(method = "handleResourcePackPush", at = @At("HEAD"), cancellable = true)
    private void skipPackWeAlreadyHave(ClientboundResourcePackPushPacket packet, CallbackInfo ci) {
        // This method runs twice: first on the network thread, then on the game thread
        if (!minecraft.isSameThread()) {
            return;
        }

        if (!SUPConfig.get().main.automaticResourcePack) {
            return;
        }

        UUID packId = packet.id();
        String packHash = packet.hash();

        if (!ServerPackCache.isSkyUpPack(packId)) {
            return;
        }

        boolean serverPacksDisabled = serverData != null && serverData.getResourcePackStatus() == ServerData.ServerPackStatus.DISABLED;

        if (serverPacksDisabled) {
            return;
        }

        if (!ServerPackCache.alreadyHavePack(packHash)) {
            LOGGER.info("[SkyUP+] Resource pack is new or changed, downloading it (server: {}, loaded: {})",
                    packet.hash(), ServerPackStorage.getLoadedPackHash());
            ServerPackCache.waitingForDownload = true;
            ServerPackCache.expectedHash = packHash;
            return;
        }

        LOGGER.info("[SkyUP+] Resource pack unchanged, skipping download");
        sendStatus(packId, Action.ACCEPTED);
        sendStatus(packId, Action.DOWNLOADED);
        sendStatus(packId, Action.SUCCESSFULLY_LOADED);
        ci.cancel();
    }

    @Unique
    private void sendStatus(UUID packId, Action status) {
        connection.send(new ServerboundResourcePackPacket(packId, status));
    }
}
