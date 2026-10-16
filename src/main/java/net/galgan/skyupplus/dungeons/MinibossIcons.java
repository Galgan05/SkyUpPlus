package net.galgan.skyupplus.dungeons;

import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class MinibossIcons {

    private static final String MOD_ID = "skyupplus";

    // Part of the miniboss name -> icon in textures/gui/miniboss/. Checked from top to bottom.
    private static final List<Map.Entry<String, String>> ICON_FILES = List.of(
            Map.entry("hydron", "hydron.png"),
            Map.entry("czyken", "czyken.png"),
            Map.entry("boni", "boni_rotus.png"),
            Map.entry("rotuś", "boni_rotus.png"),
            Map.entry("chemonizy", "chemonizy.png"),
            Map.entry("nautenak", "nautenak.png")
    );

    private MinibossIcons() {}

    // The icon for a miniboss, or null if it doesn't have one
    public static Identifier getIcon(String name) {
        String lowerCaseName = name.toLowerCase(Locale.ROOT);

        for (Map.Entry<String, String> iconFile : ICON_FILES) {
            if (lowerCaseName.contains(iconFile.getKey())) {
                return Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/miniboss/" + iconFile.getValue());
            }
        }

        return null;
    }
}