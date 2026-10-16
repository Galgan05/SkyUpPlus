package net.galgan.skyupplus.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;

public class Sound {

    public static void playSound(Minecraft mc, SoundEvent sound, SoundSource source, float pitch) {

        if (mc.player != null && mc.level != null) {

            mc.getSoundManager().play(
                    new SimpleSoundInstance(
                        sound.location(),
                        source,
                        1.0f,
                        pitch,
                        SoundInstance.createUnseededRandom(),
                        false,
                        0,
                        SimpleSoundInstance.Attenuation.NONE,
                        0.0, 0.0, 0.0,
                        true
            ));
        }
    }
}
