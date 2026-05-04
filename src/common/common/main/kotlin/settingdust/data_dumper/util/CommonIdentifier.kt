package settingdust.data_dumper.util

/**
 * Common interface for resource identifiers across MC versions.
 * Injected into MC's `ResourceLocation` (1.20.1/1.21.1) and `Identifier` (1.26.1) via Mixin.
 */
interface CommonIdentifier {
    val namespace: String
    val path: String

    companion object {
        /**
         * Creates a new [CommonIdentifier] by delegating to the version-specific factory.
         */
        fun of(namespace: String, path: String): CommonIdentifier =
            IdentifierAdapter.create(namespace, path)

        /**
         * Parses a string of the form `namespace:path` into a [CommonIdentifier].
         * Delegates to the version-specific [IdentifierAdapter.parse].
         */
        fun parse(value: String): CommonIdentifier =
            IdentifierAdapter.parse(value)
    }
}
