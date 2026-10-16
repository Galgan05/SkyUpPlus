package net.galgan.skyupplus.mixin;

import net.galgan.skyupplus.config.SUPConfig;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Mixin(BossHealthOverlay.class)
public abstract class HideBossBar {

    @Unique
    private static final String[] ABILITY_NAMES = {
            "\uF011 Wiertło",
            "\uF012 Piła",
            "\uF013 Pług",
            "\uF014 Nawałnica",
            "\uF015 Rozbiórka",
            "\uF016 Sieci rybackie",
            "\uF017 Eskalacja"
    };

    @Unique
    private static boolean shouldHide(BossEvent bar) {
        if (!SUPConfig.get().abilities.customAbilities) return false;

        String name = bar.getName().getString();
        for (String prefix : ABILITY_NAMES) {
            if (name.startsWith(prefix)) return true;
        }
        return false;
    }

    @Redirect(
            method = "extractRenderState",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"),
            require = 1
    )

    private Collection<?> filterValues(Map<?, ?> map) {
        var list = new ArrayList<>(map.values());
        list.removeIf(v -> v instanceof BossEvent bar && shouldHide(bar));
        return list;
    }

}