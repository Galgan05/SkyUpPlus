package net.galgan.skyupplus.mixin;

import net.galgan.skyupplus.resourcepack_saver.ServerPackCache;
import net.galgan.skyupplus.resourcepack_saver.ServerPackStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class ConnectionSendMixin {

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"))
    private void watchResourcePackStatus(Packet<?> packet, CallbackInfo ci) {
        if (!(packet instanceof ServerboundResourcePackPacket(java.util.UUID id, ServerboundResourcePackPacket.Action action))) {
            return;
        }

        if (!ServerPackCache.isSkyUpPack(id)) {
            return;
        }

        Minecraft.getInstance().execute(() -> ServerPackStorage.onPackStatusSent(action));
    }
}
