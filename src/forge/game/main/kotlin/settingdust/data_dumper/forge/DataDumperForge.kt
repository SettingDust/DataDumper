package settingdust.data_dumper.forge

import com.mojang.serialization.Codec
import dev.nyon.klf.MOD_BUS
import net.minecraft.commands.synchronization.ArgumentTypeInfos
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.DataPackRegistriesHooks
import net.minecraftforge.server.permission.PermissionAPI
import net.minecraftforge.server.permission.nodes.PermissionNode
import net.minecraftforge.server.permission.nodes.PermissionTypes
import settingdust.data_dumper.DataDumper
import settingdust.data_dumper.RegexArgumentType
import settingdust.data_dumper.TagArgumentType
import settingdust.data_dumper.buildCommands
import settingdust.data_dumper.util.Entrypoint
import settingdust.data_dumper.util.ResourceKeyAdapter.Companion.getIdentifier

@Mod(DataDumper.ID)
object DataDumperForge {
    private val permission =
        PermissionNode(
            "commands", DataDumper.ID, PermissionTypes.BOOLEAN,
            { player, _, _ ->
                player != null && player.hasPermissions(4)
            },
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

        // Register commands via Forge event bus
        MinecraftForge.EVENT_BUS.addListener<RegisterCommandsEvent> { event ->
            event.dispatcher.register(
                buildCommands(
                    permissionChecker = { source ->
                        if (source.hasPermission(4)) return@buildCommands true
                        val player = source.entity
                        if (player !is ServerPlayer) return@buildCommands false
                        PermissionAPI.getPermission(player, permission)
                    },
                    dynamicRegistryKeys = {
                        DataPackRegistriesHooks.getDataPackRegistries()
                            .map { it.key().getIdentifier() }
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
