package settingdust.datadumper.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Decoder;
import net.minecraft.registry.*;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import settingdust.datadumper.EntrypointKt;

import java.util.Map;

@Mixin(RegistryLoader.class)
public class RegistryLoaderMixin {


    @Inject(
        method = "load(Lnet/minecraft/registry/RegistryOps$RegistryInfoGetter;" +
                 "Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/RegistryKey;" +
                 "Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
        )
    )
    private static <E> void datadumper$dumpFailed(
        final RegistryOps.RegistryInfoGetter registryInfoGetter,
        final ResourceManager resourceManager,
        final RegistryKey<? extends Registry<E>> registryRef,
        final MutableRegistry<E> newRegistry,
        final Decoder<E> decoder,
        final Map<RegistryKey<?>, Exception> exceptions,
        final CallbackInfo ci,
        @Local Identifier identifier,
        @Local Resource resource
    ) {
        EntrypointKt.dumpFailedEntry(identifier, resource);
    }
}
