package settingdust.data_dumper.v26_1.util

import net.minecraft.core.Registry
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import settingdust.data_dumper.util.CommonIdentifier
import settingdust.data_dumper.util.MinecraftVersion
import settingdust.data_dumper.util.ResourceKeyAdapter

class ResourceKeyAdapter : ResourceKeyAdapter {
    init {
        MinecraftVersion.V261.requireCurrent()
    }

    override fun <T : Any> createRegistryKey(id: CommonIdentifier): ResourceKey<Registry<T>> {
        return ResourceKey.createRegistryKey(id as Identifier)
    }

    @Suppress("USELESS_CAST")
    override fun ResourceKey<*>.getIdentifier(): CommonIdentifier {
        return identifier() as CommonIdentifier
    }
}
