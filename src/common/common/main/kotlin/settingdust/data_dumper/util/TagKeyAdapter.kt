package settingdust.data_dumper.util

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey

/**
 * SPI interface for abstracting `TagKey.of` / `TagKey.create` across MC versions.
 * The method name may differ between versions
 * (e.g. `TagKey.of` vs `TagKey.create`).
 */
interface TagKeyAdapter {
    companion object : TagKeyAdapter by ServiceLoaderUtil.findService()

    /**
     * Creates a [TagKey] from a registry key and an identifier, abstracting over
     * version-specific `TagKey.of` / `TagKey.create`.
     *
     * @param registryKey the registry key
     * @param id the tag identifier
     * @return a TagKey for the given registry and identifier
     */
    fun <T : Any> create(registryKey: ResourceKey<Registry<T>>, id: CommonIdentifier): TagKey<T>
}
