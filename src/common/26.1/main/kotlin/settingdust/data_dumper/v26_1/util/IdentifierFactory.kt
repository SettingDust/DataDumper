package settingdust.data_dumper.v26_1.util

import net.minecraft.resources.Identifier
import settingdust.data_dumper.util.CommonIdentifier
import settingdust.data_dumper.util.IdentifierFactory
import settingdust.data_dumper.util.MinecraftVersion

class IdentifierFactory : IdentifierFactory {
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
