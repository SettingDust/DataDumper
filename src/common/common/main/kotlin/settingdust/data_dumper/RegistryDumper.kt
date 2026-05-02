package settingdust.data_dumper

import com.google.gson.stream.JsonWriter
import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import kotlin.io.path.createParentDirectories
import kotlin.io.path.div
import kotlin.io.path.writeBytes
import kotlin.io.path.writer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryOps
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.resource.Resource
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import settingdust.data_dumper.util.LoaderAdapter

object RegistryDumper {
    val output get() = LoaderAdapter.gameDir / ".datadumper"

    @JvmStatic
    fun dumpFailedEntry(identifier: Identifier, resource: Resource) {
        val outputFile = output / identifier.namespace / identifier.path
        outputFile.createParentDirectories()
        outputFile.writeBytes(resource.inputStream.readAllBytes())
    }

    fun dumpEntries(
        entries: Iterable<RegistryEntry<Any>>,
        registryKey: RegistryKey<Registry<Any>>,
        registryManager: RegistryWrapper.WrapperLookup,
        codecProvider: (RegistryKey<out Registry<*>>) -> Codec<Any>?
    ) {
        val codec = codecProvider(registryKey) ?: return
        for (entry in entries) {
            GlobalScope.launch(Dispatchers.IO) {
                val registryOps = RegistryOps.of(JsonOps.INSTANCE, registryManager)
                val outputFile =
                    output /
                        entry.key.orElseThrow().value.namespace /
                        registryKey.value.path /
                        "${entry.key.orElseThrow().value.path}.json"
                outputFile.createParentDirectories()

                outputFile.writer().use { writer ->
                    val jsonWriter = JsonWriter(writer).apply { setIndent("  ") }
                    JsonHelper.writeSorted(
                        jsonWriter,
                        codec.encodeStart(registryOps, entry.value()).getOrThrow(false) {
                            error(it)
                        },
                        null,
                    )
                }
            }
        }
    }

    fun sendRegistries(
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
}
