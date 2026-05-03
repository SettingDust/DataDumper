package settingdust.data_dumper.v26_1.util

import java.util.*
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceKey
import settingdust.data_dumper.util.MinecraftVersion
import settingdust.data_dumper.util.RegistryAccessAdapter

class RegistryAccessAdapter : RegistryAccessAdapter {
    init {
        MinecraftVersion.V261.requireCurrent()
    }

    override fun <T : Any> RegistryAccess.lookupRegistry(registryKey: ResourceKey<Registry<T>>): Optional<Registry<T>> =
        lookup(registryKey)

    override fun <T : Any> RegistryAccess.lookupRegistryOrThrow(registryKey: ResourceKey<Registry<T>>): Registry<T> =
        lookupOrThrow(registryKey)
}
