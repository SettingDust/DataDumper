package settingdust.data_dumper.neoforge.v26_1

import dev.nyon.klf.MOD_BUS
import settingdust.data_dumper.util.Entrypoint
import settingdust.data_dumper.util.MinecraftVersion

class DataDumperNeoForgeEntrypoint : Entrypoint {
    init {
        MinecraftVersion.V261.requireCurrent()
        @Suppress("RedundantRequireNotNullCall")
        requireNotNull(MOD_BUS)
    }

    override fun construct() {
    }
}
