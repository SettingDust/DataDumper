package settingdust.data_dumper.util

import java.util.stream.Stream
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.tags.TagKey

interface RegistryAdapter {
    companion object : RegistryAdapter by ServiceLoaderUtil.findService()

    /**
     * Gets all tag identifiers from the given registry.
     * Abstracts over version-specific `Registry.getTags()` / `Registry.streamTags()`.
     *
     * @return a stream of tag [CommonIdentifier]s
     */
    fun Registry<*>.getTagIds(): Stream<CommonIdentifier>

    /**
     * Gets or creates a tag in the given registry.
     * Abstracts over version-specific `Registry.getOrCreateTag(TagKey)` / `Registry.getOrCreateEntryList(TagKey)`.
     *
     * @param tagKey the tag key to look up
     * @return an iterable of holders for the tag entries
     */
    fun <T : Any> Registry<T>.getOrCreateTag(tagKey: TagKey<T>): Iterable<Holder<T>>
}
