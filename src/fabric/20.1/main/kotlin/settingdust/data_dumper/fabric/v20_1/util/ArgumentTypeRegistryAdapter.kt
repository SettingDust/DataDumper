package settingdust.data_dumper.fabric.v20_1.util

import com.mojang.brigadier.arguments.ArgumentType
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.resources.ResourceLocation
import settingdust.data_dumper.fabric.util.ArgumentTypeRegistryAdapter
import settingdust.data_dumper.util.CommonIdentifier

class ArgumentTypeRegistryAdapter : ArgumentTypeRegistryAdapter {
    override fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerArgumentType(
        id: CommonIdentifier,
        clazz: Class<A>,
        info: ArgumentTypeInfo<A, T>,
    ) {
        ArgumentTypeRegistry.registerArgumentType(id as ResourceLocation, clazz, info)
    }
}
