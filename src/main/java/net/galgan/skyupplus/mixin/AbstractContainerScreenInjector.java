package net.galgan.skyupplus.mixin;

import net.galgan.skyupplus.config.SUPConfig;
import net.galgan.skyupplus.util.ServerRestrictor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenInjector {

    @Inject(
            method = "extractSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;item(Lnet/minecraft/world/item/ItemStack;III)V"
            )
    )
    private void drawStorageBackground(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        if (!SUPConfig.get().main.showFullStorages || !ServerRestrictor.isAllowed()) return;

        String title = ((Screen)(Object)this).getTitle().getString();
        if (!title.equals("\uE001\uE041\uE002Kontroler magazynów")) return;

        ItemStack item = slot.getItem();
        if (item.isEmpty() || !slot.isActive()) return;

        List<Component> siblings = item.getHoverName().getSiblings();

        if (siblings.size() < 4) return;
        if (!siblings.getFirst().getString().equals("» ")) return;

        TextColor color = siblings.get(1).getStyle().getColor();
        if (color == null) return;

        int x = slot.x;
        int y = slot.y;

        if (color.getValue() == 0xFF5555) {          // ChatFormatting.RED
            graphics.fill(x, y, x + 16, y + 16, 0xFFFF5555);
        } else if (color.getValue() == 0xFFAA00) {   // ChatFormatting.GOLD
            graphics.fill(x, y, x + 16, y + 16, 0xFFFFAA00);
        }
    }
}