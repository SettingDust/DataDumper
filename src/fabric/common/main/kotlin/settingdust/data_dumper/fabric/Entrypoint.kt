package settingdust.data_dumper.fabric

import com.mojang.serialization.Codec
import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.registry.DynamicRegistries
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer
import settingdust.data_dumper.DataDumper
import settingdust.data_dumper.RegexArgumentType
import settingdust.data_dumper.TagArgumentType
import settingdust.data_dumper.buildCommands
import settingdust.data_dumper.util.Entrypoint

object DataDumperFabric {
    init {
        requireNotNull(DataDumper)
        Entrypoint.construct()
    }

    fun init() {
        Entrypoint.init()

        // Register custom argument types
        ArgumentTypeRegistry.registerArgumentType(
            DataDumper.id("tag"),
            TagArgumentType::class.java,
            ConstantArgumentSerializer.of(::TagArgumentType),
        )

        ArgumentTypeRegistry.registerArgumentType(
            DataDumper.id("regex"),
            RegexArgumentType::class.java,
            ConstantArgumentSerializer.of(::RegexArgumentType),
        )

        // Register commands
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                buildCommands(
                    permissionChecker = { source, permission, level ->
                        Permissions.check(source, permission, level)
                    },
                    dynamicRegistryKeys = {
                        DynamicRegistries.getDynamicRegistries().map { it.key.value }
                    },
                    dynamicRegistryCodec = { registryKey ->
                        @Suppress("UNCHECKED_CAST")
                        DynamicRegistries.getDynamicRegistries()
                            .find { it.key == registryKey }
                            ?.elementCodec as? Codec<Any>
                    },
                )
            )
        }
    }

    fun clientInit() {
        Entrypoint.clientInit()
    }
}
