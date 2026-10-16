package net.galgan.skyupplus.mixin;

import net.galgan.skyupplus.features.Abilities;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ActionBarInjector {

    @Inject(method = "setActionBarText", at = @At("TAIL"))
    private void getActionBar(ClientboundSetActionBarTextPacket packet, CallbackInfo ci) {
        Abilities.bossBarMessage = packet.text();
    }
}