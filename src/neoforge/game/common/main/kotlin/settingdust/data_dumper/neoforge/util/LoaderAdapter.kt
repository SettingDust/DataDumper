package settingdust.data_dumper.neoforge.util

import java.nio.file.Path
import net.neoforged.fml.loading.FMLPaths
import net.neoforged.fml.loading.LoadingModList
import settingdust.data_dumper.util.LoaderAdapter

class LoaderAdapter : LoaderAdapter {
    override val isClient: Boolean
        get() = NeoForgeAdapter.dist.isClient

    override val gameDir: Path = FMLPaths.GAMEDIR.get()

    override fun isModLoaded(modId: String) = LoadingModList.get().getModFileById(modId) != null
}
