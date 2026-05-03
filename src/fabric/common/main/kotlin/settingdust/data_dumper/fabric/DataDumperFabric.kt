package settingdust.data_dumper.fabric

import com.mojang.serialization.Codec
import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.registry.DynamicRegistries
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import settingdust.data_dumper.DataDumper
import settingdust.data_dumper.RegexArgumentType
import settingdust.data_dumper.TagArgumentType
import settingdust.data_dumper.buildCommands
import settingdust.data_dumper.fabric.util.ArgumentTypeRegistryAdapter
import settingdust.data_dumper.util.Entrypoint
import settingdust.data_dumper.util.ResourceKeyAdapter.Companion.getIdentifier

object DataDumperFabric {
    init {
        requireNotNull(DataDumper)
        Entrypoint.construct()
    }

    fun init() {
        Entrypoint.init()

        // Register custom argument types via SPI
        ArgumentTypeRegistryAdapter.registerArgumentType(
            DataDumper.id("tag"),
            TagArgumentType::class.java,
            SingletonArgumentInfo.contextFree(::TagArgumentType),
        )
        ArgumentTypeRegistryAdapter.registerArgumentType(
            DataDumper.id("regex"),
            RegexArgumentType::class.java,
            SingletonArgumentInfo.contextFree(::RegexArgumentType),
        )

        // Register commands
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                buildCommands(
                    permissionChecker = { source ->
                        Permissions.check(source, "commands.${DataDumper.ID}", 4)
                    },
                    dynamicRegistryKeys = {
                        DynamicRegistries.getDynamicRegistries().map { it.key().getIdentifier() }
                    },
                    dynamicRegistryCodec = { registryKey ->
                        @Suppress("UNCHECKED_CAST")
                        DynamicRegistries.getDynamicRegistries()
                            .find { it.key() == registryKey }
                            ?.elementCodec() as? Codec<Any>
                    },
                ),
            )
        }
    }

    fun clientInit() {
        Entrypoint.clientInit()
    }
}
