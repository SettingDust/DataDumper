package settingdust.data_dumper.v20_1.util

import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.commands.arguments.ResourceLocationArgument
import settingdust.data_dumper.util.ArgumentTypeAdapter
import settingdust.data_dumper.util.CommonIdentifier
import settingdust.data_dumper.util.MinecraftVersion

class ArgumentTypeAdapter : ArgumentTypeAdapter {
    init {
        MinecraftVersion.V1201.requireCurrent()
    }

    override fun identifier(): ArgumentType<CommonIdentifier> {
        return ResourceLocationArgument.id() as ArgumentType<CommonIdentifier>
    }
}
