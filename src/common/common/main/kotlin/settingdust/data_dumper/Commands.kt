package settingdust.data_dumper

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.serialization.Codec
import java.util.regex.Pattern
import kotlin.streams.asSequence
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier

/**
 * Builds the datadumper command tree.
 *
 * @param permissionChecker checks if a source has a given permission (permission string, level) -> boolean
 * @param dynamicRegistryKeys provides identifiers of all dynamic registries (for entry command suggestions)
 * @param dynamicRegistryCodec provides the codec for a given dynamic registry key (for entry serialization)
 */
fun buildCommands(
    permissionChecker: (ServerCommandSource, String, Int) -> Boolean,
    dynamicRegistryKeys: () -> Iterable<Identifier>,
    dynamicRegistryCodec: (RegistryKey<out Registry<*>>) -> Codec<Any>?
): LiteralArgumentBuilder<ServerCommandSource> {
    val registryTag =
        argument("tag", TagArgumentType()).executes { context ->
            val registryKey =
                RegistryKey.ofRegistry<Any>(context.getArgument("key", Identifier::class.java))
            val registry = context.source.registryManager.get(registryKey)

            val keys =
                registry
                    .getOrCreateEntryList(
                        TagKey.of(
                            registryKey,
                            context.getArgument("tag", Identifier::class.java),
                        ),
                    )
                    .map { it.key.orElseThrow() }
            RegistryDumper.sendRegistries(context, keys)

            keys.size
        }
    val registryRegex =
        argument("regex", RegexArgumentType()).executes { context ->
            val registryKey =
                RegistryKey.ofRegistry<Any>(context.getArgument("key", Identifier::class.java))
            val registry = context.source.registryManager.get(registryKey)

            val pattern = context.getArgument("regex", Pattern::class.java)
            val keys =
                registry
                    .streamEntries()
                    .asSequence()
                    .map { it.key.orElseThrow() }
                    .filter { pattern.matcher(it.value.toString()).find() }
                    .toList()

            RegistryDumper.sendRegistries(context, keys)

            keys.size
        }
    val registry =
        literal("registry")
            .then(
                argument("key", IdentifierArgumentType.identifier())
                    .suggests { context, builder ->
                        CommandSource.suggestIdentifiers(
                            context.source.registryManager.streamAllRegistries().map {
                                it.key.value
                            },
                            builder,
                        )
                    }
                    .executes { context ->
                        val registry =
                            RegistryKey.ofRegistry<Any>(
                                context.getArgument("key", Identifier::class.java),
                            )
                        val keys = context.source.registryManager.get(registry).keys
                        RegistryDumper.sendRegistries(context, keys.sortedBy { it.value.toString() })
                        keys.size
                    }
                    .then(registryTag)
                    .then(registryRegex),
            )

    val entryTag =
        argument("tag", TagArgumentType()).executes { context ->
            val registryKey =
                RegistryKey.ofRegistry<Any>(context.getArgument("key", Identifier::class.java))
            val registry = context.source.registryManager.get(registryKey)

            val entryList =
                registry.getOrCreateEntryList(
                    TagKey.of(registryKey, context.getArgument("tag", Identifier::class.java)),
                )

            RegistryDumper.dumpEntries(entryList, registryKey, context.source.registryManager, dynamicRegistryCodec)

            val keys = entryList.map { it.key.orElseThrow() }
            RegistryDumper.sendRegistries(context, keys)

            keys.size
        }
    val entryRegex =
        argument("regex", RegexArgumentType()).executes { context ->
            val registryKey =
                RegistryKey.ofRegistry<Any>(context.getArgument("key", Identifier::class.java))
            val registry = context.source.registryManager.get(registryKey)

            val pattern = context.getArgument("regex", Pattern::class.java)
            val entries =
                registry
                    .streamEntries()
                    .asSequence()
                    .filter { pattern.matcher(it.key.orElseThrow().value.toString()).find() }
                    .toList()

            RegistryDumper.dumpEntries(entries, registryKey, context.source.registryManager, dynamicRegistryCodec)

            RegistryDumper.sendRegistries(context, entries.map { it.key.orElseThrow() })

            entries.size
        }
    val entry =
        literal("entry")
            .then(
                argument("key", IdentifierArgumentType.identifier())
                    .suggests { _, builder ->
                        CommandSource.suggestIdentifiers(
                            dynamicRegistryKeys(),
                            builder,
                        )
                    }
                    .executes { context ->
                        val registryKey =
                            RegistryKey.ofRegistry<Any>(
                                context.getArgument("key", Identifier::class.java),
                            )
                        val registry = context.source.registryManager.get(registryKey)

                        @Suppress("UNCHECKED_CAST")
                        RegistryDumper.dumpEntries(
                            registry.streamEntries().toList() as List<RegistryEntry<Any>>,
                            registryKey,
                            context.source.registryManager,
                            dynamicRegistryCodec,
                        )

                        val keys = registry.keys
                        RegistryDumper.sendRegistries(context, keys.sortedBy { it.value.toString() })
                        keys.size
                    }
                    .then(entryTag)
                    .then(entryRegex),
            )

    return literal("datadumper")
        .requires { source -> permissionChecker(source, "commands.datadumper", 4) }
        .then(
            literal("registries").executes { context ->
                val keys =
                    context.source.registryManager
                        .streamAllRegistries()
                        .asSequence()
                        .map { it.key }
                        .sortedBy { it.value.toString() }
                        .toList()
                RegistryDumper.sendRegistries(context, keys)
                return@executes keys.size
            },
        )
        .then(registry)
        .then(entry)
}
