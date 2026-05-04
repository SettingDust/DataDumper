package settingdust.data_dumper.v26_1.util

import net.minecraft.resources.Identifier
import settingdust.data_dumper.util.CommonIdentifier
import settingdust.data_dumper.util.IdentifierAdapter
import settingdust.data_dumper.util.MinecraftVersion

class IdentifierAdapter : IdentifierAdapter {
    init {
        MinecraftVersion.V261.requireCurrent()
    }

    @Suppress("USELESS_CAST")
    override fun create(namespace: String, path: String): CommonIdentifier {
        return Identifier.fromNamespaceAndPath(namespace, path) as CommonIdentifier
    }

    @Suppress("USELESS_CAST")
    override fun parse(value: String): CommonIdentifier {
        return Identifier.parse(value) as CommonIdentifier
    }
}
