package settingdust.data_dumper.v21_1.util

import settingdust.data_dumper.util.MinecraftAdapter
import settingdust.data_dumper.util.MinecraftVersion

class MinecraftAdapter : MinecraftAdapter {
    init {
        MinecraftVersion.V1211.requireCurrent()
    }
}