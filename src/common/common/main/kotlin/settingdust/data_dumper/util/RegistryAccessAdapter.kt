package settingdust.data_dumper.util

import java.util.*
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceKey

/**
 * SPI interface for abstracting `RegistryAccess.lookup` across MC versions.
 * The method name and return type may differ between versions
 * (e.g. `registryAccess()` vs `registryAccess()`, `lookup` vs `lookupOrThrow`).
 */
interface RegistryAccessAdapter {
    companion object : RegistryAccessAdapter by ServiceLoaderUtil.findService()

    /**
     * Looks up a registry from the given registry access object.
     * Abstracts over version-specific `RegistryAccess.lookup(ResourceKey)`.
     *
     * @param registryKey the registry key to look up
     * @return an Optional containing the registry lookup result, or empty if not found
     */
    fun <T : Any> RegistryAccess.lookupRegistry(registryKey: ResourceKey<Registry<T>>): Optional<Registry<T>>

    fun <T: Any> RegistryAccess.lookupRegistryOrThrow(registryKey: ResourceKey<Registry<T>>): Registry<T>
}
