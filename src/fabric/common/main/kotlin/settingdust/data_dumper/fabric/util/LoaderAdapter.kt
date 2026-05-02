package settingdust.data_dumper.fabric.util

import java.nio.file.Path
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import settingdust.data_dumper.util.LoaderAdapter

class LoaderAdapter : LoaderAdapter {
    override val isClient = FabricLoader.getInstance().environmentType === EnvType.CLIENT

    override val gameDir: Path = FabricLoader.getInstance().gameDir

    override fun isModLoaded(modId: String) = FabricLoader.getInstance().isModLoaded(modId)
}
