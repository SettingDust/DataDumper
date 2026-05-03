package settingdust.data_dumper

import com.google.gson.stream.JsonWriter
import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import kotlin.io.path.createParentDirectories
import kotlin.io.path.div
import kotlin.io.path.writeBytes
import kotlin.io.path.writer
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.commands.CommandSourceStack
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.network.chat.Component
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceKey
import net.minecraft.server.packs.resources.Resource
import net.minecraft.util.GsonHelper
import settingdust.data_dumper.util.CommonIdentifier
import settingdust.data_dumper.util.ComponentEventAdapter
import settingdust.data_dumper.util.DataResultAdapter.Companion.getOrThrow
import settingdust.data_dumper.util.LoaderAdapter
import settingdust.data_dumper.util.ResourceKeyAdapter.Companion.getIdentifier

object RegistryDumper {
    val output get() = LoaderAdapter.gameDir / ".datadumper"

    @JvmStatic
    fun dumpFailedEntry(identifier: CommonIdentifier, resource: Resource) {
        val outputFile = output / identifier.namespace / identifier.path
        outputFile.createParentDirectories()
        outputFile.writeBytes(resource.open().readAllBytes())
    }

    fun dumpEntries(
        entries: Iterable<Holder<Any>>,
        registryKey: ResourceKey<Registry<Any>>,
        registryManager: RegistryAccess,
        codecProvider: (ResourceKey<out Registry<*>>) -> Codec<Any>?
    ) {
        val codec = codecProvider(registryKey) ?: return
        for (entry in entries) {
            CoroutineScope(
                CoroutineName(
                    "Dump ${
                        entry.unwrapKey().orElseThrow().getIdentifier()
                    }",
                ),
            ).launch(Dispatchers.IO) {
                val registryOps = RegistryOps.create(JsonOps.INSTANCE, registryManager)
                val outputFile =
                    output /
                        entry.unwrapKey().orElseThrow().getIdentifier().namespace /
                        registryKey.getIdentifier().path /
                        "${entry.unwrapKey().orElseThrow().getIdentifier().path}.json"
                outputFile.createParentDirectories()

                outputFile.writer().use { writer ->
                    val jsonWriter = JsonWriter(writer).apply { setIndent("  ") }
                    GsonHelper.writeValue(
                        jsonWriter,
                        codec.encodeStart(registryOps, entry.value()).getOrThrow(),
                        null,
                    )
                }
            }
        }
    }

    fun sendRegistries(
        context: CommandContext<CommandSourceStack>,
        keys: List<ResourceKey<*>>
    ) {
        if (context.source.isPlayer) {
            context.source.sendSuccess(
                {
                    val feedback =
                        Component.translatable(
                            "commands.datadumper.registries.feedback.player",
                            keys.size,
                        )

                    feedback.style =
                        feedback.style
                            .withHoverEvent(
                                ComponentEventAdapter.showText(
                                    Component.literal("/${context.input}\n")
                                        .append(Component.translatable("datadumper.message.clickToCopy")),
                                ),
                            )
                            .withClickEvent(
                                ComponentEventAdapter.copyToClipboard(
                                    keys.joinToString("\n") { it.getIdentifier().toString() },
                                ),
                            )

                    feedback
                },
                false,
            )
        } else {
            for (key in keys) {
                context.source.sendSuccess(
                    { Component.literal(key.getIdentifier().toString()) },
                    false,
                )
            }
        }
    }
}
