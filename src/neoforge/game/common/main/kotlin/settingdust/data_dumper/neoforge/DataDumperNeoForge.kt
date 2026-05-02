package settingdust.data_dumper.neoforge

import com.mojang.serialization.Codec
import dev.nyon.klf.MOD_BUS
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.registries.DataPackRegistriesHooks
import settingdust.data_dumper.DataDumper
import settingdust.data_dumper.buildCommands
import settingdust.data_dumper.util.Entrypoint

@Mod(DataDumper.ID)
object DataDumperNeoForge {
    init {
        requireNotNull(DataDumper)
        Entrypoint.construct()
        MOD_BUS.apply {
            addListener<FMLCommonSetupEvent> {
                Entrypoint.init()
            }
            addListener<FMLClientSetupEvent> { Entrypoint.clientInit() }
        }

        // Register commands via NeoForge event bus
        NeoForge.EVENT_BUS.addListener<RegisterCommandsEvent> { event ->
            event.dispatcher.register(
                buildCommands(
                    permissionChecker = { source, _, level ->
                        source.hasPermissionLevel(level)
                    },
                    dynamicRegistryKeys = {
                        DataPackRegistriesHooks.getDataPackRegistries().map { it.key().location() }
                    },
                    dynamicRegistryCodec = { registryKey ->
                        @Suppress("UNCHECKED_CAST")
                        DataPackRegistriesHooks.getDataPackRegistries()
                            .find { it.key() == registryKey }
                            ?.value() as? Codec<Any>
                    },
                )
            )
        }
    }
}
