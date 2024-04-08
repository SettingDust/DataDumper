package settingdust.datadumper

import com.mojang.brigadier.context.CommandContext
import java.util.regex.Pattern
import kotlin.streams.asSequence
import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import settingdust.datadumper.mixin.ArgumentTypesAccessor

object DataDumper {
    const val ID = "data-dumper"

    val logger = LogManager.getLogger()!!

    fun identifier(name: String): Identifier {
        return Identifier(ID, name)
    }
}

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
                                context.getArgument("tag", Identifier::class.java)
                            )
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
                            sendRegistries(context, keys.sortedBy { it.value })
                            keys.size
                        }
                        .then(registryTag)
                        .then(registryRegex),
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
                                .sortedBy { it.value }
                                .toList()
                        sendRegistries(context, keys)
                        return@executes keys.size
                    },
                )
                .then(
                    registry,
                ),
        )
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
