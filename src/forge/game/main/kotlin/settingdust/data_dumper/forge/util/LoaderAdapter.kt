package settingdust.data_dumper.forge.util

import java.nio.file.Path
import net.minecraftforge.fml.loading.FMLLoader
import net.minecraftforge.fml.loading.FMLPaths
import net.minecraftforge.fml.loading.LoadingModList
import settingdust.data_dumper.util.LoaderAdapter

class LoaderAdapter : LoaderAdapter {
    override val isClient: Boolean
        get() = FMLLoader.getDist().isClient

    override val gameDir: Path = FMLPaths.GAMEDIR.get()

    override fun isModLoaded(modId: String) = LoadingModList.get().getModFileById(modId) != null
}
