package settingdust.data_dumper.neoforge.v21_1.util

import net.neoforged.fml.loading.FMLLoader
import settingdust.data_dumper.neoforge.util.NeoForgeAdapter
import settingdust.data_dumper.util.MinecraftVersion

class NeoForgeAdapter : NeoForgeAdapter {
    init {
        MinecraftVersion.V1211.requireCurrent()
    }

    override val dist = FMLLoader.getDist()!!
}
