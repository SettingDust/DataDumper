package settingdust.data_dumper.v21_1.util

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import settingdust.data_dumper.util.CommonIdentifier
import settingdust.data_dumper.util.MinecraftVersion
import settingdust.data_dumper.util.TagKeyAdapter

class TagKeyAdapter : TagKeyAdapter {
    init {
        MinecraftVersion.V1211.requireCurrent()
    }

    override fun <T : Any> create(registryKey: ResourceKey<Registry<T>>, id: CommonIdentifier): TagKey<T> {
        return TagKey.create(registryKey, id as ResourceLocation)
    }
}
