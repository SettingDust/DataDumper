package settingdust.data_dumper.neoforge

import com.mojang.serialization.Codec
import dev.nyon.klf.MOD_BUS
import net.minecraft.commands.synchronization.ArgumentTypeInfos
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import net.minecraft.server.level.ServerPlayer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.registries.DataPackRegistriesHooks
import net.neoforged.neoforge.server.permission.PermissionAPI
import net.neoforged.neoforge.server.permission.nodes.PermissionNode
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes
import settingdust.data_dumper.DataDumper
import settingdust.data_dumper.RegexArgumentType
import settingdust.data_dumper.TagArgumentType
import settingdust.data_dumper.buildCommands
import settingdust.data_dumper.neoforge.util.PermissionAdapter.Companion.checkPermission
import settingdust.data_dumper.util.Entrypoint
import settingdust.data_dumper.util.ResourceKeyAdapter.Companion.getIdentifier

@Mod(DataDumper.ID)
object DataDumperNeoForge {
    private val permission =
        PermissionNode(
            "commands", DataDumper.ID, PermissionTypes.BOOLEAN,
            { player, _, _ ->
                player != null && player.checkPermission(4)
            }
        )

    init {
        requireNotNull(DataDumper)
        Entrypoint.construct()
        MOD_BUS.apply {
            addListener<FMLCommonSetupEvent> {
                Entrypoint.init()
                ArgumentTypeInfos.registerByClass(
                    RegexArgumentType::class.java,
                    SingletonArgumentInfo.contextFree(::RegexArgumentType),
                )
                ArgumentTypeInfos.registerByClass(
                    TagArgumentType::class.java,
                    SingletonArgumentInfo.contextFree(::TagArgumentType),
                )
            }
            addListener<FMLClientSetupEvent> { Entrypoint.clientInit() }
        }

        // Register commands via NeoForge event bus
        NeoForge.EVENT_BUS.addListener<RegisterCommandsEvent> { event ->
            event.dispatcher.register(
                buildCommands(
                    permissionChecker = { source ->
                        if (source.checkPermission(4)) return@buildCommands true
                        val player = source.entity
                        if (player !is ServerPlayer) return@buildCommands false
                        PermissionAPI.getPermission(player,permission)
                    },
                    dynamicRegistryKeys = {
                        DataPackRegistriesHooks.getDataPackRegistries().map { it.key().getIdentifier() }
                    },
                    dynamicRegistryCodec = { registryKey ->
                        @Suppress("UNCHECKED_CAST")
                        DataPackRegistriesHooks.getDataPackRegistries()
                            .find { it.key() == registryKey }
                            ?.elementCodec() as? Codec<Any>
                    },
                ),
            )
        }
    }
}
