package settingdust.data_dumper.v26_1.mixin;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Decoder;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import settingdust.data_dumper.RegistryDumper;
import settingdust.data_dumper.util.CommonIdentifier;

@Mixin(RegistryLoadTask.PendingRegistration.class)
public class RegistryLoadTaskPendingRegistrationMixin {
    @WrapOperation(
            method = "lambda$findAndLoadFromResource$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/RegistryLoadTask$PendingRegistration;loadFromResource(Lcom/mojang/serialization/Decoder;Lnet/minecraft/resources/RegistryOps;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/server/packs/resources/Resource;)Lcom/mojang/datafixers/util/Either;"))
    private static Either<?, Exception> datadumper$dumpFailed(
            Decoder<?> elementDecoder,
            RegistryOps<JsonElement> ops,
            ResourceKey<?> elementKey,
            Resource thunk,
            Operation<Either<?, Exception>> original) {
        try {
            return original.call(elementDecoder, ops, elementKey, thunk);
        } catch (Throwable throwable) {
            RegistryDumper.dumpFailedEntry((CommonIdentifier) (Object) elementKey.identifier(), thunk);
            throw throwable;
        }
    }
}
