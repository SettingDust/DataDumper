package settingdust.data_dumper.v26_1.util

import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.commands.arguments.IdentifierArgument
import settingdust.data_dumper.util.ArgumentTypeAdapter
import settingdust.data_dumper.util.CommonIdentifier
import settingdust.data_dumper.util.MinecraftVersion

class ArgumentTypeAdapter : ArgumentTypeAdapter {
    init {
        MinecraftVersion.V261.requireCurrent()
    }

    override fun identifier(): ArgumentType<CommonIdentifier> {
        return IdentifierArgument.id() as ArgumentType<CommonIdentifier>
    }
}
