package settingdust.data_dumper.util

import com.mojang.serialization.DataResult

/**
 * SPI interface for abstracting `DataResult.getOrThrow` across MC versions.
 * The method signature may differ between versions:
 * - 1.20.1: `dataResult.getOrThrow(false) { error -> ... }`
 * - 1.21+: `dataResult.getOrThrow()`
 */
interface DataResultAdapter {
    companion object : DataResultAdapter by ServiceLoaderUtil.findService()

    /**
     * Extracts the success value from a [DataResult], or throws on error.
     * Abstracts over version-specific `DataResult.getOrThrow` signatures.
     *
     * @return the success value
     * @throws IllegalStateException if the result is an error
     */
    fun <T> DataResult<T>.getOrThrow(): T
}
