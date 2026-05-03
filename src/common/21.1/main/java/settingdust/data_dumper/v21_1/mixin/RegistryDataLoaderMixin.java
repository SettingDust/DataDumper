package settingdust.data_dumper.v21_1.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Decoder;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import settingdust.data_dumper.RegistryDumper;
import settingdust.data_dumper.util.CommonIdentifier;

import java.util.Map;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {
    @Inject(
            method = "loadContentsFromManager",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private static <E> void datadumper$dumpFailed(
            ResourceManager par1,
            RegistryOps.RegistryInfoLookup par2,
            WritableRegistry<E> par3,
            Decoder<E> par4,
            Map<ResourceKey<?>, Exception> par5,
            CallbackInfo ci,
            @Local ResourceLocation identifier,
            @Local Resource resource
    ) {
        RegistryDumper.dumpFailedEntry((CommonIdentifier) (Object) identifier, resource);
    }
}
