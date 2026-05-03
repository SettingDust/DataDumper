package settingdust.data_dumper.v20_1.util

import java.util.*
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceKey
import settingdust.data_dumper.util.MinecraftVersion
import settingdust.data_dumper.util.RegistryAccessAdapter

class RegistryAccessAdapter : RegistryAccessAdapter {
    init {
        MinecraftVersion.V1201.requireCurrent()
    }

    override fun <T : Any> RegistryAccess.lookupRegistry(registryKey: ResourceKey<Registry<T>>): Optional<Registry<T>> =
        registry(registryKey)

    override fun <T : Any> RegistryAccess.lookupRegistryOrThrow(registryKey: ResourceKey<Registry<T>>): Registry<T> =
        registryOrThrow(registryKey)
}
