package settingdust.data_dumper.util

import com.google.common.collect.Streams
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import java.util.stream.Stream

/**
 * SPI interface for abstracting `SharedSuggestionProvider.suggestResource` across MC versions.
 * The method name and signature may differ between versions (e.g. `SharedSuggestionProvider`
 * vs `CommandSource`, `suggestResource` vs `suggestIdentifiers`).
 */
interface SuggestResourceProvider {
    companion object : SuggestResourceProvider by ServiceLoaderUtil.findService() {
        /**
         * Convenience overload accepting an [Iterable] instead of a [Stream].
         */
        fun suggestResource(
            identifiers: Iterable<CommonIdentifier>,
            builder: SuggestionsBuilder,
            prefix: String? = null,
        ): CompletableFuture<Suggestions> = suggestResource(
            Streams.stream(identifiers),
            builder,
            prefix,
        )
    }

    /**
     * Suggests resource identifiers, abstracting over version-specific
     * `SharedSuggestionProvider.suggestResource` / `CommandSource.suggestIdentifiers`.
     *
     * @param identifiers stream of identifiers to suggest
     * @param builder the suggestions builder
     * @param prefix optional prefix to prepend (e.g. "#" for tags)
     * @return a future of suggestions
     */
    fun suggestResource(
        identifiers: Stream<CommonIdentifier>,
        builder: SuggestionsBuilder,
        prefix: String? = null,
    ): CompletableFuture<Suggestions>
}
