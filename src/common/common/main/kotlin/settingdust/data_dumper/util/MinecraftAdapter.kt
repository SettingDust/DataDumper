package settingdust.data_dumper.util

interface MinecraftAdapter {
    companion object : MinecraftAdapter by ServiceLoaderUtil.findService()
}