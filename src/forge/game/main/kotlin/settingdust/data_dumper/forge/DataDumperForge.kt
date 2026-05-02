package settingdust.data_dumper.forge

import com.mojang.serialization.Codec
import dev.nyon.klf.MOD_BUS
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.DataPackRegistriesHooks
import settingdust.data_dumper.DataDumper
import settingdust.data_dumper.buildCommands
import settingdust.data_dumper.util.Entrypoint

@Mod(DataDumper.ID)
object DataDumperForge {
    init {
        requireNotNull(DataDumper)
        Entrypoint.construct()
        MOD_BUS.apply {
            addListener<FMLCommonSetupEvent> {
                Entrypoint.init()
            }
            addListener<FMLClientSetupEvent> { Entrypoint.clientInit() }
        }

        // Register commands via Forge event bus
        MinecraftForge.EVENT_BUS.addListener<RegisterCommandsEvent> { event ->
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
