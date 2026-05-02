package settingdust.data_dumper.util

import java.nio.file.Path

interface LoaderAdapter {
    companion object : LoaderAdapter by ServiceLoaderUtil.findService()

    val isClient: Boolean

    val gameDir: Path

    fun isModLoaded(modId: String): Boolean
}