package settingdust.datadumper

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import java.util.regex.Pattern
import kotlin.jvm.optionals.getOrNull
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.registry.RegistryKey
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class RegexArgumentType : ArgumentType<Pattern> {
    override fun parse(reader: StringReader): Pattern {
        return Pattern.compile(reader.readQuotedString())
    }
}

class TagArgumentType : IdentifierArgumentType() {
    companion object {
        private val NOT_TAG_EXCEPTION =
            SimpleCommandExceptionType(Text.translatable("datadumper.message.invalidTag"))
    }

    override fun parse(reader: StringReader): Identifier {
        if (reader.canRead() && reader.peek() == '#') {
            reader.skip()
            return super.parse(reader)
        }
        throw NOT_TAG_EXCEPTION.create()
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val source = context.source
        require(source is CommandSource)
        val registryKey =
            RegistryKey.ofRegistry<Any>(context.getArgument("key", Identifier::class.java))
        val registry =
            source.registryManager.getOptional(registryKey).getOrNull()
                ?: if (source is ServerCommandSource) {
                    source.world.registryManager.getOptional(registryKey).getOrNull()
                } else {
                    (source as FabricClientCommandSource)
                        .world
                        .registryManager
                        .getOptional(registryKey)
                        .getOrNull()
                }
                ?: return Suggestions.empty()
        return CommandSource.suggestIdentifiers(
            registry.streamTags().map { it.id },
            builder,
            "#",
        )
    }
}
