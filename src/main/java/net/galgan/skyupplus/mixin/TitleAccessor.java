package net.galgan.skyupplus.mixin;

import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Hud.class)
public interface TitleAccessor {

    @Accessor("title")
    Component getTitle();

    @Accessor("subtitle")
    Component getSubtitle();
}

