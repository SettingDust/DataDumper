package settingdust.data_dumper.neoforge.v21_1

import dev.nyon.klf.MOD_BUS
import settingdust.data_dumper.util.Entrypoint
import settingdust.data_dumper.util.MinecraftVersion

class DataDumperNeoForgeEntrypoint : Entrypoint {
    init {
        MinecraftVersion.V1211.requireCurrent()
        @Suppress("RedundantRequireNotNullCall")
        requireNotNull(MOD_BUS)
    }

    override fun construct() {
    }
}
