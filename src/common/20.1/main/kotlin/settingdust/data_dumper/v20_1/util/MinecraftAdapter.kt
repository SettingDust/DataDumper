package settingdust.data_dumper.v20_1.util

import settingdust.data_dumper.util.MinecraftAdapter
import settingdust.data_dumper.util.MinecraftVersion

class MinecraftAdapter : MinecraftAdapter {
    init {
        MinecraftVersion.V1201.requireCurrent()
    }
}