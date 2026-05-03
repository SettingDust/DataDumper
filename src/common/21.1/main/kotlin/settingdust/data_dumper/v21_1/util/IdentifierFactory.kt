package settingdust.data_dumper.v21_1.util

import net.minecraft.resources.ResourceLocation
import settingdust.data_dumper.util.CommonIdentifier
import settingdust.data_dumper.util.IdentifierFactory
import settingdust.data_dumper.util.MinecraftVersion

class IdentifierFactory : IdentifierFactory {
    init {
        MinecraftVersion.V1211.requireCurrent()
    }

    @Suppress("USELESS_CAST")
    override fun create(namespace: String, path: String): CommonIdentifier {
        return ResourceLocation.fromNamespaceAndPath(namespace, path) as CommonIdentifier
    }

    @Suppress("USELESS_CAST")
    override fun parse(value: String): CommonIdentifier {
        return ResourceLocation.parse(value) as CommonIdentifier
    }
}
