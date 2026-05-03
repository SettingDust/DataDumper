package settingdust.data_dumper.v26_1.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldCallback;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContextMapper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.WorldLoader;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    @Shadow
    @Final
    private static Logger LOGGER;

    private CreateWorldScreenMixin(Component title) {
        super(title);
    }

    @Inject(
            method = "openCreateWorldScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;managedBlock(Ljava/util/function/BooleanSupplier;)V"
            ),
            cancellable = true
    )
    private static void datadumper$handleCrash(
            Minecraft client,
            Runnable onClose,
            Function<WorldLoader.DataLoadContext, WorldGenSettings> worldGenSettings,
            WorldCreationContextMapper par4,
            ResourceKey<WorldPreset> par5,
            CreateWorldCallback par6,
            CallbackInfo ci,
            @Local LocalRef<CompletableFuture<WorldCreationContext>> original
    ) {
        original.set(original.get().handle((input, throwable) -> {
            if (throwable != null) {
                LOGGER.warn("Failed to validate datapack", throwable);
                client.setScreen(null);
                ci.cancel();
            }
            return input;
        }));
    }
}
