package net.galgan.skyupplus.mixin;

import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class CancelSound {

    @Inject(method = "handleSoundEvent", at = @At("HEAD"), cancellable = true)
    private void cancelSound(ClientboundSoundPacket packet, CallbackInfo ci) {
        if(!ServerRestrictor.isAllowed()) return;

        if(SUPConfig.get().chat.disableAnnouncements) {
            SoundEvent sound = packet.getSound().value();

            if (sound.location().toString().equals("minecraft:block.note_block.pling") && packet.getSource() == SoundSource.MASTER && Math.abs(packet.getPitch() - 2.0f) < 0.001f) {
                ci.cancel();
            }
        }

        if(SUPConfig.get().chat.disableSpawnerSound) {
            SoundEvent sound = packet.getSound().value();

            if (sound.location().toString().equals("minecraft:block.trial_spawner.spawn_mob") && packet.getSource() == SoundSource.MASTER) {
                ci.cancel();
            }
        }

    }
}
