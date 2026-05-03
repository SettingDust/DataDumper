package settingdust.data_dumper.v21_1.util

import com.mojang.serialization.DataResult
import settingdust.data_dumper.util.DataResultAdapter
import settingdust.data_dumper.util.MinecraftVersion

class DataResultAdapter : DataResultAdapter {
    init {
        MinecraftVersion.V1211.requireCurrent()
    }

    override fun <T> DataResult<T>.getOrThrow(): T {
        return getOrThrow()
    }
}
