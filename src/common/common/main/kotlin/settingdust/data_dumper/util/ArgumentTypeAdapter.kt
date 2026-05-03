package settingdust.data_dumper.util

import com.mojang.brigadier.arguments.ArgumentType

interface ArgumentTypeAdapter {
    companion object : ArgumentTypeAdapter by ServiceLoaderUtil.findService()

    /**
     * Creates an identifier argument type instance, abstracting over version-specific
     * `IdentifierArgumentType.identifier()` / `ResourceLocationArgument.id()`.
     *
     * @return an [ArgumentType] that parses identifiers
     */
    fun identifier(): ArgumentType<CommonIdentifier>
}
