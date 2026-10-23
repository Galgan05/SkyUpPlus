package net.galgan.skyupplus.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.galgan.skyupplus.resourcepack_saver.ServerPackCache;
import net.galgan.skyupplus.resourcepack_saver.SkyUpPackSource;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {

    @Shadow @Final @Mutable
    private Set<RepositorySource> sources;

    // Adds our saved SkyUP pack as a source, but only to the client resource pack repository (not data packs)
    @Inject(method = "<init>", at = @At("RETURN"))
    private void skyupplus$addSavedPackSource(RepositorySource[] originalSources, CallbackInfo ci) {
        boolean isClientResources = Arrays.stream(originalSources).anyMatch(source -> source instanceof ClientPackSource);

        if (!isClientResources) {
            return;
        }

        Set<RepositorySource> withOurs = new LinkedHashSet<>(this.sources);
        withOurs.add(new SkyUpPackSource());
        this.sources = withOurs;
    }

    // When the server pushes a new pack version while we're connected, Minecraft loads its own copy.
    // Our saved copy would sit on top of it and hide the update, so we move ours just below theirs.
    @ModifyReturnValue(method = "rebuildSelected", at = @At("RETURN"))
    private List<Pack> skyupplus$putServerCopyOnTop(List<Pack> selected) {
        String uuid = ServerPackCache.SKYUP_PACK_ID.toString();

        int ours = -1;
        int theirs = -1;

        for (int i = 0; i < selected.size(); i++) {
            String id = selected.get(i).getId();

            if (id.equals("skyupplus_server_pack")) {
                ours = i;
            } else if (id.contains(uuid)) {
                theirs = i;
            }
        }

        // Later in the list = higher priority. Only change something if ours is above theirs.
        if (ours == -1 || theirs == -1 || ours < theirs) {
            return selected;
        }

        List<Pack> reordered = new ArrayList<>(selected);
        Pack ourPack = reordered.remove(ours);
        reordered.add(theirs, ourPack);

        return List.copyOf(reordered);
    }
}