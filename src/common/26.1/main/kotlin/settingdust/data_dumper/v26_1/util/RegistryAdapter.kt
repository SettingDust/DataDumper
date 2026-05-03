package settingdust.data_dumper.v26_1.util

import java.util.stream.Stream
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.tags.TagKey
import settingdust.data_dumper.util.CommonIdentifier
import settingdust.data_dumper.util.MinecraftVersion
import settingdust.data_dumper.util.RegistryAdapter

class RegistryAdapter : RegistryAdapter {
    init {
        MinecraftVersion.V261.requireCurrent()
    }

    @Suppress("UNCHECKED_CAST")
    override fun Registry<*>.getTagIds(): Stream<CommonIdentifier> {
        return tags.map { it.key().location() as CommonIdentifier }
    }

    override fun <T : Any> Registry<T>.getOrCreateTag(tagKey: TagKey<T>): Iterable<Holder<T>> {
        return getTagOrEmpty(tagKey)
    }
}
