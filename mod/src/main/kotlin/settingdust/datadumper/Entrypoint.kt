package settingdust.datadumper

import com.google.gson.Gson
import com.google.gson.stream.JsonWriter
import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import java.util.regex.Pattern
import kotlin.io.path.createParentDirectories
import kotlin.io.path.div
import kotlin.io.path.writeBytes
import kotlin.io.path.writer
import kotlin.streams.asSequence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.registry.DynamicRegistries
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryOps
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.TagKey
import net.minecraft.resource.Resource
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import org.apache.logging.log4j.LogManager
import settingdust.datadumper.mixin.ArgumentTypesAccessor

object DataDumper {
    const val ID = "data-dumper"

    val logger = LogManager.getLogger()!!

    fun identifier(name: String): Identifier {
        return Identifier(ID, name)
    }
}

val output = FabricLoader.getInstance().gameDir / ".datadumper"

private val GSON = Gson()

fun init() {
    ArgumentTypesAccessor.register(
        Registries.COMMAND_ARGUMENT_TYPE,
        "${DataDumper.ID}:tag",
        TagArgumentType::class.java,
        ConstantArgumentSerializer.of(::TagArgumentType),
    )

    ArgumentTypesAccessor.register(
        Registries.COMMAND_ARGUMENT_TYPE,
        "${DataDumper.ID}:regex",
        RegexArgumentType::class.java,
        ConstantArgumentSerializer.of(::RegexArgumentType),
    )

    CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
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
                sendRegistries(context, keys)

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

                sendRegistries(context, keys)

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
                            sendRegistries(context, keys.sortedBy { it.value.toString() })
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

                dumpEntries(entryList, registryKey, context.source.registryManager)

                val keys = entryList.map { it.key.orElseThrow() }
                sendRegistries(context, keys)

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

                dumpEntries(entries, registryKey, context.source.registryManager)

                sendRegistries(context, entries.map { it.key.orElseThrow() })

                entries.size
            }
        val entry =
            literal("entry")
                .then(
                    argument("key", IdentifierArgumentType.identifier())
                        .suggests { context, builder ->
                            CommandSource.suggestIdentifiers(
                                DynamicRegistries.getDynamicRegistries().map { it.key.value },
                                builder,
                            )
                        }
                        .executes { context ->
                            val registryKey =
                                RegistryKey.ofRegistry<Any>(
                                    context.getArgument("key", Identifier::class.java),
                                )
                            val registry = context.source.registryManager.get(registryKey)

                            dumpEntries(
                                registry.streamEntries().toList(),
                                registryKey,
                                context.source.registryManager
                            )

                            val keys = registry.keys
                            sendRegistries(context, keys.sortedBy { it.value.toString() })
                            keys.size
                        }
                        .then(entryTag)
                        .then(entryRegex),
                )

        dispatcher.register(
            literal("datadumper")
                .requires(Permissions.require("commands.datadumper", 4))
                .then(
                    literal("registries").executes { context ->
                        val keys =
                            context.source.registryManager
                                .streamAllRegistries()
                                .asSequence()
                                .map { it.key }
                                .sortedBy { it.value.toString() }
                                .toList()
                        sendRegistries(context, keys)
                        return@executes keys.size
                    },
                )
                .then(registry)
                .then(entry),
        )
    }
}

fun dumpFailedEntry(identifier: Identifier, resource: Resource) {
    val outputFile = output / identifier.namespace / identifier.path
    outputFile.createParentDirectories()
    outputFile.writeBytes(resource.inputStream.readAllBytes())
}

private fun dumpEntries(
    entries: Iterable<RegistryEntry<Any>>,
    registryKey: RegistryKey<Registry<Any>>,
    registryManager: RegistryWrapper.WrapperLookup
) {
    val codec =
        DynamicRegistries.getDynamicRegistries().find { it.key.equals(registryKey) }!!.elementCodec
            as Codec<Any>
    for (entry in entries) {
        GlobalScope.launch(Dispatchers.IO) {
            val registryOps = RegistryOps.of(JsonOps.INSTANCE, registryManager)
            val outputFile =
                output /
                    entry.key.orElseThrow().value.namespace /
                    registryKey.value.path /
                    "${entry.key.orElseThrow().value.path}.json"
            outputFile.createParentDirectories()

            outputFile.writer().use {
                val jsonWriter = JsonWriter(it).apply { setIndent("  ") }
                JsonHelper.writeSorted(
                    jsonWriter,
                    codec.encodeStart(registryOps, entry.value()).result().orElseThrow(),
                    null
                )
            }
        }
    }
}

private fun sendRegistries(
    context: CommandContext<ServerCommandSource>,
    keys: List<RegistryKey<*>>
) {
    if (context.source.isExecutedByPlayer) {
        context.source.sendFeedback(
            {
                val feedback =
                    Text.translatable(
                        "commands.datadumper.registries.feedback.player",
                        keys.size,
                    )

                feedback.style =
                    feedback.style
                        .withHoverEvent(
                            HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Text.literal("/${context.input}\n")
                                    .append(Text.translatable("datadumper.message.clickToCopy")),
                            ),
                        )
                        .withClickEvent(
                            ClickEvent(
                                ClickEvent.Action.COPY_TO_CLIPBOARD,
                                keys.joinToString("\n") { it.value.toString() },
                            ),
                        )

                feedback
            },
            false,
        )
    } else {
        for (key in keys) {
            context.source.sendFeedback(
                { Text.literal(key.value.toString()) },
                false,
            )
        }
    }
}
