package settingdust.data_dumper.fabric.util

import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import settingdust.data_dumper.util.CommonIdentifier
import settingdust.data_dumper.util.ServiceLoaderUtil

/**
 * SPI that abstracts over version-specific Fabric argument type registration.
 *
 * Delegates to `net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry.registerArgumentType`,
 * which requires a version-specific native identifier conversion.
 */
interface ArgumentTypeRegistryAdapter {
    companion object : ArgumentTypeRegistryAdapter by ServiceLoaderUtil.findService()

    /**
     * Registers a single custom argument type via the Fabric API.
     *
     * @param id    the argument type identifier
     * @param clazz the argument type class
     * @param info  the argument type serialization info
     */
    fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerArgumentType(
        id: CommonIdentifier,
        clazz: Class<A>,
        info: ArgumentTypeInfo<A, T>,
    )
}
