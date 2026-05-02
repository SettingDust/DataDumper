package settingdust.data_dumper.neoforge.v26_1.util

import net.neoforged.fml.loading.FMLLoader
import settingdust.data_dumper.neoforge.util.NeoForgeAdapter
import settingdust.data_dumper.util.MinecraftVersion

class NeoForgeAdapter : NeoForgeAdapter {
    init {
        MinecraftVersion.V261.requireCurrent()
    }

    override val dist = FMLLoader.getCurrent().dist
}
