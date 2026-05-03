package settingdust.data_dumper.v20_1.util

import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import java.util.stream.Stream
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.resources.ResourceLocation
import settingdust.data_dumper.util.CommonIdentifier
import settingdust.data_dumper.util.MinecraftVersion
import settingdust.data_dumper.util.SuggestResourceProvider

class SuggestResourceProvider : SuggestResourceProvider {
    init {
        MinecraftVersion.V1201.requireCurrent()
    }

    override fun suggestResource(
        identifiers: Stream<CommonIdentifier>,
        builder: SuggestionsBuilder,
        prefix: String?,
    ): CompletableFuture<Suggestions> {
        @Suppress("UNCHECKED_CAST")
        val nativeStream = identifiers as Stream<ResourceLocation>
        return if (prefix != null) {
            SharedSuggestionProvider.suggestResource(nativeStream, builder, prefix)
        } else {
            SharedSuggestionProvider.suggestResource(nativeStream, builder)
        }
    }
}
