package net.galgan.skyupplus.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.notenoughupdates.moulconfig.gui.CloseEventListener;
import io.github.notenoughupdates.moulconfig.gui.GuiContext;
import io.github.notenoughupdates.moulconfig.gui.GuiElementComponent;
import io.github.notenoughupdates.moulconfig.platform.MoulConfigScreenComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            GuiContext context = new GuiContext(new GuiElementComponent(SUPConfig.createEditor()));

            return new MoulConfigScreenComponent(Component.literal("SkyUP+"), context, parent) {
                @Override
                public void onClose() {
                    if (getGuiContext().onBeforeClose() == CloseEventListener.CloseAction.NO_OBJECTIONS_TO_CLOSE) {
                        Minecraft.getInstance().gui.setScreen(parent);
                    }
                }
            };
        };
    }
}