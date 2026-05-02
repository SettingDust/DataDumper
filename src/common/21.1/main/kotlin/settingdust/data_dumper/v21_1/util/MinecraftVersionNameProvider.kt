package settingdust.data_dumper.v21_1.util

import net.minecraft.SharedConstants
import settingdust.data_dumper.util.MinecraftVersionNameProvider

class MinecraftVersionNameProvider : MinecraftVersionNameProvider {
    override fun currentVersionName(): String = SharedConstants.getCurrentVersion().name
}
