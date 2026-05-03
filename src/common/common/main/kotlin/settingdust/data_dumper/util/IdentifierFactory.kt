package settingdust.data_dumper.util

/**
 * SPI interface for creating [CommonIdentifier] instances.
 * Each MC version provides a factory that delegates to the native
 * `ResourceLocation` / `Identifier` constructor.
 */
interface IdentifierFactory {
    companion object : IdentifierFactory by ServiceLoaderUtil.findService()

    /**
     * Creates a [CommonIdentifier] backed by the version-specific native class.
     *
     * @param namespace the resource namespace
     * @param path the resource path
     * @return a [CommonIdentifier] (which is the native MC class at runtime)
     */
    fun create(namespace: String, path: String): CommonIdentifier

    /**
     * Parses a string of the form `namespace:path` into a [CommonIdentifier].
     * Delegates to the version-specific native parsing method.
     *
     * @param value the string to parse (e.g. `"minecraft:diamond"`)
     * @return a [CommonIdentifier] backed by the native MC class
     */
    fun parse(value: String): CommonIdentifier
}
