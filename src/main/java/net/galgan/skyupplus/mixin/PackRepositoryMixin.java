package net.galgan.skyupplus.mixin;

import net.galgan.skyupplus.resourcepack_saver.SkyUpPackSource;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;
import java.util.Set;

@Mixin(PackRepository.class)
public abstract class PackRepositoryMixin {

    @Shadow @Final @Mutable private Set<RepositorySource> sources;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addSkyUpPackSource(RepositorySource[] sources, CallbackInfo ci) {
        // PackRepository is also used for data packs. Only the resource pack one has ClientPackSource.
        boolean isResourcePackRepository = false;
        for (RepositorySource source : this.sources) {
            if (source instanceof ClientPackSource) {
                isResourcePackRepository = true;
                break;
            }
        }

        if (!isResourcePackRepository) {
            return;
        }

        Set<RepositorySource> newSources = new LinkedHashSet<>(this.sources);
        newSources.add(new SkyUpPackSource());
        this.sources = newSources;
    }
}
