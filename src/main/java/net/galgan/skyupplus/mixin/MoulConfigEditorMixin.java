package net.galgan.skyupplus.mixin;

import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = MoulConfigEditor.class, remap = false)
public class MoulConfigEditorMixin {
    @ModifyConstant(method = "render", constant = @Constant(stringValue = "Categories"), remap = false)
    private String renameCategories(String original) {
        return "Kategorie";
    }
}