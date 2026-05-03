package settingdust.data_dumper.util

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

/**
 * SPI interface for abstracting `ResourceKey`/`RegistryKey` creation across MC versions.
 * The class name and factory method may differ between versions
 * (e.g. `ResourceKey.createRegistryKey` vs `RegistryKey.ofRegistry`).
 */
interface ResourceKeyAdapter {
    companion object : ResourceKeyAdapter by ServiceLoaderUtil.findService()

    /**
     * Creates a registry key from an identifier, abstracting over version-specific
     * `ResourceKey.createRegistryKey` / `ResourceKey.ofRegistry` / `RegistryKey.ofRegistry`.
     *
     * @param id the registry identifier
     * @return a registry key for the given identifier
     */
    fun <T : Any> createRegistryKey(id: CommonIdentifier): ResourceKey<Registry<T>>

    /**
     * Gets the location/identifier of a [ResourceKey], abstracting over version-specific
     * `ResourceKey.location()` / `ResourceKey.getValue()` / `RegistryKey.getValue()`.
     *
     * @return the location as a [CommonIdentifier]
     */
    fun ResourceKey<*>.getIdentifier(): CommonIdentifier
}
