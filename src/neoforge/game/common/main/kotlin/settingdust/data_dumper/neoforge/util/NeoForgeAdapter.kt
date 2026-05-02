package settingdust.data_dumper.neoforge.util

import net.neoforged.api.distmarker.Dist
import settingdust.data_dumper.util.ServiceLoaderUtil

interface NeoForgeAdapter {
    companion object : NeoForgeAdapter by ServiceLoaderUtil.findService()

    val dist: Dist
}
