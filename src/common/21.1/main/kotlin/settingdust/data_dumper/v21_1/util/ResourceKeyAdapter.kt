package settingdust.data_dumper.v21_1.util

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import settingdust.data_dumper.util.CommonIdentifier
import settingdust.data_dumper.util.MinecraftVersion
import settingdust.data_dumper.util.ResourceKeyAdapter

class ResourceKeyAdapter : ResourceKeyAdapter {
    init {
        MinecraftVersion.V1211.requireCurrent()
    }

    override fun <T : Any> createRegistryKey(id: CommonIdentifier): ResourceKey<Registry<T>> {
        return ResourceKey.createRegistryKey(id as ResourceLocation)
    }

    @Suppress("USELESS_CAST")
    override fun ResourceKey<*>.getIdentifier(): CommonIdentifier {
        return location() as CommonIdentifier
    }
}
