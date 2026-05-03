package settingdust.data_dumper

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import java.util.regex.Pattern
import kotlin.jvm.optionals.getOrNull
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.Component
import settingdust.data_dumper.util.ArgumentTypeAdapter
import settingdust.data_dumper.util.CommonIdentifier
import settingdust.data_dumper.util.RegistryAccessAdapter.Companion.lookupRegistry
import settingdust.data_dumper.util.RegistryAdapter.Companion.getTagIds
import settingdust.data_dumper.util.ResourceKeyAdapter
import settingdust.data_dumper.util.SuggestResourceProvider

class RegexArgumentType : ArgumentType<Pattern> {
    override fun parse(reader: StringReader): Pattern {
        return Pattern.compile(reader.readQuotedString())
    }
}

class TagArgumentType : ArgumentType<CommonIdentifier> {
    companion object {
        private val NOT_TAG_EXCEPTION =
            SimpleCommandExceptionType(Component.translatable("datadumper.message.invalidTag"))
    }

    override fun parse(reader: StringReader): CommonIdentifier {
        if (reader.canRead() && reader.peek() == '#') {
            reader.skip()
            return ArgumentTypeAdapter.identifier().parse(reader)
        }
        throw NOT_TAG_EXCEPTION.create()
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val source = context.source
        require(source is SharedSuggestionProvider)
        val registryKey =
            ResourceKeyAdapter.createRegistryKey<Any>(
                context.getArgument(
                    "key",
                    CommonIdentifier::class.java,
                ),
            )
        val registry =
            source.registryAccess().lookupRegistry(registryKey).getOrNull()
                ?: if (source is CommandSourceStack) {
                    source.level.registryAccess().lookupRegistry(registryKey).getOrNull()
                } else {
                    null
                }
                ?: return Suggestions.empty()
        val tagIds = registry.getTagIds()
        return SuggestResourceProvider.suggestResource(tagIds, builder, "#")
    }
}
