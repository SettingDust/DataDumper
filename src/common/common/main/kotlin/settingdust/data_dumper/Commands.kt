package settingdust.data_dumper

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.serialization.Codec
import java.util.regex.Pattern
import kotlin.streams.asSequence
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import settingdust.data_dumper.util.ArgumentTypeAdapter
import settingdust.data_dumper.util.CommonIdentifier
import settingdust.data_dumper.util.RegistryAccessAdapter.Companion.lookupRegistryOrThrow
import settingdust.data_dumper.util.RegistryAdapter.Companion.getOrCreateTag
import settingdust.data_dumper.util.ResourceKeyAdapter
import settingdust.data_dumper.util.ResourceKeyAdapter.Companion.getIdentifier
import settingdust.data_dumper.util.SuggestResourceProvider
import settingdust.data_dumper.util.TagKeyAdapter

/**
 * Builds the datadumper command tree.
 *
 * @param permissionChecker checks if a source has a given permission
 * @param dynamicRegistryKeys provides identifiers of all dynamic registries (for entry command suggestions)
 * @param dynamicRegistryCodec provides the codec for a given dynamic registry key (for entry serialization)
 */
fun buildCommands(
    permissionChecker: (CommandSourceStack) -> Boolean,
    dynamicRegistryKeys: () -> Iterable<CommonIdentifier>,
    dynamicRegistryCodec: (ResourceKey<out Registry<*>>) -> Codec<Any>?
): LiteralArgumentBuilder<CommandSourceStack> {
    val registryTag =
        argument("tag", TagArgumentType()).executes { context ->
            val registryKey =
                ResourceKeyAdapter.createRegistryKey<Any>(
                    context.getArgument(
                        "key",
                        CommonIdentifier::class.java,
                    ),
                )
            val registry = context.source.registryAccess().lookupRegistryOrThrow(registryKey)

            val keys =
                registry
                    .getOrCreateTag(
                        TagKeyAdapter.create(
                            registryKey,
                            context.getArgument("tag", CommonIdentifier::class.java),
                        ),
                    )
                    .map { it.unwrapKey().orElseThrow() }
            RegistryDumper.sendRegistries(context, keys)

            keys.size
        }
    val registryRegex =
        argument("regex", RegexArgumentType()).executes { context ->
            val registryKey =
                ResourceKeyAdapter.createRegistryKey<Any>(
                    context.getArgument(
                        "key",
                        CommonIdentifier::class.java,
                    ),
                )
            val registry = context.source.registryAccess().lookupRegistryOrThrow(registryKey)

            val pattern = context.getArgument("regex", Pattern::class.java)

            val keys =
                registry.asHolderIdMap()
                    .asSequence()
                    .map { it.unwrapKey().orElseThrow() }
                    .filter { pattern.matcher(it.getIdentifier().toString()).find() }
                    .toList()

            RegistryDumper.sendRegistries(context, keys)

            keys.size
        }
    val registry =
        literal("registry")
            .then(
                argument("key", ArgumentTypeAdapter.identifier())
                    .suggests { context, builder ->
                        SuggestResourceProvider.suggestResource(
                            context.source.registryAccess().registries().map {
                                it.key().getIdentifier()
                            },
                            builder,
                        )
                    }
                    .executes { context ->
                        val registry =
                            ResourceKeyAdapter.createRegistryKey<Any>(
                                context.getArgument("key", CommonIdentifier::class.java),
                            )
                        val keys = context.source.registryAccess().lookupRegistryOrThrow(registry)
                            .registryKeySet()
                        RegistryDumper.sendRegistries(
                            context,
                            keys.sortedBy { it.getIdentifier().toString() },
                        )
                        keys.size
                    }
                    .then(registryTag)
                    .then(registryRegex),
            )

    val entryTag =
        argument("tag", TagArgumentType()).executes { context ->
            val registryKey =
                ResourceKeyAdapter.createRegistryKey<Any>(
                    context.getArgument(
                        "key",
                        CommonIdentifier::class.java,
                    ),
                )
            val registry = context.source.registryAccess().lookupRegistryOrThrow(registryKey)

            val holders =
                registry.getOrCreateTag(
                    TagKeyAdapter.create(
                        registryKey,
                        context.getArgument("tag", CommonIdentifier::class.java),
                    ),
                )

            RegistryDumper.dumpEntries(
                holders,
                registryKey,
                context.source.registryAccess(),
                dynamicRegistryCodec,
            )

            val keys = holders.map { it.unwrapKey().orElseThrow() }
            RegistryDumper.sendRegistries(context, keys)

            keys.size
        }
    val entryRegex =
        argument("regex", RegexArgumentType()).executes { context ->
            val registryKey =
                ResourceKeyAdapter.createRegistryKey<Any>(
                    context.getArgument(
                        "key",
                        CommonIdentifier::class.java,
                    ),
                )
            val registry = context.source.registryAccess().lookupRegistryOrThrow(registryKey)

            val pattern = context.getArgument("regex", Pattern::class.java)
            val entries =
                registry
                    .asHolderIdMap()
                    .asSequence()
                    .filter {
                        pattern.matcher(it.unwrapKey().orElseThrow().getIdentifier().toString()).find()
                    }
                    .toList()

            RegistryDumper.dumpEntries(
                entries,
                registryKey,
                context.source.registryAccess(),
                dynamicRegistryCodec,
            )

            RegistryDumper.sendRegistries(context, entries.map { it.unwrapKey().orElseThrow() })

            entries.size
        }
    val entry =
        literal("entry")
            .then(
                argument("key", ArgumentTypeAdapter.identifier())
                    .suggests { _, builder ->
                        SuggestResourceProvider.suggestResource(
                            dynamicRegistryKeys(),
                            builder,
                        )
                    }
                    .executes { context ->
                        val registryKey =
                            ResourceKeyAdapter.createRegistryKey<Any>(
                                context.getArgument("key", CommonIdentifier::class.java),
                            )
                        val registry =
                            context.source.registryAccess().lookupRegistryOrThrow(registryKey)

                        @Suppress("UNCHECKED_CAST")
                        RegistryDumper.dumpEntries(
                            registry.asHolderIdMap().toList(),
                            registryKey,
                            context.source.registryAccess(),
                            dynamicRegistryCodec,
                        )

                        val keys = registry.registryKeySet()
                        RegistryDumper.sendRegistries(
                            context,
                            keys.sortedBy { it.getIdentifier().toString() },
                        )
                        keys.size
                    }
                    .then(entryTag)
                    .then(entryRegex),
            )

    return literal("datadumper")
        .requires { source -> permissionChecker(source) }
        .then(
            literal("registries").executes { context ->
                val keys =
                    context.source.registryAccess()
                        .registries()
                        .asSequence()
                        .map { it.key() }
                        .sortedBy { it.getIdentifier().toString() }
                        .toList()
                RegistryDumper.sendRegistries(context, keys)
                return@executes keys.size
            },
        )
        .then(registry)
        .then(entry)
}
